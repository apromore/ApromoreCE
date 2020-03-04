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

package com.raffaeleconforti.log.util;

import com.google.common.collect.ImmutableList;
import com.raffaeleconforti.singletonlog.XFactorySingletonImpl;
import org.apache.commons.cli.*;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.*;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.CSVConversion;
import org.processmining.log.csvimport.config.CSVConversionConfig;
import org.processmining.log.csvimport.exception.CSVConversionConfigException;
import org.processmining.log.csvimport.exception.CSVConversionException;
import org.processmining.log.utils.XUtils;

import java.io.*;
import java.util.Collection;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */

public class LogImporter {

    private static final class ProgressListenerPrintStreamImpl extends CSVConversion.NoOpProgressListenerImpl {

        private final PrintStream out;

        public ProgressListenerPrintStreamImpl(PrintStream out) {
            this.out = out;
        }

        public void log(String message) {
            out.println(message);
        }

    }

    private static final Options OPTIONS = new Options();

    private static final Option HELP = OptionBuilder.withDescription("help").create('h');
    private static final Option LINKEDLIST = OptionBuilder.withDescription("useLinkedList").create("linkedlist");
    private static final Option XES = OptionBuilder.hasArg().withArgName("filename").create("xes");
    private static final Option TRACE = OptionBuilder.hasArg().withArgName("traceColumn").create("trace");
    private static final Option EVENT = OptionBuilder.hasArg().withArgName("eventColumn").create("event");
    private static final Option START = OptionBuilder.hasArg().withArgName("startColumn").create("start");
    private static final Option COMPLETE = OptionBuilder.hasArg().withArgName("completionColumn").create("complete");

    static {
        OPTIONS.addOption(HELP);
        OPTIONS.addOption(LINKEDLIST);
        OPTIONS.addOption(XES);
        OPTIONS.addOption(TRACE);
        OPTIONS.addOption(EVENT);
        OPTIONS.addOption(START);
        OPTIONS.addOption(COMPLETE);
    }

