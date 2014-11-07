package org.test.cep.extension;

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

/**
 * @author woranga
 * @date : Nov 06, 2014
 */
@SiddhiExtension(namespace = "custom", function = "kalmanMin")
public class CustomWindowExtensionKalmanMin extends WindowProcessor {

    String variable = "";
    int variablePosition = 0;
    int bw = 0;
    int window = 0;
    Queue<InEvent> eventStack = null;
    Queue<Double> priceStack = null;
    Queue<InEvent> uniqueQueue = null;

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

        if (expressions.length != 3) {//price variable name, bandwidth, window size
            log.error("Parameters count is not matching, There should be two parameters ");
        }
        variable = ((Variable) expressions[0]).getAttributeName();
        //bw = ((IntConstant) expressions[1]).getValue();
        window = ((IntConstant) expressions[1]).getValue();

        eventStack = new LinkedList<InEvent>();
        priceStack = new LinkedList<Double>();
        uniqueQueue = new LinkedList<InEvent>();
        variablePosition = abstractDefinition.getAttributePosition(variable);

    }

    private void doProcessing(InEvent event) {
        Double eventKey = (Double)event.getData(variablePosition);
        Helper helper = new Helper();

        if(eventStack.size()< window){
            eventStack.add(event);
            priceStack.add(eventKey);
        }
        else{
            eventStack.add(event);
            priceStack.add(eventKey);

            Queue<Double> output = helper.kalmanFilter(priceStack);
            //TODO:remove hard coded values
            Integer minPos = helper.findMin(output, 2);
            if(minPos!=null){
                //TODO:remove hard coded values
                Integer minPosEvnt = helper.findMin(priceStack,10);
                if(minPosEvnt!=null){
                    InEvent minimumEvent = (InEvent)eventStack.toArray()[minPosEvnt];
                    if(!uniqueQueue.contains(minimumEvent)){
                        //TODO:remove hard coded values
                        if(uniqueQueue.size()>5){
                            uniqueQueue.remove();
                        }
                        uniqueQueue.add(minimumEvent);
                        log.info(eventStack.toArray()[minPos]);
                        log.info(eventStack.toArray()[0]);
                        log.info(eventStack.toArray()[eventStack.size()-1]);
                        nextProcessor.process(minimumEvent);

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
