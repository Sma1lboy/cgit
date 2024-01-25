package gitlet;

import java.io.File;

public class Blobs {

    public static boolean containsBlobs(File file) {
        Blob testBlob = new Blob(file.getName(), file);
        File blobFile = Utils.join(Repositories.BLOB_FOLDER, testBlob.getSha1());

        return blobFile.exists();

    }
}
