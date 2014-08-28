package at.yawk.ninjakat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class Log {
    private static final Logger ROOT = Logger.getLogger("at.yawk.ninjakat");

    static { init0(); }

    public static void init() {
        // static constructor
    }

    private static void init0() {
        ROOT.setLevel(Level.FINEST);
        ROOT.setUseParentHandlers(false);
        Arrays.stream(ROOT.getHandlers()).forEach(ROOT::removeHandler);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        handler.setFormatter(new NinjaFormatter());
        ROOT.addHandler(handler);

        log.info("Logging set up");
    }

    public static void addLogFile(Path file) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(file);

        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel().intValue() < getLevel().intValue()) {
                    return;
                }

                Formatter formatter = getFormatter();
                String formatted = formatter.format(record);
                try {
                    writer.write(formatted);
                    writer.flush();
                } catch (IOException ignored) {}
            }

            @Override
            public void flush() {} // flush in publish(LogRecord)

            @Override
            public void close() throws SecurityException {
                try {
                    writer.close();
                } catch (IOException ignored) {}
            }
        };
        handler.setFormatter(new NinjaFormatter());

        ROOT.addHandler(handler);
    }

    private static class NinjaFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(record.getLevel())
                    .append('\t')
                    .append(record.getMessage())
                    .append('\n');
            Throwable thrown = record.getThrown();
            while (thrown != null) {
                builder.append(thrown.getClass().getName());
                String message = thrown.getMessage();
                if (message != null) {
                    builder.append(": ").append(message);
                }
                builder.append('\n');
                Arrays.stream(thrown.getStackTrace())
                        .forEach(ele -> builder.append('\t').append(ele.toString()).append('\n'));
                thrown = thrown.getCause();
                if (thrown != null) {
                    builder.append("Caused by: ");
                }
            }
            return builder.toString();
        }
    }
}
