package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Commit tree class
 */
public class Commit implements Serializable {

    String sha1; // hash SHA value
    String parentHash;
    // format of timestamp is Sat Nov 11 12:30:00 2017 -0800
    String timestamp;
    String log;
    HashMap<String, String> blobs;

    public Commit() {

    }
}
