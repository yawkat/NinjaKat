package at.yawk.ninjakat.js;

import java.io.Reader;
import java.nio.file.Path;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

/**
 * @author yawkat
 */
@Slf4j
public class JsRunner {
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    public JsRunner(Path workingDir) {
        engine.put("obfuscator", new ActionProvider(workingDir));
        engine.put("log", LoggerFactory.getLogger(JsRunner.class));
    }

    public void execute(Reader reader) throws ScriptException {
        engine.eval(reader);
    }
}
