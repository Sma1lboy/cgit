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
    private URL url;
    private String name;
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
        File remoteFile = Utils.join(Repository.REMOTES_DIR, name);
        Utils.writeObject(remoteFile, this);
    }

    public void delete() {
        File remoteFile = Utils.join(Repository.REMOTES_DIR, name);
        if (remoteFile.exists()) {
            remoteFile.delete();
        }
    }

    public String getURL() {
        return this.url.toString();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURL(String url) {
        try {
            this.url = new URL(url);
        } catch (Exception e) {
            Main.exitMessage("This is invalid URL");

        }
    }

    @Override
    public String toString() {
        return this.url.toString();
    }

    /*
     * Static helper method
     */

    public static List<Remote> getAll() {
        File[] files = Repository.REMOTES_DIR.listFiles();
        return Arrays.stream(files).map(file -> Utils.readObject(file, Remote.class)).toList();
    }

    public static Remote readRemote(String name) {
        return Utils.readObject(Utils.join(Repository.REMOTES_DIR, name), Remote.class);
    }
}
