package me.jacksonchen.gitlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Network {

    public static void download(String url) {

        String downloadURL = buildDownloadUrl(url);
        File tempFolder = createTempDir();
        if (!downloadTarball(downloadURL, tempFolder)) {
            Main.exitMessage("Downloading " + url + "failed");
        }
        File downloadedFile = findDownloadedFile(tempFolder);

        String repoName = parseProjectName(url);
        File repoFolder = buildRepoFolder(repoName);

        extractTarball(downloadedFile, repoFolder);

        File extractedFolder = repoFolder.listFiles()[0];

        organizeDownloadedFiles(extractedFolder, repoFolder);

        removeDir(extractedFolder);
        removeDir(tempFolder);
    }

    private static File buildRepoFolder(String repoName) {
        File repoFolder = Utils.join(Repository.CURR_DIR, repoName);
        if (repoFolder.exists()) {
            Main.exitMessage("The folder already exist! " + repoFolder);
        }
        repoFolder.mkdirs();
        return repoFolder;
    }

    private static void removeDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                removeDir(file);
            }
            file.delete();
        }
        dir.delete();
    }

    private static void moveIndentOfFile(File root, File targetDir) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File newDir = new File(targetDir, file.getName());
                    newDir.mkdir();
                    // Recursively move files in subdirectories
                    moveIndentOfFile(file, newDir);
                    // After moving files, delete the now-empty subdirectory
                    if (!file.delete()) {
                        System.out.println("Failed to delete directory: " + file);
                    }
                } else {
                    // Move the file to the parent directory
                    File newFile = new File(targetDir, file.getName());
                    if (!file.renameTo(newFile)) {
                        System.out.println("Failed to move file: " + file);
                    }
                }
            }
        }
    }

    private static File createTempDir() {
        File tempDir = Utils.join(Repository.CURR_DIR, ".temp");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            System.out.println("Failed to create directory: " + tempDir);
            return null;
        }
        return tempDir;
    }

    private static String parseProjectName(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.indexOf(".git"));
    }

    private static String buildDownloadUrl(String repoUrl) {
        return repoUrl.replaceAll("\\.git$", "/tarball/main");
    }

    private static boolean downloadTarball(String tarballUrl, File downloadDir) {

        ProcessBuilder downloadProcessBuilder = new ProcessBuilder("curl", "-LJO", tarballUrl)
                .directory(downloadDir)
                .redirectErrorStream(true);
        try {
            Process downloadProcess = downloadProcessBuilder.start();
            return downloadProcess.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static File findDownloadedFile(File dir) {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".tar.gz"));
        return files != null && files.length > 0 ? files[0] : null;
    }

    private static boolean extractTarball(File tarFile, File destDir) {
        ProcessBuilder tarBuilder = new ProcessBuilder("tar", "xvzf", tarFile.getAbsolutePath(), "-C",
                destDir.getAbsolutePath())
                .directory(destDir)
                .redirectErrorStream(true);
        try {
            Process tarProcess = tarBuilder.start();
            return tarProcess.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void organizeDownloadedFiles(File root, File targetDir) {
        // Logic for moving and organizing files
        // Similar to existing `moveIndentOfFile` method
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File newDir = new File(targetDir, file.getName());
                    newDir.mkdir();
                    // Recursively move files in subdirectories
                    moveIndentOfFile(file, newDir);
                    // After moving files, delete the now-empty subdirectory
                    if (!file.delete()) {
                        System.out.println("Failed to delete directory: " + file);
                    }
                } else {
                    // Move the file to the parent directory
                    File newFile = new File(targetDir, file.getName());
                    if (!file.renameTo(newFile)) {
                        System.out.println("Failed to move file: " + file);
                    }
                }
            }
        }
    }
}
