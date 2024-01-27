package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Repository
 */
public class Repository {

    public static final String SENTINEL_COMMIT_ID = "6cf73ef132f3f89a94f4c73ec879aa79ba529e86";
    public static String INIT_PARENT_SHA1 = "0000000000000000000000000000000000000000";
    public static File CWD = new File(".");
    public static File GITLET_FOLDER = Utils.join(".gitlet");
    public static File HEAD = Utils.join(GITLET_FOLDER, "HEAD");
    public static File HEAD_REFS_FOLDER = Utils.join(GITLET_FOLDER, "heads");
    public static File LOGS_FOLDER = Utils.join(GITLET_FOLDER, "logs");
    public static File STAGE_FOLDER = Utils.join(GITLET_FOLDER, "staging");
    public static File COMMITS_FOLDER = Utils.join(GITLET_FOLDER, "commits");
    public static File BLOB_FOLDER = Utils.join(GITLET_FOLDER, "blobs");
    public static String CURR_DIR = System.getProperty("user.dir");

    public void init() throws IOException {
        // init dirs
        initDirs();
        // init branch, head, first init commit Sat Nov 11 12:30:00 2017 -0800
        HashMap<String, String> sentinelMap = new HashMap<>();
        Commit sentinelCommit = new Commit("sentinel", sentinelMap);
        Commit initCommit = new Commit("init commit", sentinelCommit.getSHA1(), new HashMap<>(), true);
        sentinelCommit.save();
        initCommit.save();
        Head.setGlobalHEAD("master", initCommit);
        Head.setBranchHEAD("master", initCommit);

    }

    public void initDirs() throws IOException {
        Repository.GITLET_FOLDER.mkdir();
        Repository.HEAD.createNewFile();
        Repository.HEAD_REFS_FOLDER.mkdir();
        Repository.LOGS_FOLDER.mkdir();
        Repository.STAGE_FOLDER.mkdir();
        Repository.COMMITS_FOLDER.mkdir();
        Repository.BLOB_FOLDER.mkdir();
    }

    public boolean isStageEmpty() throws IOException {
        return Stage.isStageEmpty();
    }

    public void log() {
        Head.showLog();
    }

    public void add(String filename, File filepath) throws IOException {
        Stage.addFile(filename, filepath);
    }

    public void status() throws IOException {
        Branch.showBranches();
        System.out.println();
        Stage.showAdditionFiles();
        System.out.println();
        Stage.showRemovalFiles();
        System.out.println();
    }

    public void commit(String message) throws IOException {
        if (!Stage.containsAdditionFiles() && !Stage.containsRemovalFiles()) {
            Main.exitMessage("You should commit with track file");
        }
        Commit prevCommit = Head.getGlobalHEAD();
        HashMap<String, String> blobs = prevCommit.getCloneBlobs();
        for (Entry<String, String> entry : Stage.stagedAddition.entrySet()) {
            blobs.put(entry.getKey(), entry.getValue());
        }
        for (Entry<String, String> entry : Stage.stagedRemoval.entrySet()) {
            File file = Utils.join(entry.getKey());
            file.delete();
            blobs.remove(entry.getKey());
        }
        Commit commit = new Commit(message, prevCommit.getSHA1(), blobs, false);
        Stage.clear();
        Branch HEADbranch = Branch.getGlobalBranch();
        Head.setGlobalHEAD(HEADbranch.getBranchName(), commit);
        Head.setBranchHEAD(HEADbranch.getBranchName(), commit);
        commit.save();
    }

    /**
     * Remove file from staging area
     * 
     * @param file
     * @throws IOException
     */
    public void rm(String file, File filepath) throws IOException {
        if (Stage.containsAdditionFile(filepath)) {
            Stage.removeFile(filepath);
        } else if (Head.containsFile(filepath)) {
            Stage.addRemovalFile(file, filepath);
        } else {
            Main.exitMessage("No reason to remove the file.");
        }
    }

    public void globalLog() {
        File[] files = Repository.COMMITS_FOLDER.listFiles();
        for (File file : files) {
            Commit commit = Utils.readObject(file, Commit.class);
            Prompt.promptLog(commit);
        }
    }

    public void checkout(String branch) throws IOException {
        Head.checkout(branch);
    }

    public void branch(String branchName) {
        if (Branch.containsBranch(branchName)) {
            Main.exitMessage("A branch with that name already exists.");
        }
        Commit commit = Head.getGlobalHEAD();
        Head.setGlobalHEAD(branchName, commit);
        Head.setBranchHEAD(branchName, commit);
    }

    public void removeBranch(String branchName) {
        if (!Branch.containsBranch(branchName)) {
            Main.exitMessage("A branch with that name does not exist.");
        }
        if (Branch.getGlobalBranch().getBranchName().equals(branchName)) {
            Main.exitMessage("Cannot remove the current branch.");
        }
        Branch.remove(branchName);
    }

    public void find(String message) {
        Branch.showBranchesByMessage(message);
    }

    public void reset(String commitVersion) throws IOException {
        Commit commit = Commit.findByVersion(commitVersion);
        if (commit == null) {
            Main.exitMessage("No commit with that id exists.");
        }
        Head.checkout(commitVersion);

    }

    public void merge(String branchName) {
        Head.mergeBranch(branchName);
    }
}