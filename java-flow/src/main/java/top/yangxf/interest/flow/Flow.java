package top.yangxf.interest.flow;

import java.io.Serializable;

/**
 * @author yangxf
 */
public interface Flow extends Serializable {
    /**
     * logger of context
     */
    String LOGGER_CTX_VAR_NAME = "TASK_FLOW_CONTEXT_LOGGER";
    String START_TIME_CTX_VAR_NAME = "TASK_FLOW_CONTEXT_START_TIME";
    String CURRENT_TIME_CTX_VAR_NAME = "TASK_FLOW_CONTEXT_CURRENT_TIME";
    
    void start();
    
    void close();
    
    boolean isClosed();

    String name();
}
