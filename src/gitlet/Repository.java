package gitlet;

import java.io.IOException;
import java.util.HashMap;

/**
 * Repository
 */
public class Repository {

    public static final String SENTINEL_COMMIT_ID = "6cf73ef132f3f89a94f4c73ec879aa79ba529e86";
    public static String INIT_PARENT_SHA1 = "0000000000000000000000000000000000000000";

    Head head = new Head();
    Stage stagingArea = new Stage();

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

    public boolean isStageEmpty() {
        return true;
    }

    public void showLog() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showLog'");
    }
}