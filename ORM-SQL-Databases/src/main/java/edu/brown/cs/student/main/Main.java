/*
public class Main {
  public static void main(String[] args){
    edu.brown.cs.student.main.KDTree testTree = new edu.brown.cs.student.main.KDTree();
    edu.brown.cs.student.main.KDTree.Node n = testTree.new Node(1, 1, 1, 1, "Leo");
    testTree.insertNode(n);
    edu.brown.cs.student.main.KDTree.Node n2 = testTree.new Node(2, 2, 2, 2, "Leo");
    edu.brown.cs.student.main.KDTree.Node n3 = testTree.new Node(3, 3, 3, 3, "Leo");
    edu.brown.cs.student.main.KDTree.Node n4 = testTree.new Node(4, 15, 5, 23, "Leo");
    edu.brown.cs.student.main.KDTree.Node n5 = testTree.new Node(5, 15, 8, 23, "Leo");
    edu.brown.cs.student.main.KDTree.Node n6 = testTree.new Node(6, 15, 11, 23, "Leo");
    edu.brown.cs.student.main.KDTree.Node n7 = testTree.new Node(7, 15, 13, 23, "Leo");
    edu.brown.cs.student.main.KDTree.Node n8 = testTree.new Node(8, 15, 11, 21, "Leo");
    edu.brown.cs.student.main.KDTree.Node n9 = testTree.new Node(9, 15, 5, 25, "Leo");
    testTree.insertNode(n2);
    testTree.insertNode(n3);
    testTree.insertNode(n4);
    testTree.insertNode(n5);
    testTree.insertNode(n6);
    testTree.insertNode(n7);
    testTree.insertNode(n8);
    testTree.insertNode(n9);
    System.out.println(testTree);
    System.out.println(testTree.nearestNeighbors(4, 1));
  }
}
*/

package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */




/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    Database db = null;
    Rent rent = new Rent("good", "meh", "3", "4", "5", "6", "7", "1234");

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          //This regex splits at spaces except when surrounded by quotes. I learned how to do this
          // from this link https://stackabuse.com/regex-splitting-by-character-unless-in-quotes/
          String[] arguments = input.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
          switch (arguments[0]) {
            case "database":
              db = new Database(arguments[1]);
              break;
            case "insert":
              db.insert(rent);
              break;
            case "delete":
              db.delete(rent);
              break;
            case "select":
              db.where("item_id", "2");
              break;
            case "update":
              db.update(rent, "fit", "excellent!");
              break;
            case "query":
              String query = "UPDATE rent set size='5' where fit='bad'";
              db.sql(query);
              break;
            default:
              System.out.println("ERROR: command not found");
              break;
          }
        } catch (Exception e) {
          e.printStackTrace();
          //System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}


