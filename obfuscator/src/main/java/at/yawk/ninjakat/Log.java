package at.yawk.ninjakat;

import java.util.Arrays;
import java.util.logging.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class Log {
    public static void init() {
        Logger main = Logger.getLogger("at.yawk.ninjakat");
        main.setLevel(Level.FINEST);
        main.setUseParentHandlers(false);
        Arrays.stream(main.getHandlers()).forEach(main::removeHandler);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLevel() + "\t" + record.getMessage() + "\n";
            }
        });
        main.addHandler(handler);

        log.info("Logging set up");
    }
}
