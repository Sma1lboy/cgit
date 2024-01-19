package gitlet;

import java.io.IOException;
import java.util.HashMap;

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

    public void showLog() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showLog'");
    }

    public void add(String filename) throws IOException {
        Stage.addFile(filename);
    }

    public void status() throws IOException {
        Stage.showAdditionFiles();
    }

    public void commit(String message) throws IOException {
        Commit prevCommit = Head.getGlobalHEAD();
        Commit commit = new Commit(message, prevCommit.getSHA1(), Stage.stagedAddition, false);
        Stage.clear();
        Head.setGlobalHEAD(branch, commit);
        Head.setBranchHEAD(branch, commit);
        commit.save();
    }
}