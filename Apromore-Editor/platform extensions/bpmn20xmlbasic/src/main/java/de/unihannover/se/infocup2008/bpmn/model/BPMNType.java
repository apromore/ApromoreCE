/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.unihannover.se.infocup2008.bpmn.model;

/**
 * This class holds all constants from the oryx erdf and provides some methods
 * to check types.
 * TODO adapt this for JSON
 *
 * @author Team Royal Fawn
 */
public class BPMNType {
    public final static String PREFIX = "http://b3mn.org/stencilset/bpmn1.1#";

    // Canvas
    public final static String BPMNDiagram = PREFIX + "BPMNDiagram";

    public static boolean isADiagram(String type) {
        return type.equals(BPMNDiagram);
    }

    // Activities
    public final static String Task = PREFIX + "Task";
    public final static String CollapsedSubprocess = PREFIX + "CollapsedSubprocess";
    public final static String Subprocess = PREFIX + "Subprocess";

    public static boolean isAActivity(String type) {
        return type.equals(Task) || type.equals(CollapsedSubprocess)
                || type.equals(Subprocess);
    }

    // Artifacts
    public final static String Group = PREFIX + "Group";
    public final static String TextAnnotation = PREFIX + "TextAnnotation";
    public final static String DataObject = PREFIX + "DataObject";

    public static boolean isAArtifact(String type) {
        return type.equals(Group) || type.equals(TextAnnotation)
                || type.equals(DataObject);
    }

    // Oryx Throwing Intermediate Events
    public final static String IntermediateMessageEventThrowing = PREFIX + "IntermediateMessageEventThrowing";
    public final static String IntermediateSignalEventThrowing = PREFIX + "IntermediateSignalEventThrowing";
    public final static String IntermediateLinkEventThrowing = PREFIX + "IntermediateLinkEventThrowing";
    public final static String IntermediateMultipleEventThrowing = PREFIX + "IntermediateMultipleEventThrowing";
    public final static String IntermediateCompensationEventThrowing = PREFIX + "IntermediateCompensationEventThrowing";

    public static boolean isAThrowingIntermediateEvent(String type) {
        return type.equals(IntermediateMessageEventThrowing)
                || type.equals(IntermediateSignalEventThrowing)
                || type.equals(IntermediateLinkEventThrowing)
                || type.equals(IntermediateMultipleEventThrowing)
                || type.equals(IntermediateCompensationEventThrowing);
    }

    // Catching Intermediate Events
    public final static String IntermediateEvent = PREFIX + "IntermediateEvent";
    public final static String IntermediateMessageEventCatching = PREFIX + "IntermediateMessageEventCatching";
    public final static String IntermediateTimerEvent = PREFIX + "IntermediateTimerEvent";
    public final static String IntermediateErrorEvent = PREFIX + "IntermediateErrorEvent";
    public final static String IntermediateCancelEvent = PREFIX + "IntermediateCancelEvent";
    public final static String IntermediateCompensationEventCatching = PREFIX + "IntermediateCompensationEventCatching";
    public final static String IntermediateConditionalEvent = PREFIX + "IntermediateConditionalEvent";
    public final static String IntermediateSignalEventCatching = PREFIX + "IntermediateSignalEventCatching";
    public final static String IntermediateMultipleEventCatching = PREFIX + "IntermediateMultipleEventCatching";
    public final static String IntermediateLinkEventCatching = PREFIX + "IntermediateLinkEventCatching";

    public static boolean isACatchingIntermediateEvent(String type) {
        return type.equals(IntermediateEvent)
                || type.equals(IntermediateMessageEventCatching)
                || type.equals(IntermediateTimerEvent)
                || type.equals(IntermediateErrorEvent)
                || type.equals(IntermediateCancelEvent)
                || type.equals(IntermediateCompensationEventCatching)
                || type.equals(IntermediateConditionalEvent)
                || type.equals(IntermediateSignalEventCatching)
                || type.equals(IntermediateMultipleEventCatching)
                || type.equals(IntermediateLinkEventCatching);
    }

    // Connecting Elements
    public final static String SequenceFlow = PREFIX + "SequenceFlow";
    public final static String MessageFlow = PREFIX + "MessageFlow";
    public final static String Association_Undirected = PREFIX + "Association_Undirected";
    public final static String Association_Unidirectional = PREFIX + "Association_Unidirectional";
    public final static String Association_Bidirectional = PREFIX + "Association_Bidirectional";

    public static boolean isAConnectingElement(String type) {
        return type.equals(SequenceFlow) || type.equals(MessageFlow)
                || type.equals(Association_Undirected)
                || type.equals(Association_Unidirectional)
                || type.equals(Association_Bidirectional);
    }

    // End Events
    public final static String EndEvent = PREFIX + "EndEvent";
    public final static String EndMessageEvent = PREFIX + "EndMessageEvent";
    public final static String EndErrorEvent = PREFIX + "EndErrorEvent";
    public final static String EndCancelEvent = PREFIX + "EndCancelEvent";
    public final static String EndCompensationEvent = PREFIX + "EndCompensationEvent";
    public final static String EndSignalEvent = PREFIX + "EndSignalEvent";
    public final static String EndMultipleEvent = PREFIX + "EndMultipleEvent";
    public final static String EndTerminateEvent = PREFIX + "EndTerminateEvent";

    public static boolean isAEndEvent(String type) {
        return type.equals(EndEvent) || type.equals(EndMessageEvent)
                || type.equals(EndErrorEvent) || type.equals(EndCancelEvent)
                || type.equals(EndCompensationEvent)
                || type.equals(EndSignalEvent) || type.equals(EndMultipleEvent)
                || type.equals(EndTerminateEvent);
    }

    // GateWays
    public final static String Exclusive_Databased_Gateway = PREFIX + "Exclusive_Databased_Gateway";
    public final static String Exclusive_Eventbased_Gateway = PREFIX + "Exclusive_Eventbased_Gateway";
    public final static String AND_Gateway = PREFIX + "AND_Gateway";
    public final static String OR_Gateway = PREFIX + "OR_Gateway";
    public final static String Complex_Gateway = PREFIX + "Complex_Gateway";

    public static boolean isAGateWay(String type) {
        return type.equals(Exclusive_Databased_Gateway)
                || type.equals(Exclusive_Eventbased_Gateway)
                || type.equals(AND_Gateway) || type.equals(OR_Gateway)
                || type.equals(Complex_Gateway);
    }

    // Start Events
    public final static String StartEvent = PREFIX + "StartEvent";
    public final static String StartMessageEvent = PREFIX + "StartMessageEvent";
    public final static String StartTimerEvent = PREFIX + "StartTimerEvent";
    public final static String StartConditionalEvent = PREFIX + "StartConditionalEvent";
    public final static String StartSignalEvent = PREFIX + "StartSignalEvent";
    public final static String StartMultipleEvent = PREFIX + "StartMultipleEvent";

    public static boolean isAStartEvent(String type) {
        return type.equals(StartEvent) || type.equals(StartMessageEvent)
                || type.equals(StartTimerEvent)
                || type.equals(StartConditionalEvent)
                || type.equals(StartSignalEvent)
                || type.equals(StartMultipleEvent);
    }

    // Swimlanes
    public final static String Pool = PREFIX + "Pool";
    public final static String Lane = PREFIX + "Lane";
    public final static String CollapsedPool = PREFIX + "CollapsedPool";

    public static boolean isASwimlane(String type) {
        return type.equals(Pool) || type.equals(Lane)
                || type.equals(CollapsedPool);
    }

}
