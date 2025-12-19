package Loggers;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerWriter extends Writer {

    StringBuffer buffer = new StringBuffer();
    Logger logger;

    LoggerWriter(Logger logger){
        this.logger=logger;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

        for (int i = 0; i < len; i++) {
            char c = cbuf[off + i];
            if (c == '\n' || c == '\r') {
                flush();
            } else {
                buffer.append(c);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (!buffer.isEmpty()) {
            logger.log(Level.INFO, buffer.toString());
            buffer.setLength(0);
            buffer = new StringBuffer();
        }

    }

    @Override
    public void close() throws IOException {
        flush();

    }
}
