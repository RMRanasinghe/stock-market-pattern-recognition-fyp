package org.cep.extension;

import org.apache.log4j.Logger;
import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.exception.QueryCreationException;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;
 
@SiddhiExtension(namespace = "custom", function = "min")
public class CustomFunctionMin extends FunctionExecutor {
    Logger log = Logger.getLogger(CustomFunctionMax.class);
    Attribute.Type returnType;
    
    /**
     * Method will be called when initialising the custom function
     *
     * @param types
     * @param siddhiContext
     */
    @Override
    public void init(Attribute.Type[] types, SiddhiContext siddhiContext) {
        for (Attribute.Type attributeType : types) {
            if (attributeType == Attribute.Type.DOUBLE) {
                returnType = attributeType;
                break;
            } else  {
                throw new QueryCreationException("Parameters are not of type Double");
            } 
        }
    }
    
    /**
     * Method called when sending events to process
     * return minimum of arbitrary long set of Double valued
     * @param obj
     * @return min
     */
    @Override
    protected Object process(Object obj) {	 
            Double min = Double.MAX_VALUE;
            if (obj instanceof Object[]) {
                for (Object aObj : (Object[]) obj) {
                	Double value = Double.parseDouble(String.valueOf(aObj));
                	if(value<min){
                		min=value;
                	}
                }
            }
            return min; 
    }
   
    @Override
    public void destroy() {
    }
    /**
     * Return type of the custom function mentioned
     *
     * @return
     */
    
    @Override
    public Attribute.Type getReturnType() {
        return returnType;
    }
 
}