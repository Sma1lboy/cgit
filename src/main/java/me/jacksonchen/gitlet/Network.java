package me.jacksonchen.gitlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Network {

    public static boolean download(String str) {
        File tempDir = Utils.join(Repository.CURR_DIR, ".temp");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            System.out.println("Failed to create directory: " + tempDir);
            return false;
        }
        String projName = str.substring(str.lastIndexOf("/") + 1, str.indexOf(".git"));

        // Convert GitHub repo URL to download tarball URL
        String tarballUrl = str.replaceAll("\\.git$", "/tarball/main");
        ProcessBuilder downloadProcessBuilder = new ProcessBuilder("curl", "-LJO", tarballUrl)
                .directory(tempDir)
                .redirectErrorStream(true);

        try {
            // Download the tarball
            Process downloadProcess = downloadProcessBuilder.start();
            if (downloadProcess.waitFor() != 0) {
                System.out.println("Download failed.");
                return false;
            }

            // Find the downloaded file
            File[] files = tempDir.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("No files downloaded.");
                return false;
            }

            // Assuming the first .tar.gz file is our downloaded file
            File tarFile = null;
            for (File file : files) {
                if (file.getName().endsWith(".tar.gz")) {
                    tarFile = file;
                    break;
                }
            }

            if (tarFile == null) {
                System.out.println("Downloaded file not found.");
                return false;
            }

            System.out.println(tarFile.getParentFile().toString());
            // Extract the tarball
            System.out.println("tar" + "xvzf" + tarFile.getAbsolutePath() + "-C" +
                    Utils.join(tempDir.getParentFile(), projName).toString());
            File projDir = Utils.join(tempDir.getParentFile(), projName);
            if (projDir.exists()) {
                Main.exitMessage("Current name of project already exit, please create another name");
            }
            if (!projDir.exists() && !projDir.mkdirs()) {
                System.out.println("Failed to create directory: " + projDir);
                return false;
            }
            ProcessBuilder tarBuilder = new ProcessBuilder("tar", "xvzf", tarFile.getAbsolutePath(), "-C",
                    projDir.toString())
                    .directory(tempDir)
                    .redirectErrorStream(true);

            Process tarProcess = tarBuilder.start();
            int exitCode = tarProcess.waitFor();

            // if there only exist folder relate to tar file
            File taredFile = projDir.listFiles()[0];
            moveIndentOfFile(taredFile, projDir);
            if (!taredFile.delete()) {
                Main.exitMessage("Cannot delete file " + projDir.listFiles()[0]);
            }
            removeDir(tempDir);
            return exitCode == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

}
