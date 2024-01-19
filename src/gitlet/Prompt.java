package gitlet;

/**
 * Manage prompt format class
 */
public class Prompt {

    public static void logTitle(String message) {
        System.out.println("=== " + message + " ===");
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public static void promptLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getSHA1());
        System.out.println("Date: " + commit.getDate());
        System.out.println(commit.message);
    }
}
