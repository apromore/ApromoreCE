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
package de.hpi.bpmn2_0.factory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.loop.ComplexBehaviorDefinition;
import de.hpi.bpmn2_0.model.activity.loop.LoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.MultiInstanceFlowCondition;
import de.hpi.bpmn2_0.model.activity.loop.MultiInstanceLoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.StandardLoopCharacteristics;
import de.hpi.bpmn2_0.model.data_object.DataState;
import de.hpi.bpmn2_0.model.event.EventDefinition;
import de.hpi.bpmn2_0.model.event.ImplicitThrowEvent;
import de.hpi.bpmn2_0.model.misc.IoOption;
import de.hpi.bpmn2_0.model.misc.ItemKind;
import de.hpi.bpmn2_0.model.misc.Property;

/**
 * Common factory for BPMN 2.0 activities
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public abstract class AbstractActivityFactory extends AbstractShapeFactory {

	/**
	 * Sets common attributes of activity (task, subprocess, event-subprocess)
	 * derived from the source shape.
	 * 
	 * @param activity
	 *            The resulting activity.
	 * @param shape
	 *            The source diagram shape.
	 */
	protected void setStandardAttributes(Activity activity, GenericShape shape) {
		this.setCommonAttributes(activity, shape);
		
		/* Handle isCompensation Property */
		this.setCompensationProperty(activity, shape);
		
		/* Loop Characteristics */
		this.createLoopCharacteristics(activity, shape);
		
		/* Properties */
		activity.getProperty().addAll(this.createPropertiesList(shape));
		
		/* Start and Completion Quantity */
		this.setStartAndCompletionQuantity(activity, shape);
		
		/* Collect data for IOSpecification */
		this.collectIoSpecificationInfo(activity, shape);
		
	}

	/**
	 * Takes the complex property input set as well as output set and stores 
	 * them in a hash map for further processing.
	 * @param shape 
	 * @param activity 
	 */
	private void collectIoSpecificationInfo(Activity activity, GenericShape shape) {
		activity.getOutputSetInfo().add(this.collectSetInfoFor(shape, "dataoutputset"));
		activity.getInputSetInfo().add(this.collectSetInfoFor(shape, "datainputset"));
	}
	
	/**
	 * Generic method to parse data input and output set properties.
	 * 
	 * @param property
	 * 		Identifies the shape's property to handle either a output or input set.
	 */
	private HashMap<String, IoOption> collectSetInfoFor(GenericShape shape, String property) {
		String ioSpecString = shape.getProperty(property);
		
		HashMap<String, IoOption> options = new HashMap<String, IoOption>();
		
		if(ioSpecString != null && !(ioSpecString.length() == 0)) {
			try {
				JSONObject ioSpecObject = new JSONObject(ioSpecString);
				JSONArray ioSpecItems = ioSpecObject.getJSONArray("items");

				/* Retrieve io spec option definitions */
				for (int i = 0; i < ioSpecItems.length(); i++) {
					JSONObject propertyItem = ioSpecItems.getJSONObject(i);

					IoOption ioOpt = new IoOption();

					/* Name */
					String name = propertyItem.getString("name");
					if(name == null || name.length() == 0)
						continue;
					
					/* Optional */
					String isOptional = propertyItem.getString("optional");
					if(isOptional != null && isOptional.equalsIgnoreCase("true"))
						ioOpt.setOptional(true);
					
					/* While executing */
					String whileExecuting = propertyItem.getString("whileexecuting");
					if(whileExecuting != null && whileExecuting.equalsIgnoreCase("true"))
						ioOpt.setOptional(true);
					
					options.put(name, ioOpt);
					
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return options;
	}

	/**
	 * Process the compensation property.
	 * 
	 * @param activity
	 * @param shape
	 */
	private void setCompensationProperty(Activity activity, GenericShape shape) {
		activity
				.setIsForCompensation((shape.getProperty("isforcompensation") != null ? shape
						.getProperty("isforcompensation").equalsIgnoreCase(
								"true")
						: false));
	}
	

	/**
	 * Sets the start quantity of the activity based on the data of the shape.
	 * 
	 * @param activity
	 * @param shape
	 * 		The resource shape
	 */
	private void setStartAndCompletionQuantity(Activity activity, GenericShape shape) {
		
		/* Start quantity */
		
		String startQuantity = shape.getProperty("startquantity");
		if(startQuantity != null) {
			try {
				activity.setStartQuantity(BigInteger.valueOf(Integer.valueOf(startQuantity)));
			} catch(Exception e) {
				e.printStackTrace();
				/* Set to default value in case of an exception */
				activity.setStartQuantity(BigInteger.valueOf(1));
			}
			
		}
		
		/* Completion quantity */
		String completionQuantity = shape.getProperty("completionquantity");
		if(completionQuantity != null) {
			try {
				activity.setCompletionQuantity(BigInteger.valueOf(Integer.valueOf(completionQuantity)));
			} catch(Exception e) {
				/* Set to default value in case of an exception */
				e.printStackTrace();
				activity.setCompletionQuantity(BigInteger.valueOf(1));
			}
		}
	}
	
	/**
	 * Entry method to create the {@link LoopCharacteristics} for a given 
	 * activity's shape.
	 * 
	 * @param activity
	 * @param shape
	 */
	protected void createLoopCharacteristics(Activity activity, GenericShape shape) {

		/* Distinguish between standard and multiple instance loop types */
		String loopType = shape.getProperty("looptype");
		if (loopType != null && !(loopType.length() == 0)) {

			/* Loop type standard */
			if (loopType.equalsIgnoreCase("Standard")) 
				activity.setLoopCharacteristics(createStandardLoopCharacteristics(shape));
			
			/* Loop type multiple instances */
			else if (loopType.equalsIgnoreCase("Parallel")
					|| loopType.equalsIgnoreCase("Sequential")) {
				activity.setLoopCharacteristics(createMultiInstanceLoopCharacteristics(shape, loopType));
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param shape
	 */
	protected List<Property> createPropertiesList(GenericShape shape) {
		ArrayList<Property> propertiesList = new ArrayList<Property>();
		
		String propertiesString = shape.getProperty("properties");
		if(propertiesString != null && !(propertiesString.length() == 0)) {
			try {
				JSONObject propertyObject = new JSONObject(propertiesString);
				JSONArray propertyItems = propertyObject.getJSONArray("items");

				/*
				 * Retrieve property definitions and process
				 * them.
				 */
				for (int i = 0; i < propertyItems.length(); i++) {
					JSONObject propertyItem = propertyItems.getJSONObject(i);

					Property property = new Property();

					/* Name */
					String name = propertyItem.getString("name");
					if(name != null && !(name.length() == 0))
						property.setName(name);
					
					/* Data State */
					String dataState = propertyItem.getString("datastate");
					if(dataState != null && !(dataState.length() == 0))
						property.setDataState(new DataState(dataState));
					
					/* ItemKind */
					String itemKind = propertyItem.getString("itemkind");
					if(itemKind != null && !(itemKind.length() == 0))
						property.setItemKind(ItemKind.fromValue(itemKind));
					
					/* Structure */
					String structureString = propertyItem.getString("structure");
					if(structureString != null && !(structureString.length() == 0))
						property.setStructure(structureString);
					
					/* isCollection */
					String isCollection = propertyItem.getString("iscollection");
					if(isCollection != null && isCollection.equalsIgnoreCase("false"))
						property.setCollection(false);
					else 
						property.setCollection(true);
					
					propertiesList.add(property);
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return propertiesList;
	}

	/**
	 * Creates the loop characteristics for multiple instances loops.
	 * 
	 * @param shape
	 * @param loopType
	 */
	private MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(GenericShape shape,
			String loopType) {
		MultiInstanceLoopCharacteristics miLoop = new MultiInstanceLoopCharacteristics();

		/* Determine whether it is parallel or sequential */
		if (loopType.equalsIgnoreCase("Parallel"))
			miLoop.setIsSequential(false);
		else
			miLoop.setIsSequential(true);

		/* Set loop cardinality */
		String loopCardinalityString = shape
				.getProperty("loopcardinality");
		if (loopCardinalityString != null && !(loopCardinalityString.length() == 0)) {
			FormalExpression loopCardinality = new FormalExpression(
					loopCardinalityString);
			miLoop.setLoopCardinality(loopCardinality);
		}

		/* Reference required DataInput */
		// miLoop.setLoopDataInput(value)
		// Task t = null;
		// t.get

		/* Completion condition */
		String completionCondition = shape
				.getProperty("completioncondition");
		if (completionCondition != null
				&& !(completionCondition.length() == 0)) {
			FormalExpression completionConditionExpr = new FormalExpression(
					completionCondition);
			miLoop.setCompletionCondition(completionConditionExpr);
		}

		/* Handle loop behavior */
		handleLoopBehaviorAttributes(shape, miLoop);
		
		return miLoop;
	}

	/**
	 * Processes the attributes that are related to the loop behavior. 
	 * 
	 * @param shape
	 * @param miLoop
	 */
	private void handleLoopBehaviorAttributes(GenericShape shape,
			MultiInstanceLoopCharacteristics miLoop) {
		String behavior = shape.getProperty("behavior");
		if (behavior != null && !(behavior.length() == 0)) {
			miLoop.setBehavior(MultiInstanceFlowCondition
					.fromValue(behavior));
		}

		/* Complex behavior */
		if (miLoop.getBehavior().equals(
				MultiInstanceFlowCondition.COMPLEX)) {
			try {
				String comBehavDefString = shape
						.getProperty("complexbehaviordefinition");
				JSONObject complexDef = new JSONObject(
						comBehavDefString);
				JSONArray complexDefItems = complexDef
						.getJSONArray("items");

				/*
				 * Retrieve complex behavior definitions and process
				 * them.
				 */
				for (int i = 0; i < complexDefItems.length(); i++) {
					JSONObject complexDefItem = complexDefItems
							.getJSONObject(i);

					ComplexBehaviorDefinition comBehavDef = new ComplexBehaviorDefinition();

					/* Condition */
					String condition = complexDefItem
							.getString("cexpression");
					if (condition != null && !(condition.length() == 0))
						comBehavDef.setCondition(new FormalExpression(
								condition));

					/* Event */
					ImplicitThrowEvent event = new ImplicitThrowEvent(
							complexDefItem.getString("ceventdefinition"));
					comBehavDef.setEvent(event);

					miLoop.getComplexBehaviorDefinition().add(
							comBehavDef);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			/* Handle none behavior choice */
		} else if (miLoop.getBehavior().equals(
				MultiInstanceFlowCondition.NONE)) {
			String noneBehavString = shape
					.getProperty("nonebehavioreventref");
			if (noneBehavString != null && !(noneBehavString.length() == 0)) {
				miLoop.setNoneBehaviorEventRef(EventDefinition
						.createEventDefinition(noneBehavString));
			}
			/* Handle one behavior choice */
		} else if (miLoop.getBehavior().equals(
				MultiInstanceFlowCondition.ONE)) {
			String oneBehavString = shape
					.getProperty("onebehavioreventref");
			if (oneBehavString != null && !(oneBehavString.length() == 0)) {
				miLoop.setOneBehaviorEventRef(EventDefinition
						.createEventDefinition(oneBehavString));
			}
		}
	}

	/**
	 * Creates a {@link StandardLoopCharacteristics} object based on the shape's 
	 * properties.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		The {@link StandardLoopCharacteristics}
	 */
	private LoopCharacteristics createStandardLoopCharacteristics(GenericShape shape) {
		StandardLoopCharacteristics standardLoop = new StandardLoopCharacteristics();

		/* Set loop condition */
		String loopConditionString = shape.getProperty("loopcondition");
		if (loopConditionString != null
				&& !(loopConditionString.length() == 0)) {
			FormalExpression loopCondition = new FormalExpression(
					loopConditionString);
			standardLoop.setLoopCondition(loopCondition);
		}

		/* Determine the point in time to check the loop condition */
		String testBeforeString = shape.getProperty("testbefore");
		if (testBeforeString != null
				&& testBeforeString.equalsIgnoreCase("true")) {
			standardLoop.setTestBefore(true);
		} else {
			standardLoop.setTestBefore(false);
		}

		/* Set the maximum number of loop iterations */
		try {
			standardLoop.setLoopMaximum(BigInteger.valueOf(Integer
					.parseInt(shape.getProperty("loopmaximum"))));
		} catch (Exception e) {
			/* In case of an exception do not set a loop iteration cap */
		}

		return standardLoop;
	}

}
