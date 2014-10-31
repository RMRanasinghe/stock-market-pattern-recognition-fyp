package org.test.cep.extension;

/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.event.in.InEvent;
import org.wso2.siddhi.core.event.in.InListEvent;
import org.wso2.siddhi.core.query.QueryPostProcessingElement;
import org.wso2.siddhi.core.query.processor.window.WindowProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.expression.Variable;
import org.wso2.siddhi.query.api.expression.constant.IntConstant;
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@SiddhiExtension(namespace = "custom", function = "ruleMin")
public class CustomWindowExtensionMin extends WindowProcessor {

	String variable = "";
	int variablePosition = 0;
	int bwl = 0;
	int bwr = 0;
	Map<Object, InEvent> uniqueWindow = null;
	Queue<InEvent> eventStack = null;
	Queue<Double> priceStack = null;

	@Override
	/**
	 *This method called when processing an event
	 */
	protected void processEvent(InEvent inEvent) {
		acquireLock();
		try {
			doProcessing(inEvent);
		} finally

		{
			releaseLock();
		}

	}

	@Override
	/**
	 *This method called when processing an event list
	 */
	protected void processEvent(InListEvent inListEvent) {

		for (int i = 0; i < inListEvent.getActiveEvents(); i++) {
			InEvent inEvent = (InEvent) inListEvent.getEvent(i);
			processEvent(inEvent);
		}
	}

	@Override
	/**
	 * This method iterate through the events which are in window
	 */
	public Iterator<StreamEvent> iterator() {
		return null;
	}

	@Override
	/**
	 * This method iterate through the events which are in window but used in distributed processing
	 */
	public Iterator<StreamEvent> iterator(String s) {
		return null;
	}

	@Override
	/**
	 * This method used to return the current state of the window, Used for persistence of data
	 */
	protected Object[] currentState() {
		return new Object[] { uniqueWindow };
	}

	@Override
	/**
	 * This method is used to restore from the persisted state
	 */
	protected void restoreState(Object[] objects) {
	}

	@Override
	/**
	 * Method called when initialising the extension
	 */
	protected void init(Expression[] expressions,
			QueryPostProcessingElement queryPostProcessingElement,
			AbstractDefinition abstractDefinition, String s, boolean b,
			SiddhiContext siddhiContext) {

		if (expressions.length != 3) {
			log.error("Parameters count is not matching, There should be two parameters ");
		}
		variable = ((Variable) expressions[0]).getAttributeName();
		bwl = ((IntConstant) expressions[1]).getValue();
		bwr = ((IntConstant) expressions[2]).getValue();
		
		eventStack = new LinkedList<InEvent>();
		priceStack = new LinkedList<Double>();
		variablePosition = abstractDefinition.getAttributePosition(variable);
		
	}

	private void doProcessing(InEvent event) {
		Double eventKey = (Double)event.getData(variablePosition);
		Helper helper = new Helper();
		//log.info(eventKey+10000);
		if(eventStack.size()< (bwl+bwr)){
			eventStack.add(event);
			priceStack.add(eventKey);
		}
		else{
			eventStack.add(event);
			priceStack.add(eventKey);
			//TODO:Double equivalence check
			if(helper.min(priceStack)==priceStack.toArray()[bwl]){
				nextProcessor.process((InEvent)eventStack.toArray()[bwl]);
			}
			eventStack.remove();
			priceStack.remove();
			
		}
		//nextProcessor.process(event);

	}

	@Override
	public void destroy() {
	}
}
