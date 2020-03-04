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

package com.raffaeleconforti.wrappers;

import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 9/11/16.
 */
public class InterruptingMiningAlgorithm {

    private MiningAlgorithm miningAlgorithm;
    private long timeout;

    public InterruptingMiningAlgorithm(MiningAlgorithm miningAlgorithm, long timeout) {
        this.miningAlgorithm = miningAlgorithm;
        this.timeout = timeout;
    }

    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        final PetrinetWithMarking[] petrinetWithMarking = new PetrinetWithMarking[1];

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.setOut(new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) {}
                }));

                try {
                    petrinetWithMarking[0] = miningAlgorithm.minePetrinet(context, log, structure, null, xEventClassifier);
                } catch (Exception e) {

                }

                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            }
        };
        Thread t = new Thread(runnable);
        t.start();

        long time = 0;
        boolean reached = false;
        try {
            while(time < timeout && t.isAlive()) {
                Thread.currentThread().sleep(100);
                if(time % 300000 == 0) {
                    System.out.println("DEBUG - sleeping: " + getAlgorithmName());
                }
                time += 100;
            }
            if (t.isAlive()) {
                t.interrupt();
                reached = true;
            }
            Thread.currentThread().sleep(1000);
            if (t.isAlive()) {
                t.stop();
            }
        } catch (Exception e) {

        }
        if(reached) {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            System.out.println(getAlgorithmName() + " - Timeout Reached!");
        }

        return petrinetWithMarking[0];
    }

    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        return miningAlgorithm.mineBPMNDiagram(context, log, structure, params, xEventClassifier);
    }

    public String getAlgorithmName() {
        return miningAlgorithm.getAlgorithmName();
    }
}
