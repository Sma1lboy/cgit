package gitlet;

import java.io.File;

/**
 * Helper class for repositories
 */
public class Repositories {
    public static File CWD = new File(".");
    public static File GITLET_FOLDER = Utils.join(".gitlet");
    public static File HEAD = Utils.join(GITLET_FOLDER, "HEAD");
    public static File HEAD_REFS_FOLDER = Utils.join(GITLET_FOLDER, "heads");
    public static File LOGS_FOLDER = Utils.join(GITLET_FOLDER, "logs");
    public static File STAGE_FOLDER = Utils.join(GITLET_FOLDER, "staging");
    public static File COMMITS_FOLDER = Utils.join(GITLET_FOLDER, "commits");
    public static File BLOB_FOLDER = Utils.join(GITLET_FOLDER, "blobs");

    /**
     * get caller folder's repo
     */
    public static Repository getRepo() {

        return null;
    }

}