    public static void main(String[] args) {

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine commandLine = parser.parse(OPTIONS, args);

            if (commandLine.hasOption(HELP.getOpt())) {
                printUsage();
                return;
            }

            if (commandLine.getArgs().length != 1) {
                printUsage();
                System.err.println("Missing filename of the CSV file!");
                return;
            }

            File logFile = new File(commandLine.getArgs()[0]);

            try {
                XLog log = parseCSV(logFile, commandLine);

                XUtils.saveLogGzip(log, new File(logFile.getAbsolutePath() + ".xes.gz"));
            } catch (CSVConversionException | IOException e) {
                if (e.getMessage() != null) {
                    System.err.println(e.getMessage());
                }
                e.printStackTrace();
            }

            System.out.println("Log converted successfully!");

        } catch (ParseException e) {
            printUsage();
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            }
        }

        System.exit(0);

    }

    private static XLog parseCSV(File inputFile, CommandLine commandLine) throws CSVConversionException, CSVConversionConfigException {
        CSVConversion conversion = new CSVConversion();
        CSVFile csvFile = new CSVFileReferenceUnivocityImpl(inputFile.toPath());
        CSVConfig importConfig = new CSVConfig(csvFile);
        CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, importConfig);
        conversionConfig.autoDetect();

        if (commandLine.hasOption(LINKEDLIST.getOpt())) {
            conversionConfig.setFactory(new XFactorySingletonImpl(true));
        } else {
            conversionConfig.setFactory(new XFactorySingletonImpl());
        }

        if (commandLine.hasOption(TRACE.getOpt())) {
            conversionConfig.setCaseColumns(ImmutableList.of(commandLine.getOptionValue(TRACE.getOpt())));
        }

        if (commandLine.hasOption(EVENT.getOpt())) {
            conversionConfig.setEventNameColumns(ImmutableList.of(commandLine.getOptionValue(EVENT.getOpt())));
        }

        if (commandLine.hasOption(START.getOpt())) {
            conversionConfig.setStartTimeColumn(commandLine.getOptionValue(START.getOpt()));
        }

        if (commandLine.hasOption(COMPLETE.getOpt())) {
            conversionConfig.setCompletionTimeColumn(commandLine.getOptionValue(COMPLETE.getOpt()));
        }

        CSVConversion.ProgressListener cmdLineProgressListener = new ProgressListenerPrintStreamImpl(System.out);
        CSVConversion.ConversionResult<XLog> result = conversion.doConvertCSVToXES(cmdLineProgressListener, csvFile, importConfig,
                conversionConfig);
        return result.getResult();
    }

    private static void printUsage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("mpe [CSVFILE]", OPTIONS, true);
        return;
    }

    public static XLog importFromFile(XFactory factory, String location) throws Exception {
        if(location.endsWith("mxml.gz")) {
            return importFromInputStream(new FileInputStream(location), new XMxmlGZIPParser(factory));
        }else if(location.endsWith("mxml")) {
            return importFromInputStream(new FileInputStream(location), new XMxmlParser(factory));
        }else if(location.endsWith("xes.gz")) {
            return importFromInputStream(new FileInputStream(location), new XesXmlGZIPParser(factory));
        }else if(location.endsWith("xes")) {
            return importFromInputStream(new FileInputStream(location), new XesXmlParser(factory));
        }
        return null;
    }

    public static void exportToFile(String name, XLog log) {
        if(name.endsWith("mxml.gz")) {
            exportToInputStream(log, name, new XMxmlGZIPSerializer());
        }else if(name.endsWith("mxml")) {
            exportToInputStream(log, name, new XMxmlSerializer());
        }else if(name.endsWith("xes.gz")) {
            exportToInputStream(log, name, new XesXmlGZIPSerializer());
        }else if(name.endsWith("xes")) {
            exportToInputStream(log, name, new XesXmlSerializer());
        }else {
            exportToInputStream(log, name, new XesXmlGZIPSerializer());
        }
    }

    public static void exportToFile(String path, String name, XLog log) {
        if(name.endsWith("mxml.gz")) {
            exportToInputStream(log, path + name, new XMxmlGZIPSerializer());
        }else if(name.endsWith("mxml")) {
            exportToInputStream(log, path + name, new XMxmlSerializer());
        }else if(name.endsWith("xes.gz")) {
            exportToInputStream(log, path + name, new XesXmlGZIPSerializer());
        }else if(name.endsWith("xes")) {
            exportToInputStream(log, path + name, new XesXmlSerializer());
        }else {
            exportToInputStream(log, path + name, new XesXmlGZIPSerializer());
        }
    }

    public static XLog importFromInputStream(InputStream inputStream, XParser parser) throws Exception {
        Collection<XLog> logs;
        try {
            logs = parser.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            logs = null;
        }
        if (logs == null) {
            // try any other parser
            for (XParser p : XParserRegistry.instance().getAvailable()) {
                if (p == parser) {
                    continue;
                }
                try {
                    logs = p.parse(inputStream);
                    if (logs.size() > 0) {
                        break;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    logs = null;
                }
            }
        }

        // log sanity checks;
        // notify user if the log is awkward / does miss crucial information
        if (logs == null || logs.size() == 0) {
            throw new Exception("No processes contained in log!");
        }

        XLog log = logs.iterator().next();
        if (XConceptExtension.instance().extractName(log) == null) {
            XConceptExtension.instance().assignName(log, "Anonymous log imported from ");
        }

        if (log.isEmpty()) {
            throw new Exception("No process instances contained in log!");
        }

        return log;
    }

    public static void exportToInputStream(XLog log, String name, XSerializer serializer) {
        FileOutputStream outputStream;
        try {
            File f = new File(name);
            if(!f.exists()) f.createNewFile();
            outputStream = new FileOutputStream(f);
            serializer.serialize(log, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

}