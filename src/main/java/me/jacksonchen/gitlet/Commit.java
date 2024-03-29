package me.jacksonchen.gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Commit tree class
 */
public class Commit implements Serializable {

    private String sha1; // hash SHA value
    private String parentHash;
    // format of timestamp is Sat Nov 11 12:30:00 2017 -0800
    private String timestamp;
    private String message;
    /**
     * the current commit snapshot of blobs
     * [file path, SHA1 of blobs]
     */
    private HashMap<String, String> blobs;

    private boolean initFlag = false;

    public Commit(String message, String parent, HashMap<String, String> blobs, boolean initial) {
        this.message = message;
        this.parentHash = parent;
        this.blobs = blobs;
        List<String> list = new ArrayList<>(blobs.values());
        String blobFileName = "";
        for (String blob : list) {
            blobFileName += blob;
        }
        this.timestamp = generatedDate(initial);
        this.sha1 = Utils.sha1("COMMIT" + message + blobFileName);
    }

    private String generatedDate(boolean init) {
        TimeZone tz = TimeZone.getTimeZone("PST");
        Calendar cal = Calendar.getInstance(tz);
        if (init == true) {
            cal.setTimeInMillis(0);
        }
        DateFormat sdf = new SimpleDateFormat("EEE LLL d HH:mm:ss y Z");
        sdf.setTimeZone(tz);
        return sdf.format(cal.getTime());
    }

    /**
     * Init commit construction
     * 
     * @param message
     * @param map
     */
    public Commit(String message, HashMap<String, String> blobs) {
        initFlag = true;
        this.message = message;
        this.blobs = blobs;
        this.sha1 = Utils.sha1("COMMIT" + this.message);
    }

    public String getSHA1() {
        return this.sha1;
    }

    public void save() {
        File commitFile = Utils.join(Repository.COMMITS_FOLDER, this.sha1);
        Utils.writeObject(commitFile, this);
    }

    public Commit getParent() {
        File parentFile = Utils.join(Repository.COMMITS_FOLDER, parentHash);
        return Utils.readObject(parentFile, Commit.class);
    }

    public String getDate() {
        return this.timestamp;
    }

    public HashMap<String, String> getCloneBlobs() {
        return new HashMap<>(this.blobs);
    }

    public String getMessage() {
        return this.message;
    }

    public static Commit findByVersion(String version) {
        List<Branch> branches = Branch.getBranches();
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
