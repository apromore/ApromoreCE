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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.raffaeleconforti.log.util;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.kxml2.io.KXmlParser;
import org.processmining.analysis.summary.ExtendedLogSummary;
import org.processmining.framework.log.*;
import org.processmining.framework.log.Process;
import org.processmining.framework.log.classic.AuditTrailEntryClassic;
import org.processmining.framework.log.classic.ProcessInstanceClassic;
import org.processmining.framework.ui.Message;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class LogReaderClassic extends LogReader {
    private LogFilter filter;
    protected int[] processInstancesToKeep;
    private LogFile inputFile;
    private ExtendedLogSummary summary;
    private InputStream in;
    private XmlPullParser parser;
    private boolean initialized;
    private ProcessInstance current;
    private int currentIndex;
    private int nextIndexToKeep;
    private int indexInKeepList;
    private String curProcessName;

    public static LogReader createInstance(LogReader var0, LogFilter var1) {
        return new LogReaderClassic(var1, var0.getFile());
    }

    public static LogReader createInstance(LogFilter var0, LogFile var1) {
        return new LogReaderClassic(var0, var1);
    }

    /** @deprecated */
    public static LogReader createInstance(LogReader var0, int[] var1) throws Exception {
        if(var0 instanceof LogReaderClassic) {
            return new LogReaderClassic(var0, var1);
        } else {
            throw new Exception("New instances can only be based on instances of the same class!");
        }
    }

    protected LogReaderClassic(LogFilter var1, LogFile var2) {
        this.processInstancesToKeep = null;
        this.filter = var1;
        this.inputFile = var2;
        this.initialized = false;
        this.processInstancesToKeep = null;
    }

    /** @deprecated */
    protected LogReaderClassic(LogReader var1, int[] var2) {
        this(var1.getLogFilter(), var1.getFile());
        int[] var3 = var1.processInstancesToKeep();
        this.processInstancesToKeep = new int[var2.length];

        for(int var4 = 0; var4 < this.processInstancesToKeep.length; ++var4) {
            this.processInstancesToKeep[var4] = var3 == null?var2[var4]:var3[var2[var4]];
        }

    }

    public String toString() {
        return "LogReader: " + this.getLogSummary().getNumberOfProcessInstances() + " instances from " + this.inputFile;
    }

    public boolean isSelection() {
        return this.processInstancesToKeep != null;
    }

    private void initialize() {
        if(!this.initialized) {
            Message.add("Please wait while initializing the Log....");
            this.currentIndex = -1;
            this.indexInKeepList = 0;
            this.nextIndexToKeep = this.processInstancesToKeep == null?0:this.processInstancesToKeep[0];
            this.initialized = true;
            this.open();

            try {
                this.parseDocument();
            } catch (IOException var2) {
                this.summary = new ExtendedLogSummary();
                this.summary.setWorkflowLog(new InfoItem("IO Exception occurred", var2.getMessage(), null, null));
            } catch (XmlPullParserException var3) {
                this.summary.setWorkflowLog(new InfoItem("XML Exception occurred", var3.getMessage(), null, null));
            }

            this.reset();
            Message.add("finished initializing.");
        }

    }

    public LogFile getFile() {
        return this.inputFile;
    }

    public LogFilter getLogFilter() {
        return this.filter;
    }

    public LogSummary getLogSummary() {
        this.initialize();
        return this.summary;
    }

    public boolean hasNext() {
        this.initialize();
        return this.current != null;
    }

    public ProcessInstance next() {
        this.initialize();
        ProcessInstance var1 = this.current;
        this.fetchNext();
        return var1;
    }

    public void reset() {
        this.initialize();
        this.close();
        this.open();
        this.currentIndex = -1;
        this.indexInKeepList = 0;
        this.nextIndexToKeep = this.processInstancesToKeep == null?0:this.processInstancesToKeep[0];
        this.fetchNext();
    }

    private void fetchNext() {
        if(this.nextIndexToKeep < 0) {
            this.current = null;
        } else {
            do {
                boolean var1;
                do {
                    this.current = this.parseNextProcessInstance();
                    var1 = this.current != null && (this.filter == null || this.filter.filter(this.current));
                } while(this.current != null && (!var1 || this.current.isEmpty()));

                ++this.currentIndex;
            } while(this.currentIndex != this.nextIndexToKeep);

            if(this.processInstancesToKeep == null) {
                ++this.nextIndexToKeep;
            } else {
                ++this.indexInKeepList;
                if(this.indexInKeepList < this.processInstancesToKeep.length) {
                    this.nextIndexToKeep = this.processInstancesToKeep[this.indexInKeepList];
                } else {
                    this.nextIndexToKeep = -1;
                }
            }

        }
    }

    private void open() {
        try {
            this.in = this.inputFile.getInputStream();
            this.parser = new KXmlParser();
            this.parser.setInput(this.in, null);
        } catch (IOException var2) {
            throw new LogReaderException(var2);
        } catch (XmlPullParserException var3) {
            throw new LogReaderException(var3);
        }
    }

    private void close() {
        if(this.in != null) {
            try {
                this.in.close();
            } catch (IOException var2) {
                throw new LogReaderException(var2);
            }

            this.in = null;
        }

    }

    private boolean atEndTag(String var1) throws XmlPullParserException {
        return this.parser.getEventType() == 3 && var1.equals(this.parser.getName());
    }

    private boolean atStartTag(String var1) throws XmlPullParserException {
        return this.parser.getEventType() == 2 && var1.equals(this.parser.getName());
    }

    private ProcessInstance parseNextProcessInstance() {
        if(this.parser != null && this.in != null) {
            try {
                for(int var1 = this.parser.getEventType(); var1 != 1; var1 = this.parser.next()) {
                    if(this.atStartTag("ProcessInstance")) {
                        return this.parseProcessInstance();
                    }

                    if(this.atStartTag("Process")) {
                        this.curProcessName = this.parser.getAttributeValue(null, "id");
                    }
                }

                this.close();
                return null;
            } catch (IOException var3) {
                this.close();
                throw new LogReaderException(var3);
            } catch (XmlPullParserException var4) {
                this.close();
                throw new LogReaderException(var4);
            }
        } else {
            return null;
        }
    }

    private void parseDocument() throws XmlPullParserException, IOException {
        int var1 = this.parser.getEventType();

        for(this.summary = new ExtendedLogSummary(); var1 != 1; var1 = this.parser.next()) {
            if(this.atStartTag("WorkflowLog")) {
                this.parseWorkflowLog();
                return;
            }
        }

        throw new LogReaderException("invalid workflow log: could not find WorkflowLog tag");
    }

    private void parseWorkflowLog() throws XmlPullParserException, IOException {
        int var1 = this.parser.getEventType();
        String var2 = this.parser.getAttributeValue(null, "description");

        DataSection var3;
        for(var3 = new DataSection(); var1 != 1 && !this.atEndTag("WorkflowLog"); var1 = this.parser.next()) {
            if(this.atStartTag("Data")) {
                this.parseData(var3);
            } else if(this.atStartTag("Source")) {
                this.parseSource();
            } else if(this.atStartTag("Process")) {
                this.parseProcess();
            }
        }

        this.summary.setWorkflowLog(new InfoItem("", var2, var3, null));
    }

    private void parseSource() throws XmlPullParserException, IOException {
        DataSection var1 = new DataSection();
        String var2 = this.parser.getAttributeValue(null, "program");
        var1.put("program", var2 == null?"":var2.trim());

        for(; !this.atEndTag("Source"); this.parser.next()) {
            if(this.atStartTag("Data")) {
                this.parseData(var1);
            }
        }

        this.summary.setSource(new InfoItem("", "", var1, null));
    }

    private void parseProcess() throws XmlPullParserException, IOException {
        int var1 = this.parser.getEventType();
        String var2 = this.parser.getAttributeValue(null, "description");
        DataSection var3 = new DataSection();
        boolean var4 = false;

        for(this.curProcessName = this.parser.getAttributeValue(null, "id"); var1 != 1 && !this.atEndTag("Process"); var1 = this.parser.next()) {
            if(this.atStartTag("Data")) {
                this.parseData(var3);
            } else if(this.atStartTag("ProcessInstance")) {
                if(!var4) {
                    this.summary.addProcess(new InfoItem(this.curProcessName, var2, var3, null));
                    var4 = true;
                }

                ProcessInstance var5 = this.parseProcessInstance();
                if(var5 != null && (this.filter == null || this.filter.filter(var5)) && !var5.isEmpty()) {
                    ++this.currentIndex;
                    if(this.processInstancesToKeep == null || this.currentIndex == this.nextIndexToKeep) {
                        this.summary.addProcessInstance(var5);
                    }

                    if(this.processInstancesToKeep != null && this.currentIndex == this.nextIndexToKeep) {
                        ++this.indexInKeepList;
                        if(this.indexInKeepList < this.processInstancesToKeep.length) {
                            this.nextIndexToKeep = this.processInstancesToKeep[this.indexInKeepList];
                        } else {
                            this.nextIndexToKeep = -1;
                        }
                    }
                }
            }
        }

    }

    private ProcessInstance parseProcessInstance() throws XmlPullParserException, IOException {
        String var1 = this.parser.getAttributeValue(null, "id");
        String var2 = this.parser.getAttributeValue(null, "description");
        Map var3 = new UnifiedMap();

        ArrayList var4;
        for(var4 = new ArrayList(); !this.atEndTag("ProcessInstance"); this.parser.next()) {
            if(this.atStartTag("Data")) {
                this.parseData(var3);
            } else if(this.atStartTag("AuditTrailEntry")) {
                this.insertSorted(var4, this.parseAuditTrailEntry());
            }
        }

        return new ProcessInstanceClassic(this.curProcessName, var1, var2, var3, var4);
    }

    private void parseData(Map var1) throws XmlPullParserException, IOException {
        for(; !this.atEndTag("Data"); this.parser.next()) {
            if(this.atStartTag("Attribute")) {
                String var2 = this.parser.getAttributeValue(null, "name");
                String var3 = this.parser.nextText();
                var1.put(var2 == null?"":var2.trim(), var3 == null?"":var3.trim());
            }
        }

    }

    private AuditTrailEntry parseAuditTrailEntry() throws XmlPullParserException, IOException {
        String var1 = "";
        String var2 = "";
        String var3 = "";
        String var4 = "";

        UnifiedMap var5;
        for(var5 = new UnifiedMap(); !this.atEndTag("AuditTrailEntry"); this.parser.next()) {
            if(this.atStartTag("Data")) {
                this.parseData(var5);
            } else if(this.atStartTag("WorkflowModelElement")) {
                var1 = this.parser.nextText();
            } else if(this.atStartTag("EventType")) {
                String var6 = this.parser.getAttributeValue(null, "unknowntype");
                var2 = this.parser.nextText();
                var2 = var2 == null ? "" : var2.trim();
                if(var2.equals("unknown")) {
                    if(var6 == null) {
                        var6 = "complete";
                    }
                    var2 = var2 + ":" + var6.trim();
                }
            } else if(this.atStartTag("Timestamp")) {
                var3 = this.parser.nextText();
            } else if(this.atStartTag("Originator")) {
                var4 = this.parser.nextText();
            }
        }

        return new AuditTrailEntryClassic(var1, var2, var3, var4, var5);
    }

    private void insertSorted(ArrayList var1, AuditTrailEntry var2) {
        if(var2.getTimestamp() == null) {
            var1.add(var2);
        } else {
            for(int var3 = 0; var3 < var1.size(); ++var3) {
                AuditTrailEntry var4 = (AuditTrailEntry)var1.get(var3);
                if(var4.getTimestamp() != null && var4.getTimestamp().compareTo(var2.getTimestamp()) > 0) {
                    var1.add(var3, var2);
                    return;
                }
            }

            var1.add(var2);
        }
    }

    public int[] processInstancesToKeep() {
        return this.processInstancesToKeep;
    }

    public int numberOfInstances() {
        return this.summary.getNumberOfProcessInstances();
    }

    public ProcessInstance getInstance(int var1) {
        ProcessInstance var2 = null;
        if(0 <= var1 && var1 < this.numberOfInstances()) {
            this.reset();

            for(int var3 = 0; var3 <= var1; ++var3) {
                var2 = this.next();
            }

            return var2;
        } else {
            return null;
        }
    }

    public LogReader clone(int[] var1) {
        return new LogReaderClassic(this, var1);
    }

    public Process getProcess(int var1) {
        return null;
    }

    public Iterator instanceIterator() {
        return new LogReaderClassic.LogReaderClassicIterator(this);
    }

    public int numberOfProcesses() {
        return 0;
    }

    public Iterator processIterator() {
        return null;
    }

    public class LogReaderClassicIterator implements Iterator {
        protected LogReaderClassic parent = null;

        public LogReaderClassicIterator(LogReaderClassic var2) {
            this.parent = var2;
            this.parent.reset();
        }

        public boolean hasNext() {
            return this.parent.hasNext();
        }

        public Object next() {
            return this.parent.next();
        }

        public void remove() {
        }
    }
}
