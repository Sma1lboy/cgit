package gitlet;

import java.io.File;

public class Blobs {

    public static boolean containsBlobs(File file) {
        Blob testBlob = new Blob(file.getName(), file);
        File blobFile = Utils.join(Repositories.BLOB_FOLDER, testBlob.getSha1());
        return blobFile.exists();
    }

    public static Blob getBlob(String blobSHA1) {
        return Utils.readObject(Utils.join(Repositories.BLOB_FOLDER, blobSHA1), Blob.class);
    }
}
