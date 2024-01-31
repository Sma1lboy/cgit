package me.jacksonchen.gitlet;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Remote class to save persistance for remote data
 */
public class Remote implements Serializable {
    URL url;
    String name;
    final String BREAK_SIGN = "://";

    // default remote url looks like
    /**
     * Constructor when instance a remote.
     * 
     * @param name
     * @param url
     */
    public Remote(String name, String url) {
        this.name = name;
        try {
            this.url = new URL(url);

        } catch (Exception e) {
            Main.exitMessage("This is invalid url");
        }
    }

    public void save() {
        File remoteFile = Utils.join(Repository.REMOTE_DIR, name);
        Utils.writeObject(remoteFile, this);
    }

    @Override
    public String toString() {
        return this.url.toString();
    }

    /*
     * Static helper method
     */

    public static List<Remote> getAll() {
        File[] files = Repository.REMOTE_DIR.listFiles();
        return Arrays.stream(files).map(file -> Utils.readObject(file, Remote.class)).toList();
    }
}
