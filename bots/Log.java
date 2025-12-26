package bots;

public class Log {

    // A boolean that we set as true if we want to print out debug strings, and false if not
    public static boolean IS_DEBUG = true;


    public static String logMessage = "";

    /**
     * A function to print strings if we are in debug mode
     * @param toPrint The string to print
     */
    public static void log(Object toPrint) {
        if(IS_DEBUG) {
            /*System.out.println(toPrint);*/
            logMessage += toPrint + "\n";
        }
    }

    public static void printLog() {
        System.out.print(logMessage);
        logMessage = "";
    }
}
