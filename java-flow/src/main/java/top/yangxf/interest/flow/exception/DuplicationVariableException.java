package top.yangxf.interest.flow.exception;

/**
 * @author yangxf
 */
public class DuplicationVariableException extends RuntimeException {
    private static final long serialVersionUID = -278419813851896055L;

    public DuplicationVariableException() {
        super();
    }

    public DuplicationVariableException(String message) {
        super(message);
    }

    public DuplicationVariableException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicationVariableException(Throwable cause) {
        super(cause);
    }

    protected DuplicationVariableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
