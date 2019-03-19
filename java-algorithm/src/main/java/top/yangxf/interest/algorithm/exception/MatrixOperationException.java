package top.yangxf.interest.algorithm.exception;

/**
 * @author yangxf
 */
public class MatrixOperationException extends RuntimeException {
    private static final long serialVersionUID = 6714891579678161689L;

    public MatrixOperationException() {
    }

    public MatrixOperationException(String message) {
        super(message);
    }

    public MatrixOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixOperationException(Throwable cause) {
        super(cause);
    }

    public MatrixOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
