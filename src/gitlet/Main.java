package gitlet;

import java.io.File;
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
        } else {
            switch (args[0]) {
                case "add": {
                    File filepath = Utils.join(Repositories.CURR_DIR, args[1]);
                    if (!filepath.exists()) {
                        exitMessage("file doesn't exist: " + filepath);
                    }
                    repo.add(args[1], filepath);
                    break;
                }
                case "commit":
                    if (args.length < 2) {
                        exitMessage("Please enter a commit message.");

                    }
                    if (repo.isStageEmpty()) {
                        exitMessage("No changes added to the commit.");
                    }
                    repo.commit(args[1]);
                    break;
                case "rm":
                    File filepath = Utils.join(Repositories.CURR_DIR, args[1]);
                    if (!filepath.exists()) {
                        exitMessage("file doesn't exist: " + filepath);
                    }
                    repo.rm(args[1], filepath);
                    break;
                case "log":
                    repo.log();
                    break;

                case "global-log":
                    repo.globalLog();
                    break;
                case "find":
                    break;
                case "status":
                    repo.status();
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

    public static void exitMessage(Object obj) {
        System.out.println(obj.toString());
        System.exit(0);
    }

    private static boolean checkIfInit() {
        return false;
    }
}
