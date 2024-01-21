package gitlet;

import java.io.File;
import java.util.HashMap;

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
            File prevCommitFile = Utils.join(Repositories.COMMITS_FOLDER, curr.parentHash);
            curr = Utils.readObject(prevCommitFile, Commit.class);
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
        for (String key : cloneBlobs.keySet()) {
            if (key.equals(filepath.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * jump to new branch, we should clone all file from current commit blobs;
     * 
     * @return
     */
    public static void maintrainBranch() {

    }
}
