package top.yangxf.interest.flow.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yangxf
 */
public class SoutLogger implements SysLogger {
    private static final long serialVersionUID = 6692575579263062252L;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private String name;

    SoutLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {
        print(Level.TRACE, msg);
    }

    @Override
    public void trace(String format, Object arg) {
        format(Level.TRACE, format, arg);
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        format(Level.TRACE, format, argA, argB);
    }

    @Override
    public void trace(String format, Object... arguments) {
        format(Level.TRACE, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        printErr(Level.TRACE, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isTraceEnabled();
    }

    @Override
    public void debug(String msg) {
        print(Level.DEBUG, msg);
    }

    @Override
    public void debug(String format, Object arg) {
        format(Level.DEBUG, format, arg);
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        format(Level.DEBUG, format, argA, argB);
    }

    @Override
    public void debug(String format, Object... arguments) {
        format(Level.DEBUG, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        printErr(Level.DEBUG, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isTraceEnabled();
    }

    @Override
    public void info(String msg) {
        print(Level.INFO, msg);
    }

    @Override
    public void info(String format, Object arg) {
        format(Level.INFO, format, arg);
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        format(Level.INFO, format, argA, argB);
    }

    @Override
    public void info(String format, Object... arguments) {
        format(Level.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        printErr(Level.INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isTraceEnabled();
    }

    @Override
    public void warn(String msg) {
        print(Level.WARN, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        format(Level.WARN, format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        format(Level.WARN, format, arguments);
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        format(Level.WARN, format, argA, argB);
    }

    @Override
    public void warn(String msg, Throwable t) {
        printErr(Level.WARN, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isTraceEnabled();
    }

    @Override
    public void error(String msg) {
        print(Level.ERROR, msg);
    }

    @Override
    public void error(String format, Object arg) {
        format(Level.ERROR, format, arg);
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        format(Level.ERROR, format, argA, argB);
    }

    @Override
    public void error(String format, Object... arguments) {
        format(Level.ERROR, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        printErr(Level.ERROR, msg, t);
    }

    private void print(Level l, String msg) {
        System.out.println(LocalDateTime.now().format(FORMATTER) + l.value + name + " - " + msg);
    }

    private void format(Level l, String msg, Object arg) {
        String format = String.format(msg.replaceAll("\\{}", "%s"), arg);
        System.out.println(LocalDateTime.now().format(FORMATTER) + l.value + name + " - " + format);
    }

    private void format(Level l, String msg, Object argA, Object argB) {
        String format = String.format(msg.replaceAll("\\{}", "%s"), argA, argB);
        System.out.println(LocalDateTime.now().format(FORMATTER) + l.value + name + " - " + format);
    }

    private void format(Level l, String msg, Object... arguments) {
        String format = String.format(msg.replaceAll("\\{}", "%s"), arguments);
        System.out.println(LocalDateTime.now().format(FORMATTER) + l.value + name + " - " + format);
    }

    private void printErr(Level l, String msg, Throwable t) {
        System.out.println(LocalDateTime.now().format(FORMATTER) + l.value + name + " - " + msg);
        t.printStackTrace();
    }

    private enum Level {
        TRACE(" - TRACE : "),
        DEBUG(" - DEBUG : "),
        INFO(" - INFO  : "),
        WARN(" - WARN  : "),
        ERROR(" - ERROR : ");
        private String value;

        Level(String value) {
            this.value = value;
        }
    }

}
