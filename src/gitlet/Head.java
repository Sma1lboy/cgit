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

    public static List<Branch> getBranches() {
        File[] branchFiles = Utils.join(Repositories.HEAD_REFS_FOLDER).listFiles();
        List<Branch> branches = Arrays.stream(branchFiles).map(branch -> Utils.readObject(branch, Branch.class))
                .toList();
        return branches;
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

    public static void checkout(String version) throws IOException {
        // TODO if there is untrack file, you should track or stash first
        if (containsUntrackFile()) {
            Main.exitMessage("You should clear untrack files");
        }
        if (Stage.containsAdditionFiles()) {
            Main.exitMessage("You should commit all addition file or stash them");
        }
        List<Branch> branches = getBranches();
        for (Branch branch : branches) {
            Commit curr = branch.getHead();
            while (curr.getDate() != null) {
                String commitSHA1 = curr.getSHA1();
                boolean isSame = true;
                for (int i = 0; i <= 7; i++) {
                    if (commitSHA1.charAt(i) != version.charAt(i)) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    // set HEAD pointer to it
                    setGlobalHEAD(version, curr);
                    // maintain root directory
                    maintrainBranch();
                }
                curr = curr.getParent();
            }
        }
    }

    /**
     * jump to new branch, we should clone all file from current commit blobs;
     * 
     * @return
     * @throws IOException
     * @require before we call this method, make sure there is no staging file and
     *          untrack file
     */
    public static void maintrainBranch() throws IOException {
        Commit headCommit = getGlobalHEAD();
        maintainDirectory(headCommit);
    }

    private static void maintainDirectory(Commit commit) throws IOException {
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

    private static boolean containsUntrackFile() {
        File rootDir = Utils.join(Repositories.CURR_DIR);
        return containsUntrackDir(rootDir);
    }

    private static boolean containsUntrackDir(File dir) {
        File[] rootDir = Utils.join(Repositories.CURR_DIR).listFiles();
        Map<String, String> blobs = getGlobalHEAD().getCloneBlobs();
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
}
