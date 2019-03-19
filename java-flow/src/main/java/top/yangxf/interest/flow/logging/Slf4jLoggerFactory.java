package top.yangxf.interest.flow.logging;

import org.slf4j.LoggerFactory;

/**
 * @author yangxf
 */
public class Slf4jLoggerFactory extends SysLoggerFactory {

    private Slf4jLoggerFactory() {
    }

    @Override
    public SysLogger newLogger(String name) {
        return new Slf4jLogger(LoggerFactory.getLogger(name));
    }

    public static SysLoggerFactory getInstance() {
        return Instance.DEFAULT.instance;
    }

    private enum Instance {
        DEFAULT;
        SysLoggerFactory instance;

        Instance() {
            instance = new Slf4jLoggerFactory();
        }
    }
}

