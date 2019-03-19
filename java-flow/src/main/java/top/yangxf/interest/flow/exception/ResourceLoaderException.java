package top.yangxf.interest.flow.exception;

/**
 * @author yangxf
 */
public class ResourceLoaderException extends RuntimeException {
    public ResourceLoaderException() {
        super();
    }

    public ResourceLoaderException(String message) {
        super(message);
    }

    public ResourceLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceLoaderException(Throwable cause) {
        super(cause);
    }

    protected ResourceLoaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    private static final long serialVersionUID = 6241529098566940987L;
}
