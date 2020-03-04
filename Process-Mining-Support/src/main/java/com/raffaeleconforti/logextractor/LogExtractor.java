/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.logextractor;

import com.raffaeleconforti.bpmnminer.exception.ExecutionCancelledException;
import com.raffaeleconforti.datastructures.Tree;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Column;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.PrimaryKey;
import com.raffaeleconforti.foreignkeydiscovery.util.EntityPrimaryKeyConverter;
import com.raffaeleconforti.log.util.LogCloner;
import com.raffaeleconforti.log.util.LogOptimizer;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by conforti on 22/10/2014.
 */
public class LogExtractor {

    LogCloner logCloner = new LogCloner();
    private ConcurrentHashMap<PrimaryKey, Map<String, XTrace>> mapPrimaryKeys = null;
    private Map<Object, Object> map = null;
    private XFactory factory = new XFactoryNaiveImpl();
    private XConceptExtension xce = XConceptExtension.instance();
    private EntityPrimaryKeyConverter entityPrimaryKeyConverter = new EntityPrimaryKeyConverter();
    private int numberCores = 0;
    private AtomicInteger logTotalTraces = new AtomicInteger(0);
    private AtomicInteger newLogTotalTraces = new AtomicInteger(0);
    private AtomicInteger logTotalEvents = new AtomicInteger(0);
    private AtomicInteger newLogTotalEvents = new AtomicInteger(0);

    private ConcurrentHashMap<PrimaryKey, Map<Integer, Integer[]>> mapPrimaryKeyValues = null;

    public LogExtractor(LogOptimizer lo) {
        map = lo.getReductionMap();
    }

    /*
    This method assumes that the log has been optimized using LogOptimizer
    */
    public Map<PrimaryKey, XLog> extractLogFromPrimarykeys(XLog log, Set<PrimaryKey> primaryKeys, Tree<Entity> tree, boolean conservative, double noiseThreshold) throws ExecutionCancelledException {

        mapPrimaryKeys = new ConcurrentHashMap<PrimaryKey, Map<String, XTrace>>();
        mapPrimaryKeyValues = new ConcurrentHashMap<PrimaryKey, Map<Integer, Integer[]>>();
        logTotalTraces.set(0);
        newLogTotalTraces.set(0);
        logTotalEvents.set(0);
        newLogTotalEvents.set(0);

        Map<PrimaryKey, XLog> result = new UnifiedMap<PrimaryKey, XLog>();

        boolean[] intermediate = new boolean[1];

        if(conservative) {
            intermediate[0] = false;
            log = cleanLog(log, (Tree<Entity>) tree.clone(), intermediate, noiseThreshold);
        }

        numberCores = Runtime.getRuntime().availableProcessors()*3/4;
        numberCores = (numberCores > 0)?numberCores:1;
        AtomicInteger availableCores = new AtomicInteger(numberCores);
        AtomicInteger remainingThreads = new AtomicInteger(primaryKeys.size());

        for(PrimaryKey primaryKey : primaryKeys) {
            while (availableCores.get() == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            availableCores.decrementAndGet();
            System.out.println("Starting Thread for " + primaryKey.getName());
            LogExtractorThread logExtractorThread = new LogExtractorThread(log, primaryKey, availableCores, remainingThreads, tree, true);
            logExtractorThread.start();
        }

        while (remainingThreads.get() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new ExecutionCancelledException();
            }
        }

        int tot = 0;
        int totEvents = 0;
        for(Map.Entry<PrimaryKey, Map<String, XTrace>> entry : mapPrimaryKeys.entrySet()) {

            XLog newLog = factory.createLog(log.getAttributes());

            for(Map.Entry<String, XTrace> entry1 : entry.getValue().entrySet()) {

                XTrace trace = entry1.getValue();
                newLog.add(trace);
                totEvents += trace.size();

            }

            result.put(entry.getKey(), newLog);
            tot += newLog.size();
        }

        DecimalFormat df = new DecimalFormat("#.##");
        int removed = (logTotalTraces.get()-newLogTotalTraces.get());
        int removed2 = (logTotalEvents.get()-newLogTotalEvents.get());
        if(logTotalTraces.get() > 0) {
            System.out.println("Removed " + removed + " traces out of " + tot + " (" + df.format(removed / logTotalTraces.get() * 100) + "%)");
        }
        if(logTotalEvents.get() > 0) {
            System.out.println("Removed " + removed2 + " traces out of " + totEvents + " (" + df.format(removed2 / logTotalEvents.get() * 100) + "%)");
        }
        System.out.println(logTotalEvents.get() + " " + newLogTotalEvents.get());

        return result;

    }

