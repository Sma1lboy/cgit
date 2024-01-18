package me.gitlet.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Commit tree class
 */
public class Commit implements Serializable {

    String id; // hash SHA value
    String parentHash;
    // format of timestamp is Sat Nov 11 12:30:00 2017 -0800
    String timestamp;
    String log;
    HashMap<String, String> blobs;
}
