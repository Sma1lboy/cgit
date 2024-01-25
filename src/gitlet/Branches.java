package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The helper static class for Branch operations
 */
public class Branches {
    public static List<Commit> findCommits(String message) {
        List<Branch> branches = Head.getBranches();
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
}