    private Map<Integer, Integer[]> discoverGroupValues(PrimaryKey primaryKey, XLog log) {
        Column[] columns = primaryKey.getColumns().toArray(new Column[primaryKey.getColumns().size()]);
        int currPos = 0;
        int count = 0;
        int numberRowsPos = 0;

        Map<Integer, Integer[]> result = new UnifiedMap<Integer, Integer[]>();
        Integer[] pos = new Integer[columns.length];

        for(Column c : primaryKey.getColumns()) {
            numberRowsPos = (numberRowsPos == 0)?c.getColumnValues().getValues().length:c.getColumnValues().getValues().length*numberRowsPos;
        }

        String[] rowNames = new String[columns.length];
        for(int i = 0; i < columns.length; i++) {
            String s = columns[i].getColumnName();
            rowNames[i] = getString(s.substring(s.lastIndexOf("|") + 1));
            pos[i] = 0;
        }


        while(count < numberRowsPos) {
            pos[currPos]++;
            count++;

            while(pos[currPos] > columns[currPos].getColumnValues().getValues().length-1) {
                pos[currPos] = 0;
                currPos++;
                if(currPos < columns.length - 1) {
                    pos[currPos]++;
                }
            }

            currPos = 0;

            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < rowNames.length; i++) {
                sb.append(columns[i].getColumnValues().getValues()[pos[i]]);
                sb.append("-");
            }
            sb.deleteCharAt(sb.length()-1);

            boolean found = false;
            boolean exit = false;
            int foundN = 0;
            for(XTrace t : log) {
                for(XEvent e : t) {
                    for(Map.Entry<String, XAttribute> a : e.getAttributes().entrySet()) {
                        for(int i = 0; i < rowNames.length; i++) {
                            if(rowNames[i] == a.getKey()) {
                                if(columns[i].getColumnValues().getValues()[pos[i]] == getAttribute(a.getValue())) {
                                    foundN++;
                                    break;
                                }else {
                                    exit = true;
                                    break;
                                }
                            }
                            if(exit) break;
                        }
                    }
                    if(exit) break;
                    if(foundN == columns.length) {
                        found = true;
                        break;
                    }
                }
                if(found) break;
            }

            if(found) {
                result.put(count, Arrays.copyOf(pos, pos.length));
            }
        }

