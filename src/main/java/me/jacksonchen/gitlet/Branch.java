package main.java.me.jacksonchen.gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Branch class help monitor which latest head we are at
 */
public class Branch implements Serializable {
    private String branchName;
    private Commit head; // current branch latest update commit

    public Branch(String branchName, Commit commit) {
        this.branchName = branchName;
        this.head = commit;
    }

    public Commit getHead() {
        return this.head;
    }

    public String getHeadSHA1() {
        return this.head.getSHA1();
    }

    public String getBranchName() {
        return this.branchName;
    }

    /**
     * Return all the commit from current branch.
     * 
     * @return
     */
    public Map<String, Commit> listCommits() {
        Map<String, Commit> map = new HashMap<>();
        Commit curr = this.head;
        while (curr.getDate() != null) {
            map.put(curr.getSHA1(), curr);
            curr = curr.getParent();
        }
        return map;
    }

    public Commit findSplitPoint(Branch branch) {
        Map<String, Commit> commits = listCommits();
        Commit head = branch.getHead();
        while (!commits.containsKey(head.getSHA1())) {
            head = head.getParent();
        }
        return head;
    }
    // gethead
    // getheadSHA1
    // getbranchname
    // create
    // load
    // save

    // static hasbranch
    public static List<Commit> findCommits(String message) {
        List<Branch> branches = getBranches();
        List<Commit> res = new ArrayList<>();
        branches.stream().forEach(branch -> {
            Commit currCommit = branch.getHead();
            for (; currCommit.getDate() != null; currCommit = currCommit.getParent()) {
                if (currCommit.getMessage().equals(message)) {
                    res.add(currCommit);
                }
            }
        });
        return res;
    }

    /**
     * Remove Branch with @branchName from gitlet
     * 
     * @param branchName
     */
    public static void remove(String branchName) {
        File branchFile = Utils.join(Repository.HEAD_REFS_FOLDER, branchName);
        branchFile.delete();
    }

    // it might separate into Branches class
    public static boolean containsBranch(String branchName) {
        File[] branchFiles = Repository.HEAD_REFS_FOLDER.listFiles();
        List<File> res = Arrays.stream(branchFiles).filter(file -> file.getName().equals(branchName)).toList();
        return res.size() == 1;
    }

    public static void showBranches() {
        List<Branch> branches = getBranches();
        Prompt.logTitle("Branches");
        Branch currBranch = findBranchByCommit(getGlobalBranch().getBranchName());
        branches.forEach(branch -> {
            boolean isCurrentBranch = currBranch.getBranchName().equals(branch.getBranchName());
            Prompt.log((isCurrentBranch ? "*" : "") + branch.getBranchName());
        });
    }

    public static void showBranchesByMessage(String message) {
        List<Commit> commits = Branch.findCommits(message);
        commits.stream().forEach(commit -> Prompt.promptLog(commit));
    }

    public static List<Branch> getBranches() {
        File[] branchFiles = Utils.join(Repository.HEAD_REFS_FOLDER).listFiles();
        List<Branch> branches = Arrays.stream(branchFiles).map(branch -> Utils.readObject(branch, Branch.class))
                .toList();
        return branches;
    }

    public static Branch getGlobalBranch() {
        Branch branch = Utils.readObject(Repository.HEAD, Branch.class);
        return branch;
    }

    /**
     * 
     * @param version
     * @return
     */
    public static Branch findBranchByCommit(String version) {
        List<Branch> branches = getBranches();
        for (Branch branch : branches) {
            if (branch.getBranchName().equals(version)) {
                return branch;
            }
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
                    return branch;
                }
                curr = curr.getParent();
            }
        }
        return null;
    }

    /**
     * Get branch by branch name.
     * 
     * @param branchName
     * @return
     */
    public static Branch getBranchByName(String branchName) {
        return Utils.readObject(Utils.join(Repository.HEAD_REFS_FOLDER, branchName), Branch.class);
    }
}
