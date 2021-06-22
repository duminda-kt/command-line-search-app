package com.dts.discover.jsearch.app;

import com.dts.discover.jsearch.config.AppConfig;
import com.dts.discover.jsearch.config.Colour;
import com.dts.discover.jsearch.display.DisplayFormatter;
import com.dts.discover.jsearch.exception.DataLoadException;
import com.dts.discover.jsearch.parser.GenericParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/*
 * This class is written as a supporter and a runner to assemble the core parts of this app, For the sake of time
 * this is not unit test covered and excluded form the coverage reports.
 * Actual concepts of unit testing, code extensibility, test execution automation and test coverage reports
 * are displayed in the core parts of this project.
 */
public class SearchApp {

    //declare a scanner object to read the command line input by user
    private Scanner scanner;

    private PrintStream printStream;

    private String key;
    private String value;

    public static void main(String[] args) {
        SearchApp app = null;
        try {
            app = new SearchApp(System.in, System.out);
            app.run();
        } catch (DataLoadException e) {
            System.err.println("Unable to initialise the application : " + e.getMessage());
        }
    }

    public SearchApp(InputStream inputStream, PrintStream printStream) throws DataLoadException {
        scanner = new Scanner(inputStream, StandardCharsets.ISO_8859_1).useDelimiter("\n");
        this.printStream = printStream;
    }

    @SuppressWarnings("unchecked")
    public void run() {

        String userInput;
        String orgIdKey = "organization_id";
        List<String> displayOrder;

        try {
            GenericParser userParser = new GenericParser(AppConfig.USER_FILE_URL);
            GenericParser orgParser = new GenericParser(AppConfig.ORG_FILE_URL);
            GenericParser ticketParser = new GenericParser(AppConfig.TICKET_FILE_URL);

            printStream.println("Welcome to the data search application");
            printStream.println("======================================");
            printStream.println();

            //loop the utility in loop until the user makes the choice to exit
            while (true) {
                //Print the options for the user to choose from
                printStream.println("to start searching please enter ");
                printStream.println("1 for User search");
                printStream.println("2 for Organisation search");
                printStream.println("3 for Ticket search");
                printStream.println("or 'quit' anytime to exit the program");
                // Prompt the use to make a choice
                printStream.println("Enter your choice : ");

                //Capture the user input in scanner object and store it in a pre declared variable
                userInput = scanner.next();

                //Check the user input
                switch (userInput) {
                    case "1":
                        //do the job number 1
                        captureInput(userParser);
                        var users = Optional.ofNullable(findItemsByKV(userParser, key, value));
                        if (users.isPresent()) {
                            for (JSONObject JSONObject : (Iterable<JSONObject>) users.get()) {
                                displayOrder = new ArrayList<String>();
                                JSONObject page = new JSONObject();
                                page.put("User", JSONObject);
                                displayOrder.add("User");
                                var orgId = Optional.ofNullable(JSONObject.get(orgIdKey));
                                if (orgId.isPresent()) {
                                    var childOrgs =
                                            Optional.ofNullable(findItemsByKV(orgParser, "_id", orgId.get().toString()));
                                    if (childOrgs.isPresent()) {
                                        page.put("Organisation", childOrgs.get().get(0));
                                        displayOrder.add("Organisation");
                                    }
                                    var childTickets =
                                            Optional.ofNullable(findItemsByKV(ticketParser, orgIdKey, orgId.get().toString()));
                                    if (childTickets.isPresent()) {
                                        page.put("Ticket", childTickets.get());
                                        displayOrder.add("Ticket");
                                    }
                                }
                                DisplayFormatter formatter = new DisplayFormatter(page, displayOrder);
                                printStream.println(formatter.formatPage());
                            }
                        }
                        break;
                    case "2":
                        //do the job number 2
                        captureInput(orgParser);
                        var orgs = Optional.ofNullable(findItemsByKV(orgParser, key, value));
                        if (orgs.isPresent()) {
                            for (JSONObject JSONObject : (Iterable<JSONObject>) orgs.get()) {
                                displayOrder = new ArrayList<String>();
                                JSONObject page = new JSONObject();
                                page.put("Organisation", JSONObject);
                                displayOrder.add("Organisation");
                                var orgId = Optional.ofNullable(JSONObject.get("_id"));
                                if (orgId.isPresent()) {
                                    var childUsers =
                                            Optional.ofNullable(findItemsByKV(userParser, orgIdKey, orgId.get().toString()));
                                    if (childUsers.isPresent()) {
                                        page.put("User", childUsers.get());
                                        displayOrder.add("User");
                                    }
                                    var childTickets =
                                            Optional.ofNullable(findItemsByKV(ticketParser, orgIdKey, orgId.get().toString()));
                                    if (childTickets.isPresent()) {
                                        page.put("Ticket", childTickets.get());
                                        displayOrder.add("Ticket");
                                    }
                                }
                                DisplayFormatter formatter = new DisplayFormatter(page, displayOrder);
                                printStream.println(formatter.formatPage());
                            }
                        }
                        break;
                    case "3":
                        //do the job number 3
                        captureInput(ticketParser);
                        var tickets = Optional.ofNullable(findItemsByKV(ticketParser, key, value));
                        if (tickets.isPresent()) {
                            for (JSONObject JSONObject : (Iterable<JSONObject>) tickets.get()) {
                                displayOrder = new ArrayList<String>();
                                JSONObject page = new JSONObject();
                                page.put("Ticket", JSONObject);
                                displayOrder.add("Ticket");
                                var orgId = Optional.ofNullable(JSONObject.get(orgIdKey));
                                if (orgId.isPresent()) {
                                    var childOrgs =
                                            Optional.ofNullable(findItemsByKV(orgParser, "_id", orgId.get().toString()));
                                    if (childOrgs.isPresent()) {
                                        page.put("Organisation", childOrgs.get().get(0));
                                        displayOrder.add("Organisation");
                                    }
                                }
                                DisplayFormatter formatter = new DisplayFormatter(page, displayOrder);
                                printStream.println(formatter.formatPage());
                            }
                        }
                        break;
                    case "quit":
                        //exit from the program
                        printStream.println("Exiting...");
                        System.exit(1);
                    default:
                        //inform user in case of invalid choice.
                        printStream.println(Colour.RED + "Invalid choice. Read the options carefully..." + Colour.RESET);
                        printStream.println();
                }
            }
        } catch (DataLoadException loadException) {
            printStream.println(loadException.getMessage());
        }
    }

    private void captureInput(GenericParser parser) {
        printStream.println("please enter one of the field to search following list ");
        printStream.print(Colour.GREEN + parser.getKeyString() + Colour.RESET);
        printStream.println("Enter your choice : ");
        key = scanner.next();
        while (!parser.checkKey(key)) {
            if (key.equals("quit")) {
                printStream.println("Exiting...");
                System.exit(1);
            }
            printStream.println(Colour.RED + "Invalid Key please try again : " + Colour.RESET);
            key = scanner.next();
        }
        printStream.println("please enter the value searching for or if empty type [] : ");
        value = scanner.next();
        value = (value.equals("[]")) ? "" : value;
    }

    private JSONArray findItemsByKV(GenericParser parser, String key, String value) {
        try {
            return parser.getMatchingObj(key, value);
        } catch (Exception dnfException) {
            printStream.println(Colour.RED + dnfException.getMessage() + Colour.RESET);
            printStream.println();
            return null;
        }
    }
}