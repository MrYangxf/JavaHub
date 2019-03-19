package top.yangxf.interest.flow.logging;

import java.lang.reflect.Method;

import static top.yangxf.interest.util.common.ObjectUtil.nonEmpty;

/**
 * @author yangxf
 */
public abstract class SysLoggerFactory {

    protected abstract SysLogger newLogger(String name);

    public static SysLogger getLogger(String name) {
        return getInstance().newLogger(name);
    }

    public static SysLogger getLogger(Class<?> cls) {
        return getInstance().newLogger(cls.getName());
    }

    private static SysLoggerFactory getInstance() {
        return Instance.DEFAULT.instance;
    }

    private static SysLoggerFactory newFactory() {
        SysLoggerFactory factory;
        String name = SysLoggerFactory.class.getName();

        try {
            String logProp = System.getProperty("sys.log.factory");
            if (nonEmpty(logProp)) {
                Class<?> cls = Class.forName(logProp);
                Method getInstance = cls.getMethod("getInstance");
                factory = (SysLoggerFactory) getInstance.invoke(null);
                factory.newLogger(name).debug("Using {} as logger framework. ", cls.getSimpleName());
                return factory;
            }
        } catch (Throwable t) {
            System.err.println("load SysLoggerFactory by property sys.log.factory fail.");
        }

        try {
            factory = Slf4jLoggerFactory.getInstance();
            factory.newLogger(name).debug("Using Slf4jLogger as logger framework. ");
        } catch (Throwable t1) {
            factory = SoutLoggerFactory.getInstance();
            factory.newLogger(name).debug("Using System.out as logger framework. ");
        }

        return factory;
    }

    private enum Instance {
        DEFAULT;

        private SysLoggerFactory instance;

        Instance() {
            instance = newFactory();
        }
    }
}
