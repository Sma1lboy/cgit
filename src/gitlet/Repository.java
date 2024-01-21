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

    // HEAD is
    Stage stagingArea = new Stage();
    String branch = "master";

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
        Repositories.GITLET_FOLDER.mkdir();
        Repositories.HEAD.createNewFile();
        Repositories.HEAD_REFS_FOLDER.mkdir();
        Repositories.LOGS_FOLDER.mkdir();
        Repositories.STAGE_FOLDER.mkdir();
        Repositories.COMMITS_FOLDER.mkdir();
        Repositories.BLOB_FOLDER.mkdir();
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
        Stage.showAdditionFiles();
    }

    public void commit(String message) throws IOException {
        Commit prevCommit = Head.getGlobalHEAD();
        HashMap<String, String> blobs = prevCommit.getCloneBlobs();
        for (Entry<String, String> entry : Stage.stagedAddition.entrySet()) {
            blobs.put(entry.getValue(), entry.getValue());
        }
        for (Entry<String, String> entry : Stage.stagedRemoval.entrySet()) {
            blobs.remove(entry.getValue());
        }
        Commit commit = new Commit(message, prevCommit.getSHA1(), blobs, false);
        Stage.clear();
        Head.setGlobalHEAD(branch, commit);
        Head.setBranchHEAD(branch, commit);
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
        File[] files = Repositories.COMMITS_FOLDER.listFiles();
        for (File file : files) {
            Commit commit = Utils.readObject(file, Commit.class);
            Prompt.promptLog(commit);
        }
    }

    public void checkout(String branch) {

    }
}