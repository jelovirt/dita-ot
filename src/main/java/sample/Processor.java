package sample;

import org.apache.tools.ant.*;
//import org.slf4j.Logger;

import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * DITA-OT processer. Not thread-safe, but can be reused.
 */
public class Processor {

    private File ditaDir;
    private Map<String, String> args;
//    private Logger logger;

    Processor(final File ditaDir, final String transtype, final Map<String, String> args) {
        this.ditaDir = ditaDir;
        this.args = new HashMap<String, String>(args);
        this.args.put("dita.dir", ditaDir.getAbsolutePath());
        this.args.put("transtype", transtype);
    }

    public void setInput(final File input) {
        setInput(input.getAbsoluteFile().toURI());
    }

    public void setInput(final URI input) {
        args.put("args.input", input.toString());
    }

    public void setOutput(final File output) {
        args.put("output.dir", output.getAbsolutePath());
    }

    public void setOutput(final URI output) {
        if (!output.getScheme().equals("file")) {
            throw new IllegalArgumentException("Only file scheme allowed as output directory URI");
        }
        args.put("output.dir", output.toString());
    }

    public void setProperty(final String name, final String value) {
        args.put(name, value);
    }

//    public void setLogger(final Logger logger) {
//        this.logger = logger;
//    }

    public void run() {
        if (!args.containsKey("args.input")) {
            throw new IllegalStateException();
        }
        final PrintStream savedErr = System.err;
        final PrintStream savedOut = System.out;
        try {
            final File buildFile = new File(ditaDir, "build.xml");
            final Project project = new Project();
            project.setCoreLoader(this.getClass().getClassLoader());
//            if (logger != null) {
//                project.addBuildListener(new LoggerListener(logger));
//            }
            project.addBuildListener(new DefaultLogger());
            project.fireBuildStarted();
            project.init();
            project.setBaseDir(ditaDir);
            project.setKeepGoingMode(false);
            for (final Map.Entry<String, String> arg : args.entrySet()) {
                project.setUserProperty(arg.getKey(), arg.getValue());
            }
            ProjectHelper.configureProject(project, buildFile);
            final Vector<String> targets = new Vector<String>();
            targets.addElement(project.getDefaultTarget());
            project.executeTargets(targets);
        } finally {
            System.setOut(savedOut);
            System.setErr(savedErr);
        }
    }
}
