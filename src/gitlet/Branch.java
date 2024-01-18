package gitlet;

import java.io.Serializable;

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

    // gethead
    // getheadSHA1
    // getbranchname
    // create
    // load
    // save

    // static hasbranch
}
