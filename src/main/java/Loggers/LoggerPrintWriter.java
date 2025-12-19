package Loggers;

import java.io.PrintWriter;
import java.util.logging.Logger;

public class LoggerPrintWriter extends PrintWriter {

    public LoggerPrintWriter(Logger logger){
        super(new LoggerWriter(logger));
    }
}
