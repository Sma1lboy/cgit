package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static Repository repo = new Repository();

    public static void deleteFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            }
            file.delete();
        }
        folder.delete();
    }

    public static String[] commandBuilder(String command) {
        return command.split(" ");

    }

    public static void test(String[] args) throws IOException {
        // delete gitlet folder
        File giletFolder = Repository.GITLET_FOLDER;
        if (giletFolder.exists()) {
            deleteFolder(giletFolder);
        }
        main(commandBuilder("init"));
        main(commandBuilder("add test.txt"));
        main(commandBuilder("commit 'masteraddingtest.txt'"));
        main(commandBuilder("status"));
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            exitMessage("Please enter a command.");
        }
        System.out.println(Arrays.toString(args));
        if (args[0].equals("init") && args.length == 1) {
            repo.init();
        } else {
            switch (args[0]) {
                case "test": {
                    test(args);
                    break;
                }
                case "add": {
                    File filepath = Utils.join(Repository.CURR_DIR, args[1]);
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
                    File filepath = Utils.join(Repository.CURR_DIR, args[1]);
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
                    if (args.length < 2) {
                        exitMessage("There should be a message to find.");
                    }
                    repo.find(args[1]);
                    break;
                case "status":
                    repo.status();
                    break;
                case "checkout":
                    if (args.length < 2) {
                        exitMessage("You should set version.");
                    }
                    repo.checkout(args[1]);
                    break;
                case "branch":
                    if (args.length < 2) {
                        exitMessage("You should input a name of the branch");
                    }
                    repo.branch(args[1]);
                    break;
                case "rm-branch":
                    if (args.length < 2) {
                        exitMessage("You should using this commend with args, check help methods");
                    }
                    repo.removeBranch(args[1]);
                    break;
                case "reset":
                    if (args.length < 2) {
                        exitMessage("You should using this commend with args");
                    }
                    repo.reset(args[1]);
                    break;
                case "merge":
                    if (args.length < 2) {
                        exitMessage("you should use this commend with args");
                    }
                    repo.merge(args[1]);
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
