import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        Options options = new Options();
        options.addRequiredOption("p", "Path", true, "Path to directory");
        options.addOption("s", "Statistics of information");
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);
            Parser parser = new Parser();
            parser.parse(cmd.getOptionValue("p"), cmd.hasOption("s"));
        } catch (ParseException ex) {
            log.error("Options parse error");
        }
    }
}
