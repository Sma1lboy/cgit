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

    public static boolean containsBlobs(File file) {
        Blob testBlob = new Blob(file.getName(), file);
        File blobFile = Utils.join(Repository.BLOB_FOLDER, testBlob.getSha1());
        return blobFile.exists();
    }

    public static Blob getBlob(String blobSHA1) {
        return Utils.readObject(Utils.join(Repository.BLOB_FOLDER, blobSHA1), Blob.class);
    }
}
