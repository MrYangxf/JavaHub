package top.yangxf.interest.flow.task.annotation;

import java.lang.annotation.*;

/**
 * @author yangxf
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskParam {

    /**
     * name of parameter
     */
    String value();
}
