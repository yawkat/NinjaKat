package at.yawk.ninjakat.js;

import at.yawk.ninjakat.Log;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();

        OptionSpec<String> scriptFileName =
                parser.accepts("s")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("Script file name")
                        .required();

        OptionSpec<String> logFileName =
                parser.accepts("l")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("Log file name");

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            e.printStackTrace();
            parser.printHelpOn(System.err);
            System.exit(-1);
            return;
        }

        Path scriptFile = Paths.get(scriptFileName.value(options));
        Optional<Path> logFile = Optional.ofNullable(logFileName.value(options)).map(Paths::get);

        main(scriptFile, logFile);
    }

    private static void main(Path scriptFile, Optional<Path> logFile) throws Exception {
        if (!Files.isRegularFile(scriptFile)) {
            System.err.println("Script file does not exist.");
            System.exit(-1);
        }

        logFile.ifPresent(log -> {
            try {
                Files.createDirectories(log.getParent());

                Log.addLogFile(log);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });

        JsRunner runner = new JsRunner(scriptFile.getParent());

        try (Reader r = Files.newBufferedReader(scriptFile)) {
            runner.execute(r);
        } catch (Throwable t) {
            log.error("Failed to execute script", t);
        }
    }
}
