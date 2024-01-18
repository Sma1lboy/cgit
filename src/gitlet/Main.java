package gitlet;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static Repository repo = new Repository();

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            exitMessage("Please enter a command.");
        }
        System.out.println(Arrays.toString(args));
        if (args[0].equals("init") && args.length == 1) {
            repo.init();
        }
        else {
            switch (args[0]) {
                case "add":

                    break;
                case "commit":
                    if (args.length < 2) {
                        exitMessage("Please enter a commit message.");

                    }
                    repo = Repositories.getRepo();
                    if (repo.isStageEmpty()) {
                        exitMessage("No changes added to the commit.");

                    }
                    break;
                case "rm":

                    break;
                case "log":

                    repo = Repositories.getRepo();
                    repo.showLog();
                    break;

                case "global-log":
                    break;
                case "find":
                    break;
                case "status":
                    break;
                case "checkout":
                    break;
                case "branch":
                    break;
                case "rm-branch":
                    break;
                case "reset":
                    break;
                case "merge":
                    break;
                case "add-remote":
                    break;
                case "rm-remote":
                    break;
                case "push":
                    break;
                case "fetch":
                    break;
                case "pull":
                    break;
                default:
                    exitMessage("No command with that name exists.");
                    break;
            }
        }
    }

    private static void exitMessage(Object obj) {
        System.out.println(obj.toString());
        System.exit(0);
    }

    private static boolean checkIfInit() {
        return false;
    }
}
