package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    public String filename;
    public File filepath;
    public byte[] content;
    private String sha1;

    public Blob(String filename, File filepath) {
        this.filepath = filepath;
        content = Utils.readContents(filepath);
        this.filename = filename;
        this.sha1 = Utils.sha1(content);
    }

    public String getSha1() {
        return this.sha1;
    }

}
