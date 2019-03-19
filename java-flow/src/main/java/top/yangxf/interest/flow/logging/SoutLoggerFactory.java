package top.yangxf.interest.flow.logging;

/**
 * @author yangxf
 */
public class SoutLoggerFactory extends SysLoggerFactory {

    private SoutLoggerFactory() {
    }
    
    @Override
    protected SysLogger newLogger(String name) {
        return new SoutLogger(name);
    }

    public static SysLoggerFactory getInstance() {
        return Instance.DEFAULT.instance;
    }

    private enum Instance {
        DEFAULT;
        SysLoggerFactory instance;

        Instance() {
            instance = new SoutLoggerFactory();
        }
    }
}
