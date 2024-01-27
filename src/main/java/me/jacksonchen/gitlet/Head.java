package main.java.me.jacksonchen.gitlet;

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
        Utils.writeObject(Repository.HEAD, branch);
    }

    /**
     * get the latest head commit in current HEAD pointer branch
     * 
     * @return
     */
    public static Commit getGlobalHEAD() {
        Branch branch = Utils.readObject(Repository.HEAD, Branch.class);
        return branch.getHead();
    }

    public static void setBranchHEAD(String branchName, Commit commit) {
        Branch branch = new Branch(branchName, commit);
        File branchFile = Utils.join(Repository.HEAD_REFS_FOLDER, branchName);
        Utils.writeObject(branchFile, branch);
    }

    public static Commit getBranchHead(String branchName) {
        File branchFile = Utils.join(Repository.HEAD_REFS_FOLDER, branchName);
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
        Commit branchHead = getBranchHead(version);
        if (branchHead != null) {
            setGlobalHEAD(version, branchHead);
            maintainHead();

            return;
        }
        Commit versionCommit = Commit.findByVersion(version);
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
        File rootDir = Utils.join(Repository.CURR_DIR);
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
            File blobFile = Utils.join(Repository.BLOB_FOLDER, entry.getValue());
            Blob blob = Utils.readObject(blobFile, Blob.class);
            Blob testBlob = new Blob(filepath.getName(), filepath);
            if (!blob.getSha1().equals(testBlob.getSha1())) {
                FileOutputStream fileWriter = new FileOutputStream(filepath);
                fileWriter.write(blob.content);
                fileWriter.close();
            }

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
            if (Blob.containsBlobs(file) && !blobs.containsKey(file.toString())) {
                file.delete();
            }
        }
    }

    public static void mergeBranch(String branchName) {
        Branch givenBranch = Branch.getBranchByName(branchName);
        Branch headBranch = Branch.getGlobalBranch();
        Commit splitCommit = headBranch.findSplitPoint(givenBranch);
        HashMap<String, String> splitBlobs = splitCommit.getCloneBlobs();
        HashMap<String, String> headBlobs = headBranch.getHead().getCloneBlobs();
        HashMap<String, String> givenBlobs = givenBranch.getHead().getCloneBlobs();

        // first five properties on github page
        splitBlobs.forEach((k, v) -> {
            // both contain current blob
            if (headBlobs.containsKey(k) && givenBlobs.containsKey(k)) {
                // both modified
                if (!v.equals(headBlobs.get(k)) && !v.equals(givenBlobs.get(k))) {
                    if (!headBlobs.get(k).equals(givenBlobs.get(k))) {
                        // get file and do some <====== HEAD 之类的
                        Conflict.resolve(Utils.join(k), Blob.getBlob(headBlobs.get(k)), headBranch.getBranchName(),
                                Blob.getBlob(givenBlobs.get(k)), givenBranch.getBranchName());
                    }

                } else if (!v.equals(headBlobs.get(k))) {
                    Stage.addBlobToStagedAddition(k, headBlobs.get(k));

                } else if (!v.equals(givenBlobs.get(k))) {
                    Stage.addBlobToStagedAddition(k, givenBlobs.get(k));
                }
            }
            // if
            if (headBlobs.containsKey(k) && !givenBlobs.containsKey(k)) {
                if (v.equals(headBlobs.get(k))) {
                    Stage.addBlobToStagedRemoval(k, v);
                }
            }
        });
        // last two rule check
        givenBlobs.forEach((k, v) -> {
            if (!splitBlobs.containsKey(k) && !headBlobs.containsKey(k)) {
                Stage.addBlobToStagedAddition(k, v);
            }
        });
    }

}