        return result;
    }

    private XLog cleanLog(XLog log, Tree<Entity> tree, boolean[] intermediate, double noiseThreshold) {

        XLog cloneLog = logCloner.cloneLog(log);

        int originalNumber = cloneLog.size();

        List<Tree<Entity>.Node<Entity>> leaves;

        while ((leaves = tree.findLeaves()).size() > 0) {

            List<Attribute> attributeListParent = new ArrayList<Attribute>();
            List<String> attributeListChild = new ArrayList<String>();

            Tree<Entity>.Node<Entity> node = leaves.remove(0);

            Tree<Entity>.Node<Entity> parent = node.getParent();
            if (parent != null) {
                attributeListParent = parent.getData().getKeys();
                for(Tree<Entity>.Node<Entity> child : parent.getChildren()) {
                    for(Attribute attributeChild : child.getData().getKeys()) {
                        attributeListChild.add(attributeChild.getName());
                    }
                }
            }

            Iterator<XTrace> traceIterator = cloneLog.iterator();
            while (traceIterator.hasNext()) {
                XTrace trace = traceIterator.next();

                boolean changed = false;
                Set<String>[] valueListParentSet = (attributeListParent.size() > 0) ? new Set[attributeListParent.size()] : null;
                String[] valueListParent = (attributeListParent.size() > 0) ? new String[attributeListParent.size()] : null;
                boolean valueFirstTime[] = (attributeListParent.size() > 0) ? new boolean[valueListParent.length] : null;

                if(attributeListParent.size() > 0) {
                    for (int i = 0; i < valueFirstTime.length; i++) {
                        valueListParentSet[i] = new UnifiedSet<String>();
                        valueFirstTime[i] = true;
                    }
                }

                for (XEvent event : trace) {
                    for (int aPos = 0; aPos < attributeListParent.size(); aPos++) {
                        XAttribute xAttribute = event.getAttributes().get(attributeListParent.get(aPos).getName());
                        if (xAttribute != null) {
                            String valAtt = getAttribute(xAttribute);
                            if (valAtt != null) {
                                if (valueListParent[aPos] != valAtt && !valueListParentSet[aPos].contains(valAtt)) {
                                    boolean remove = false;
                                    for(String c : event.getAttributes().keySet()) {
                                        if(attributeListChild.contains(c)) {
                                            remove = true;
                                            break;
                                        }
                                    }
                                    if(remove) {
                                        if (valueFirstTime[aPos] && valueListParent[aPos] == null) {
                                            valueFirstTime[aPos] = false;
                                        } else
                                        if(valueListParent[aPos] != null) {
                                            changed = true;
                                        }
                                    }
                                    valueListParent[aPos] = valAtt;
                                    valueListParentSet[aPos].add(valAtt);
                                }
                            }
                        }
                        if(changed) {
                            break;
                        }
                    }
                    if (changed) {
                        break;
                    }
                }

                if (changed) {
                    traceIterator.remove();
                }

            }

            tree.removeLeave(node);

        }

        int traces = 0;
        for(XTrace t : cloneLog) {
            traces += t.size();
        }
        int originalTraces = 0;
        for(XTrace t : log) {
            originalTraces += t.size();
        }
        logTotalEvents.getAndAdd(originalTraces);
        newLogTotalEvents.getAndAdd(traces);


        DecimalFormat df = new DecimalFormat("#.##");
        double removed = (originalNumber-cloneLog.size());
        double removed2 = (originalTraces-traces);
        System.out.println("Removed " + removed + " traces out of " + originalNumber + " (" + df.format(removed/originalNumber*100) + "%)");
        System.out.println("Removed " + removed2 + " traces out of " + originalTraces + " (" + df.format(removed/originalNumber*100) + "%)");

        if((originalNumber-removed) < originalNumber*noiseThreshold) {
            System.out.println("Too much noise, impossible to use conservative approach. Using heuristic to remove partial traces");
            intermediate[0] = true;
            logTotalEvents.set(0);
            newLogTotalEvents.set(0);
            return log;
        }

        return cloneLog;

    }

    private String getAttribute(XAttribute value) {
        if(value instanceof XAttributeLiteral) return getString(((XAttributeLiteral) value).getValue());
        else if(value instanceof XAttributeBoolean) return getString(Boolean.toString(((XAttributeBoolean) value).getValue()));
        else if(value instanceof XAttributeDiscrete) return getString(Long.toString(((XAttributeDiscrete) value).getValue()));
        else if(value instanceof XAttributeContinuous) return getString(Double.toString(((XAttributeContinuous) value).getValue()));
        else if(value instanceof XAttributeTimestamp) return getString(((XAttributeTimestamp) value).getValue().toString());
        else {
            System.out.println("LogExtractor Error getAttribute");
            return null;
        }
    }

    private String getString(String o) {
        String result = null;
        if((result = (String) map.get(o)) == null) {
            map.put(o, o);
            result = o;
        }
        return result;
    }

    class LogExtractorThread extends Thread {

        XLog log = null;
        PrimaryKey primaryKey = null;
        AtomicInteger availableCores = null;
        AtomicInteger remainingThreads = null;
        Tree<Entity> tree = null;
        boolean intermediate = false;

        public LogExtractorThread(XLog log, PrimaryKey primaryKey, AtomicInteger availableCores, AtomicInteger remainingThreads, Tree<Entity> tree, boolean intermediate) {
            this.log = log;
            this.primaryKey = primaryKey;
            this.availableCores = availableCores;
            this.remainingThreads = remainingThreads;
            this.tree = tree;
            this.intermediate = intermediate;
        }

        @Override
        public void run() {

            if(primaryKey.getColumns().size() > 1) {
                mapPrimaryKeyValues.put(primaryKey, discoverGroupValues(primaryKey, log));
            }

            ConcurrentHashMap<String, XTrace> mapTraces = new ConcurrentHashMap<String, XTrace>();

            TreeSet<Column> columns = new TreeSet<Column>();
            for(Column c : primaryKey.getColumns()) {
                Column newC = new Column(c.getColumnName().substring(c.getColumnName().lastIndexOf("|")+1), c.getColumnValues(), c.getTable());
                columns.add(newC);
            }

            primaryKey = new PrimaryKey(columns);
            if(mapPrimaryKeys.containsKey(primaryKey)) {
                availableCores.incrementAndGet();
                remainingThreads.decrementAndGet();
                return;
            }
            mapPrimaryKeys.put(primaryKey, mapTraces);

            int numberRows = 0;
            for(Column c : primaryKey.getColumns()) {
                numberRows = (numberRows == 0)?c.getColumnValues().getValues().length:c.getColumnValues().getValues().length*numberRows;
            }
            String[] rowNames = new String[primaryKey.getColumnsSize()];


            LogExtractorFunction logExtractorFunction = new LogExtractorFunction();
            for(int numberRowsPos = 0; numberRowsPos < numberRows; numberRowsPos++) {

                if (remainingThreads.get() < numberCores && availableCores.get() > 0) {

                    availableCores.decrementAndGet();
                    LogExtractorSubThread logExtractorSubThread = new LogExtractorSubThread(log, primaryKey, availableCores, tree, intermediate, numberRowsPos, rowNames, mapTraces);
                    logExtractorSubThread.start();

                } else {

                    logExtractorFunction.execute(primaryKey, numberRowsPos, rowNames, intermediate, tree, log, mapTraces, null);

                }

            }

            availableCores.incrementAndGet();
            remainingThreads.decrementAndGet();
            System.out.println("Stoping Thread for " + primaryKey.getName());

        }

    }

    class LogExtractorSubThread extends Thread {

        XLog log = null;
        PrimaryKey primaryKey = null;
        AtomicInteger availableCores = null;
        Tree<Entity> tree = null;
        int numberRowsPos = 0;
        boolean intermediate = false;
        String[] rowNames = null;
        ConcurrentHashMap<String, XTrace> mapTraces = null;

        public LogExtractorSubThread(XLog log, PrimaryKey primaryKey, AtomicInteger availableCores, Tree<Entity> tree, boolean intermediate, int numberRowsPos, String[] rowNames, ConcurrentHashMap<String, XTrace> mapTraces) {
            this.log = log;
            this.primaryKey = primaryKey;
            this.availableCores = availableCores;
            this.tree = tree;
            this.numberRowsPos = numberRowsPos;
            this.intermediate = intermediate;
            this.rowNames = rowNames;
            this.mapTraces = mapTraces;
        }

        @Override
        public void run() {
            LogExtractorFunction logExtractorFunction = new LogExtractorFunction();
            logExtractorFunction.execute(primaryKey, numberRowsPos, rowNames, intermediate, tree, log, mapTraces, availableCores);
        }

    }

    class LogExtractorFunction {

        public void execute(PrimaryKey primaryKey, int numberRowsPos, String[] rowNames, boolean intermediate, Tree<Entity> tree, XLog log,
                            ConcurrentHashMap<String, XTrace> mapTraces, AtomicInteger availableCores) {
            Entity entity = entityPrimaryKeyConverter.getEntity(primaryKey);
            String[] rowValues = new String[primaryKey.getColumnsSize()];
            StringBuilder sb = new StringBuilder();

            Integer[] pos = new Integer[rowNames.length];
            Column[] columns = primaryKey.getColumns().toArray(new Column[primaryKey.getColumns().size()]);

            if(primaryKey.getColumns().size() == 1) {
                pos[0] = numberRowsPos;
            }else {
                pos = mapPrimaryKeyValues.get(primaryKey).get(numberRowsPos);
                if(pos == null) return;
            }

            for(int i = 0; i < columns.length; i++) {
                rowValues[i] = columns[i].getColumnValues().getValues()[pos[i]];
                sb.append(rowValues[i]);
                if (i < rowValues.length - 1) {
                    sb.append("-");
                }
                String s = columns[i].getColumnName();
                rowNames[i] = getString(s.substring(s.lastIndexOf("|") + 1));
            }

            String rowToString = sb.toString();

            List<Attribute> attributeList = new ArrayList<Attribute>();
            List<String> attributeListChild = new ArrayList<String>();

            if(intermediate) {

                Tree<Entity>.Node<Entity> node = tree.findNode(entity);

                Tree<Entity>.Node<Entity> parent = node.getParent();
                if (parent != null) {
                    attributeList = parent.getData().getKeys();
                    for(Tree<Entity>.Node<Entity> child : parent.getChildren()) {
                        for(Attribute attributeChild : child.getData().getKeys()) {
                            attributeListChild.add(attributeChild.getName());
                        }
                    }
                }
            }

            boolean[] valueFirstTime = new boolean[attributeList.size()];

            int newLogSize = 0;
            int originalNewLogSize = 0;
            int newLogEventSize = 0;
            int originalNewLogEventSize = 0;

            for (XTrace trace : log) {

                for(int k = 0; k < valueFirstTime.length; k++) {
                    valueFirstTime[k] = true;
                }

                boolean addTrace = true;
                XTrace newTrace = factory.createTrace();

                String[] valueList = (attributeList.size() > 0)?new String[attributeList.size()]:null;

                for (int e = 0; e < trace.size(); e++) {
                    XEvent event = trace.get(e);

                    boolean found = true;
                    boolean changed = false;
                    for(int aPos = 0; aPos < attributeList.size(); aPos++) {
                        XAttribute xAttribute = event.getAttributes().get(attributeList.get(aPos).getName());
                        if(xAttribute != null) {
                            String valAtt = getAttribute(xAttribute);
                            if (valAtt != null && (valueList[aPos] == null || valueList[aPos] != valAtt)) {
                                boolean remove = false;
                                for(String c : event.getAttributes().keySet()) {
                                    if(attributeListChild.contains(c)) {
                                        remove = true;
                                        break;
                                    }
                                }
                                if(remove) {
                                    if (valueFirstTime[aPos] && valueList[aPos] == null) {
                                        valueFirstTime[aPos] = false;
                                        boolean correctChange = true;
                                        for (Attribute a : entity.getKeys()) {
                                            if (event.getAttributes().containsKey(a.getName())) {
                                                boolean eventSubProcess = false;
                                                for (int e1 = e + 1; e1 < trace.size(); e1++) {
                                                    XEvent event1 = trace.get(e1);
                                                    if (event1.getAttributes().size() < event.getAttributes().size()) {
                                                        if (event1.getAttributes().containsKey(attributeList.get(aPos).getName())) {
                                                            eventSubProcess = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (!eventSubProcess) {
                                                    correctChange = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!correctChange) {
                                            changed = true;
                                        }
                                    } else {
                                        changed = true;
                                    }
                                }
                                valueList[aPos] = getAttribute(xAttribute);
                            }
                        }
                    }
                    for(int k = 0; k < rowNames.length; k++) {
                        boolean secondFound = false;

                        XAttribute a = event.getAttributes().get(rowNames[k]);
                        if (a != null && rowValues[k] == getAttribute(a)) {
                            secondFound = true;
                        }

                        if(!secondFound) {
                            found = false;
                            break;
                        }

                    }

                    if(found) {
                        if(changed) {
                            addTrace = false;
                        }else {
                            XEvent newEvent = factory.createEvent(event.getAttributes());
                            newTrace.add(newEvent);

                        }
                    }

                    if(!addTrace) {
                        break;
                    }
                }

                if (newTrace.size() > 0) {
                    if(addTrace) {
                        originalNewLogSize++;
                        originalNewLogEventSize += trace.size();
                        xce.assignName(newTrace, rowToString);
                        for (int k = 0; k < rowNames.length; k++) {
                            XAttribute attribute = factory.createAttributeLiteral(rowNames[k], rowValues[k], null);
                            newTrace.getAttributes().put(rowNames[k], attribute);
                        }
                        newLogSize++;
                        newLogEventSize += newTrace.size();
                        mapTraces.put(rowToString, newTrace);
                    }else {
                        originalNewLogSize++;
                        originalNewLogEventSize += trace.size();
                    }
                }


            }

            logTotalTraces.getAndAdd(originalNewLogSize);
            logTotalEvents.getAndAdd(originalNewLogEventSize);
            newLogTotalTraces.getAndAdd(newLogSize);
            newLogTotalEvents.getAndAdd(newLogEventSize);

            if(availableCores != null) availableCores.incrementAndGet();

        }
    }

}
