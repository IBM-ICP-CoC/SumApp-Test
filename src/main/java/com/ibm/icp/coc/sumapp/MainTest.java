package com.ibm.icp.coc.sumapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class MainTest {
	
	private Random rnd;
	
	private String runTests(String host, int runs, boolean headless) throws InterruptedException {
		
		ChromeOptions chromeOptions = new ChromeOptions();

		if( headless ) {
			chromeOptions.addArguments("headless");
		}
		chromeOptions.addArguments("window-size=600,480");

		WebDriver webDriver = new ChromeDriver(chromeOptions);
		
		
		StringBuilder report = new StringBuilder();
		
		SumTest testRunner = new SumTest(webDriver, host);
		
		int passed = 0;
		
		System.out.println("=========================================================");
		
		String headings2 = String.format("%3s  %3s %3s   %10s  %5s     %7s\n", "run", "op1", "op2", "result  ", "title", "result" );
		String headings1 = String.format("%3s  %3s %3s   %10s  %5s     %-7s\n", "", "", "", "", "", "  run" );
		report.append(headings1);
		report.append(headings2);
		report.append("-----------------------------------------------\n");
		
		System.out.print(report.toString());

		for(int i=0; i<runs; i++) {
			
			int op1 = nextInt();
			int op2 = nextInt();
			
			TestResult res = testRunner.testSum(op1, op2);
			String runNo = String.format("%3s ", i );
			report.append(runNo+res+'\n');
			System.out.println(runNo+res);
			if( res.isPassed() ) {
				passed++;
			} 			
		}

		Thread.sleep(2000);

		webDriver.close();
		webDriver.quit();
		
		report.append( "-----------------------------------------------\n");
		report.append( "Runs:   " + runs + '\n' );
		report.append( "Passed: " + passed + '\n');

		return report.toString();

	}
	

	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		Options options = new Options();
		Option hostOption = Option.builder("s")
			     .longOpt("server")
			     .hasArg()
			     .argName("<serverurl>")
			     .desc("the url of the server to be tested")
			     .build();
		Option runsOption = Option.builder("r")
			     .longOpt("runs")
			     .hasArg()
			     .argName("<runs>")
			     .desc("the number of test runs to make")
			     .build();
		Option headlessOption = Option.builder("d")
			     .longOpt("display")
			     .desc("display the ui (not headless)")
			     .build();
		Option gitProjectOption = Option.builder("p")
			     .longOpt("gitProject")
			     .hasArg()
			     .argName("<owner/project>")
			     .desc("the git project to post report to")
			     .build();
		Option gitUserOption = Option.builder("u")
			     .longOpt("gitUser")
			     .hasArg()
			     .argName("<user>")
			     .desc("user to authenticate with GitHub")
			     .build();
		Option gitTokenOption = Option.builder("t")
			     .longOpt("gitToken")
			     .hasArg()
			     .argName("<token>")
			     .desc("token to authenticate with GitHub")
			     .build();
		
			 
		options.addOption( hostOption );
		options.addOption( runsOption );
		options.addOption( headlessOption );
		options.addOption( gitProjectOption );
		options.addOption( gitUserOption );
		options.addOption( gitTokenOption );
		
		options.addOption( "h", "help", false, "shows command line help (this message)" );

		try {
		    // parse the command line arguments
		    CommandLine line = parser.parse( options, args );

		    if( line.hasOption( 'h' )  ) {
		    	HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp( "java -jar sumapp-test.jar --host <host>", options );
		    } else {
			    if( !line.hasOption('s')) {
			    	System.out.println("Host url required.");
			    	HelpFormatter formatter = new HelpFormatter();
			    	formatter.printHelp( "sumapp-test -s <server url>", options );
			    } else {
				    
			    	String host = line.getOptionValue('s');
			    	String runsArg = line.getOptionValue('r', "20");
			    	int noRuns = Integer.parseInt(runsArg);
			    	
			    	boolean display = line.hasOption( 'd' );
			    	
			    	MainTest tester = new MainTest();
			    	String report = tester.runTests(host, noRuns, !display);
			    	
			    	if( line.hasOption('p') && line.hasOption('t') && line.hasOption('u') ) {
				    	String gitProject = line.getOptionValue('p');
				    	String gitUser = line.getOptionValue('u');
				    	String gitToken = line.getOptionValue('t');
						try {
							GitHub github = GitHub.connect(gitUser, gitToken );
							GHRepository gitrepo = github.getRepository(gitProject);
							GHIssueBuilder ib = gitrepo.createIssue("Automated Test Results");
							String buildUrl = System.getenv("BUILD_URL");
							String buildNo = System.getenv("BUILD_ID");
							
							StringBuilder body = new StringBuilder();
							body.append("<h3>Test Results</h3><p><a href=\"");
							body.append(buildUrl);
							body.append("\">Build: ");
							body.append(buildNo);
							body.append("</a></p>\n");
							body.append("<pre>\n");
							body.append(report.toString());
							body.append("</pre>");
							
							ib.body(body.toString());
							ib.label("test");
							ib.create();
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}

			    }
		    }
		    
		}
		catch( ParseException exp ) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}


	}
	
	public MainTest() {
		rnd = new Random(System.currentTimeMillis());
	}
	
	private int nextInt() {
		return 1 + rnd.nextInt(9);
	}

}
