package gitlet;

import java.io.Serializable;
import java.util.HashMap;
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

}
