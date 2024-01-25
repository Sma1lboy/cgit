package gitlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Head class keep manager which branch we are at
 */
public class Head {

    public static void setGlobalHEAD(String branchName, Commit commit) {
        Branch branch = new Branch(branchName, commit);
        Utils.writeObject(Repositories.HEAD, branch);
    }

    /**
     * get the latest head commit in current HEAD pointer branch
     * 
     * @return
     */
    public static Commit getGlobalHEAD() {
        Branch branch = Utils.readObject(Repositories.HEAD, Branch.class);
        return branch.getHead();
    }

    public static void setBranchHEAD(String branchName, Commit commit) {
        Branch branch = new Branch(branchName, commit);
        File branchFile = Utils.join(Repositories.HEAD_REFS_FOLDER, branchName);
        Utils.writeObject(branchFile, branch);
    }

    public static Commit getBranchHead(String branchName) {
        File branchFile = Utils.join(Repositories.HEAD_REFS_FOLDER, branchName);
        if (!branchFile.exists()) {
            // todo doesnt exist
            System.out.println("");
        }
        Branch branch = Utils.readObject(branchFile, Branch.class);
        return branch.getHead();
    }

    /**
     * Showing the current pointer branch log
     */
    public static void showLog() {
        Commit curr = getGlobalHEAD();
        while (curr.getDate() != null) {
            Prompt.promptLog(curr);
            curr = curr.getParent();
        }
    }

    /**
     * Check current HEAD pointer branch contains this file
     * 
     * @return
     */
    public static boolean containsFile(File filepath) {
        Commit currCommit = getGlobalHEAD();
        HashMap<String, String> cloneBlobs = currCommit.getCloneBlobs();
        return cloneBlobs.containsKey(filepath.toPath());
    }

    public static void checkout(String version) throws IOException {
        if (Stage.containsAdditionFiles()) {
            Main.exitMessage("Please commit your changes or stash them before you switch branches.");
        }
        Commit branchHead = findBranchHead(version);
        if (branchHead != null) {
            setGlobalHEAD(version, branchHead);
            maintainHead();

            return;
        }
        Commit versionCommit = Commits.findByVersion(version);
        if (versionCommit != null) {
            setGlobalHEAD(version, versionCommit);
            maintainHead();
        }
    }

    public static void resetHead(String version) {
        // Commit commit = getGlobalHEAD();
        // while (commit.getDate() != null) {
        // if(commit.)
        // }
    }

    private static Commit findBranchHead(String branchName) {
        List<Branch> branches = Branches.getBranches();
        for (Branch branch : branches) {
            if (branch.getBranchName().equals(branchName)) {
                return branch.getHead();
            }
        }
        return null;
    }

    /**
     * maintain current version commit HEAD, make current user_dir with current
     * commit
     * version
     * 
     * @return
     * @throws IOException
     * @require before we call this method, make sure there is no staging file and
     *          untrack file
     */
    public static void maintainHead() throws IOException {
        Commit headCommit = Head.getGlobalHEAD();
        maintainDirectory(headCommit);

        // check dir if there is file is blobs but not on current commit remove it
        File rootDir = Utils.join(Repositories.CURR_DIR);
        maintainDir(rootDir, headCommit);
    }

    /**
     * Maintain directory base on commit blobs, create the content of commit blobs
     * if didn't exist.
     * 
     * @param commit
     * @throws IOException
     */
    private static void maintainDirectory(Commit commit) throws IOException {
        // adding tracks file for current commit
        Map<String, String> blobs = commit.getCloneBlobs();
        for (Entry<String, String> entry : blobs.entrySet()) {
            File filepath = Utils.join(entry.getKey());
            if (!filepath.exists()) {
                filepath.createNewFile();
            }
            File blobFile = Utils.join(Repositories.BLOB_FOLDER, entry.getValue());
            Blob blob = Utils.readObject(blobFile, Blob.class);
            FileOutputStream fileWriter = new FileOutputStream(filepath);
            fileWriter.write(blob.content);
            fileWriter.close();
        }
    }

    /**
     * Delete tracked file not from this commit version.
     * 
     * @param dir
     * @param commit the version of commit you are checking
     */
    private static void maintainDir(File dir, Commit commit) {
        Map<String, String> blobs = commit.getCloneBlobs();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                maintainDir(file, commit);
                continue;
            }
            if (Blobs.containsBlobs(file) && !blobs.containsKey(file.toString())) {
                file.delete();
            }
        }
    }

}
