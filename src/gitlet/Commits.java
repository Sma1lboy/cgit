package gitlet;

import java.util.List;

public class Commits {

    public static Commit findByVersion(String version) {
        List<Branch> branches = Branches.getBranches();
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
                    return curr;
                }
                curr = curr.getParent();
            }
        }
        return null;
    }

}
