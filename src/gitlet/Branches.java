package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The helper static class for Branch operations
 */
public class Branches {
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
        File branchFile = Utils.join(Repositories.HEAD_REFS_FOLDER, branchName);
        branchFile.delete();
    }

    // it might separate into Branches class
    public static boolean containsBranch(String branchName) {
        File[] branchFiles = Repositories.HEAD_REFS_FOLDER.listFiles();
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
        List<Commit> commits = Branches.findCommits(message);
        commits.stream().forEach(commit -> Prompt.promptLog(commit));
    }

    public static List<Branch> getBranches() {
        File[] branchFiles = Utils.join(Repositories.HEAD_REFS_FOLDER).listFiles();
        List<Branch> branches = Arrays.stream(branchFiles).map(branch -> Utils.readObject(branch, Branch.class))
                .toList();
        return branches;
    }

    public static Branch getGlobalBranch() {
        Branch branch = Utils.readObject(Repositories.HEAD, Branch.class);
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
}
