package gitlet;

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
}
