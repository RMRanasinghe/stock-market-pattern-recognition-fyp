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
import java.util.Queue;

@SiddhiExtension(namespace = "custom", function = "kernelMax")
public class CustomWindowExtensionKernelMax extends WindowProcessor {

	String variable = "";
	int variablePosition = 0;
	int bw = 0;
	int window = 0;
	Queue<InEvent> eventStack = null;
	Queue<Double> priceStack = null;
	Queue<InEvent> uniqueQueue = null;

	// TODO:uses for debugging. should remove
	int dateVariablePosition = 0;

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
		return new Object[] { eventStack };
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

		if (expressions.length != 3) {// price variable name, bandwidth, window
										// size
			log.error("Parameters count is not matching, There should be two parameters ");
		}
		variable = ((Variable) expressions[0]).getAttributeName();
		bw = ((IntConstant) expressions[1]).getValue();
		window = ((IntConstant) expressions[2]).getValue();

		eventStack = new LinkedList<InEvent>();
		priceStack = new LinkedList<Double>();
		uniqueQueue = new LinkedList<InEvent>();
		variablePosition = abstractDefinition.getAttributePosition(variable);

		// TODO:for debugging. Should remove
		dateVariablePosition = abstractDefinition.getAttributePosition("date");

	}

	private void doProcessing(InEvent event) {
		Double eventKey = (Double) event.getData(variablePosition);
		Helper helper = new Helper();

		if (eventStack.size() < window) {
			eventStack.add(event);
			priceStack.add(eventKey);
		} else {
			eventStack.add(event);
			priceStack.add(eventKey);

			Queue<Double> output = helper.smooth(priceStack, bw);
			// TODO:remove hard coded values
			Integer maxPos = helper.findMax(output, 1);
			if (maxPos != null) {
				// TODO:remove hard coded values
				Integer maxPosEvnt = helper.findMax(priceStack, window/5,window/3);
				if (maxPosEvnt != null && maxPosEvnt - maxPos <= window/10 && (maxPos-maxPosEvnt) <= (window/2)) {// maxPosEvent - 1 due to findmax find one point delay.
					InEvent maximumEvent = (InEvent) eventStack.toArray()[maxPosEvnt];
					if (!uniqueQueue.contains(maximumEvent)) {
						// TODO:remove hard coded values
						if (uniqueQueue.size() > 5) {
							uniqueQueue.remove();
						}
						uniqueQueue.add(maximumEvent);

						// TODO:uses for debugging. Should remove
						log.info("***max***  Window:"
								+ ((InEvent) eventStack.toArray()[0])
										.getData(dateVariablePosition)
								+ "-"
								+ ((InEvent) eventStack.toArray()[eventStack
										.size() - 1])
										.getData(dateVariablePosition)
								+ "    max pos:"
								+ maximumEvent.getData(dateVariablePosition)
								+ "    max val:"
								+ maximumEvent.getData(variablePosition));

						nextProcessor.process(maximumEvent);

					}
				}

			}
			eventStack.remove();
			priceStack.remove();

		}

	}

	@Override
	public void destroy() {
	}
}
