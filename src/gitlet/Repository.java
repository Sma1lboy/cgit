package gitlet;

import java.util.Map;

/**
 * Repository
 */
public class Repository {

    Head head = new Head();
    Stage stagingArea = new Stage();

    public void init() {
        // init dirs
        initDirs();
        // init branch, head, first init commit Sat Nov 11 12:30:00 2017 -0800

    }

    public void initDirs() {
        Repositories.GITLET_FOLDER.mkdir();
        Repositories.HEAD.mkdir();
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