package gitlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * staging area
 */
public class Stage {
    // store the sha1 filepath and sha1 blob
    static HashMap<String, String> stagedAddition;
    static HashMap<String, String> stagedRemoval;
    static File stageAdditionFile = Utils.join(Repositories.STAGE_FOLDER, "stageaddition");
    static File stageRemovalFile = Utils.join(Repositories.STAGE_FOLDER, "stagedremoval");

    // init addition dn removal
    private static void load() throws IOException {
        if (!stageAdditionFile.exists()) {
            stagedAddition = new HashMap<>();
            Utils.writeObject(stageAdditionFile, stagedAddition);
        }
        if (!stageRemovalFile.exists()) {
            stagedRemoval = new HashMap<>();
            Utils.writeObject(stageRemovalFile, stagedRemoval);
        }
        stagedAddition = (HashMap<String, String>) Utils.readObject(stageAdditionFile, HashMap.class);
        stagedRemoval = (HashMap<String, String>) Utils.readObject(stageRemovalFile, HashMap.class);
    }

    private static void save() {
        Utils.writeObject(stageAdditionFile, stagedAddition);
        Utils.writeObject(stageRemovalFile, stagedRemoval);
    }

    // save file in stage Addition
    public static void addFile(String filename, File filepath) throws IOException {
        load();

        Blob blob = new Blob(filename, filepath);
        File blobFile = Utils.join(Repositories.BLOB_FOLDER, blob.getSha1());
        if (blobFile.exists()) {
            return;
        }
        Utils.writeObject(blobFile, blob);
        // add to stage
        stagedAddition.put(filepath.toString(), blob.getSha1());
        // save it
        save();
    }

    // save file in stage removal
    public static void addRemovalFile(String filename, File filepath) throws IOException {
        load();
        if (containsAdditionFile(filepath)) {
            return;
        }
        Blob blob = new Blob(filename, filepath);
        stagedRemoval.put(filepath.toString(), blob.getSha1());
        save();
    }

    // save file in stage Addition
    public static void removeFile(File filepath) throws IOException {
        load();
        String blobSha1 = stagedAddition.get(filepath.toString());
        if (blobSha1 == null)
            return; // check if in stage area
        File blobFile = Utils.join(Repositories.BLOB_FOLDER, blobSha1);
        blobFile.delete();
        stagedAddition.remove(filepath.toString());
        save();
    }

    public static boolean containsAdditionFile(File filepath) throws IOException {
        load();
        return stagedAddition.get(filepath.toString()) != null;
    }

    public static boolean containsAdditionFiles() throws IOException {
        load();
        return stagedAddition.size() != 0;
    }

    public static boolean containsRemovalFiles() throws IOException {
        load();
        return stagedRemoval.size() != 0;
    }

    public static void showAdditionFiles() throws IOException {
        load();
        Prompt.logTitle("Staged Files");
        for (String value : stagedAddition.values()) {
            Blob blob = Utils.readObject(Utils.join(Repositories.BLOB_FOLDER, value), Blob.class);
            Prompt.log(blob.filename);
        }
    }

    public static void showRemovalFiles() throws IOException {
        load();
        Prompt.logTitle("Removed Files");
        for (String value : stagedRemoval.values()) {
            Blob blob = Utils.readObject(Utils.join(Repositories.BLOB_FOLDER, value), Blob.class);
            Prompt.log(blob.filename);
        }
    }

    public static boolean isStageEmpty() throws IOException {
        load();
        return stagedAddition.isEmpty() && stagedRemoval.isEmpty();
    }

    /**
     * clear called when we finish commit, all remove files gones
     * 
     * @throws IOException
     */
    public static void clear() throws IOException {
        load();
        stagedAddition.clear();
        stagedRemoval.clear();
        save();
    }

    private static boolean containsUntrackFile() {
        File rootDir = Utils.join(Repositories.CURR_DIR);
        return containsUntrackDir(rootDir);
    }

    private static boolean containsUntrackDir(File dir) {
        File[] rootDir = dir.listFiles();
        Map<String, String> blobs = Head.getGlobalHEAD().getCloneBlobs();
        boolean res = true;
        for (File file : rootDir) {
            if (file.isDirectory()) {
                res = res && containsUntrackDir(file);
            } else if (file.isFile()) {
                if (!blobs.containsKey(file.toString())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean containsModifiedFile() {
        File rootDir = Utils.join(Repositories.CURR_DIR);
        return containsModifiedDir(rootDir);
    }

    private static boolean containsModifiedDir(File dir) {
        File[] rootDir = dir.listFiles();
        Map<String, String> blobs = Head.getGlobalHEAD().getCloneBlobs();
        boolean res = true;
        for (File file : rootDir) {
            if (file.isDirectory()) {
                res = res && containsUntrackDir(file);
            } else if (file.isFile()) {
                if (blobs.containsKey(file.toString())) {
                    String blobFilepath = blobs.get(file.toString());
                    File blobFile = Utils.join(Repositories.BLOB_FOLDER, blobFilepath);
                    Blob originBlob = Utils.readObject(blobFile, Blob.class);
                    Blob newBlob = Utils.readObject(file, Blob.class);
                    if (originBlob.content != newBlob.content) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

}
