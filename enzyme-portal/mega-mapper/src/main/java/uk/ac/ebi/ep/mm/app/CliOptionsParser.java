package uk.ac.ebi.ep.mm.app;


import org.apache.commons.cli.*;

/**
 * A parser of the command line options for the mega-mapper parsers.
 * @author rafa
 *
 */
public class CliOptionsParser {

	/**
	 * Processes the command line options.
	 * @param args (order does not matter):
	 * 		-file &lt;file&gt; (-indexDir &lt;indexDir&gt;|-dbConfig
	 * 		&lt;dbConfig&gt;)
	 * <ul>
	 * 	<li>-file &lt;file&gt;: the file to parse (XML, HTML, tab-delimited...).
	 * 		</li>
	 * 	<li>-indexDir &lt;indexDir&gt; (optional): the directory for a lucene
	 * 		index. If it does not exist, a new one is created.</li>
	 * 	<li>-dbConfig &lt;dbConfig&gt; (optional): the name of a database
	 * 		configuration file.</li>
	 * </ul>
	 * Either <code>-indexDir</code> or <code>-dbConfig</code> must be defined,
	 * but not both. If none of them is defined, the default hibernate config
	 * file - <code>hibernate.cfg.xml</code> - will be used as dbConfig. 
	 * @return a commandLine object with the required options, or
	 * 		<code>null</code> if the arguments are not valid.
	 */
	@SuppressWarnings("static-access")
	static CommandLine getCommandLine(String... args) {
		Options options = new Options();
        options.addOption(OptionBuilder.isRequired()
                .hasArg().withArgName("file")
                .withDescription("XML (or HTML) file")
                .create("file"));
        options.addOption(OptionBuilder
                .hasArg().withArgName("indexDir")
                .withDescription("Lucene index directory")
                .create("indexDir"));
        options.addOption(OptionBuilder
                .hasArg().withArgName("dbConfig")
                .withDescription("Configuration for DB connection")
                .create("dbConfig"));
        CommandLine cl = null;
        try {
            cl = new GnuParser().parse(options, args);
	        if (cl.hasOption("indexDir") && cl.hasOption("dbCOnfig")){
            	throw new ParseException("Either indexDir or dbConfig must be" +
            			" defined, but not both");
	        }
        } catch (ParseException e){
            new HelpFormatter().printHelp(MmParser.class.getName(), options);
        }
		return cl;
	}

}
