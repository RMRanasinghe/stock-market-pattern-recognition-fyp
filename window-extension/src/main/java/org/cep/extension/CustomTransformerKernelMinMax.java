package org.cep.extension;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.event.in.InEvent;
import org.wso2.siddhi.core.event.in.InListEvent;
import org.wso2.siddhi.core.event.in.InStream;
import org.wso2.siddhi.core.executor.expression.ExpressionExecutor;
import org.wso2.siddhi.core.query.processor.transform.TransformProcessor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.expression.Variable;
import org.wso2.siddhi.query.api.expression.constant.DoubleConstant;
import org.wso2.siddhi.query.api.expression.constant.IntConstant;
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;

@SiddhiExtension(namespace = "custom", function = "kernelMinMax")
public class CustomTransformerKernelMinMax extends TransformProcessor {
	String variable = "";
	int variablePosition = 0;
	double bw = 0;
	int window = 0;
	Queue<InEvent> eventStack = null;
	Queue<Double> priceStack = null;
	Queue<InEvent> uniqueQueue = null;
	Helper helper = null;

	// TODO:uses for debugging. should remove
	int dateVariablePosition = 0;

	public CustomTransformerKernelMinMax() {
	}

	@Override
	protected InStream processEvent(InEvent event) {
		return doProcessing(event);
	}

	@Override
	protected InStream processEvent(InListEvent inListEvent) {
		InListEvent transformedListEvent = new InListEvent();
		for (Event event : inListEvent.getEvents()) {
			if (event instanceof InEvent) {
				transformedListEvent
						.addEvent((Event) processEvent((InEvent) event));
			}
		}
		return transformedListEvent;
	}

	@Override
	protected Object[] currentState() {
		return new Object[] { eventStack };
	}

	@Override
	protected void restoreState(Object[] objects) {
	}

	@Override
	protected void init(Expression[] expressions,
			List<ExpressionExecutor> expressionExecutors,
			StreamDefinition inStreamDefinition,
			StreamDefinition outStreamDefinition, String elementId,
			SiddhiContext siddhiContext) {

		try {
			this.outStreamDefinition = this.inStreamDefinition.name(
					"minMaxStream").attribute("extremaType",
					Attribute.Type.STRING);
		} catch (Exception e) {
			// output stream already defined
		}

		if (expressions.length != 3) {// price variable name, bandwidth, window
			// size
			// error
		}

		variable = ((Variable) expressions[0]).getAttributeName();
		window = ((IntConstant) expressions[2]).getValue();

		try {
			bw = ((DoubleConstant) expressions[1]).getValue();
		} catch (Exception e) {
			bw = ((IntConstant) expressions[1]).getValue();
		}

		helper = new Helper();
		eventStack = new LinkedList<InEvent>();
		priceStack = new LinkedList<Double>();
		uniqueQueue = new LinkedList<InEvent>();
		variablePosition = inStreamDefinition.getAttributePosition(variable);

		// TODO:for debugging. Should remove
		dateVariablePosition = inStreamDefinition.getAttributePosition("date");
	}

	@Override
	public void destroy() {
	}
	
	private synchronized InStream doProcessing(InEvent event){

		Double eventKey = (Double) event.getData(variablePosition);

		if (eventStack.size() < window) {
			eventStack.add(event);
			priceStack.add(eventKey);
		} else {
			eventStack.add(event);
			priceStack.add(eventKey);
			
			Queue<Double> output = helper.smooth(priceStack, bw);
			// TODO:remove hard coded values
			Integer maxPos = helper.findMax(output, 1);
			Integer minPos = helper.findMin(output, 1);
			
			if (maxPos != null) {
				// TODO:remove hard coded values
				Integer maxPosEvnt = helper.findMax(priceStack, window / 5,
						window / 3);

				// TODO:remove following comment - debug purpose.
				// if(maxPosEvnt!=null){
				// log.info(maxPos+"-----------max---------"+maxPosEvnt);
				// log.info(eventStack.toArray()[maxPosEvnt]);
				// }

				if (maxPosEvnt != null && maxPosEvnt - maxPos <= window / 5
						&& maxPos - maxPosEvnt <= window / 2) {// maxPosEvent -
																// 1 due to
																// findmax find
																// one point
																// delay.
					InEvent maximumEvent = (InEvent) eventStack.toArray()[maxPosEvnt];
					if (!uniqueQueue.contains(maximumEvent)) {
						// TODO:remove hard coded values
						if (uniqueQueue.size() > 5) {
							uniqueQueue.remove();
						}
						uniqueQueue.add(maximumEvent);

						// TODO:uses for debugging. Should remove
						// log.info("***max***  Window:"
						// + ((InEvent) eventStack.toArray()[0])
						// .getData(dateVariablePosition)
						// + "-"
						// + ((InEvent) eventStack.toArray()[eventStack
						// .size() - 1])
						// .getData(dateVariablePosition)
						// + "    max pos:"
						// + maximumEvent.getData(dateVariablePosition)
						// + "    max val:"
						// + maximumEvent.getData(variablePosition));
						// log.info(eventStack.toArray()[maxPosEvnt]);

						Object[] inData = maximumEvent.getData();
						Object[] data = new Object[inData.length + 1];

						for (int i = 0; i < inData.length; ++i) {
							data[i] = inData[i];
						}
						data[inData.length] = "max";

						eventStack.remove();
						priceStack.remove();

						return new InEvent(event.getStreamId(),
								System.currentTimeMillis(), data);

					}
				}

			}
			
			else if (minPos != null) {
				// TODO:remove hard coded values
				Integer minPosEvnt = helper.findMin(priceStack, window / 5,
						window / 3);

				// TODO:remove following comment - debug purpose.
				// if(minPosEvnt!=null){
				// log.info(minPos+"-----------min---------"+minPosEvnt);
				// log.info(eventStack.toArray()[minPosEvnt]);
				// }

				if (minPosEvnt != null && minPosEvnt - minPos <= window / 5
						&& minPos - minPosEvnt <= window / 2) {
					InEvent minimumEvent = (InEvent) eventStack.toArray()[minPosEvnt];
					if (!uniqueQueue.contains(minimumEvent)) {
						// TODO:remove hard coded values
						if (uniqueQueue.size() > 5) {
							uniqueQueue.remove();
						}
						uniqueQueue.add(minimumEvent);

						// TODO:uses for debugging. Should remove
						// log.info("***min***  Window:"
						// + ((InEvent) eventStack.toArray()[0])
						// .getData(dateVariablePosition)
						// + "-"
						// + ((InEvent) eventStack.toArray()[eventStack
						// .size() - 1])
						// .getData(dateVariablePosition)
						// + "    min pos:"
						// +
						// minimumEvent.getData(dateVariablePosition)+"    min val:"+minimumEvent.getData(variablePosition));
						// log.info(eventStack.toArray()[minPosEvnt]);

						Object[] inData = minimumEvent.getData();
						Object[] data = new Object[inData.length + 1];

						for (int i = 0; i < inData.length; ++i) {
							data[i] = inData[i];
						}
						data[inData.length] = "min";

						eventStack.remove();
						priceStack.remove();

						return new InEvent(event.getStreamId(),
								System.currentTimeMillis(), data);

					}
				}

			}
			
			eventStack.remove();
			priceStack.remove();

		}

		return null;
	}
}