package gitlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Conflict
 */
public class Conflict {
    /**
     * Reslove the conflict from same filepath but 2 different commit version
     * 
     * @param filepath
     * @param blob1
     * @param blob2
     */
    public static void resolve(File filepath, Blob headBlob, String headBranchName, Blob givenBlob,
            String givenBranchName) {

        BufferedWriter writer = new BufferedWriter(null);
        try {
            writer = new BufferedWriter(new FileWriter(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader headReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(headBlob.content)));
        BufferedReader givenReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(givenBlob.content)));
        String headLine = "", givenLine = "";
        boolean isFlag = false;
        StringBuilder headBuf = new StringBuilder(), givenBuf = new StringBuilder();
        try {
            while ((headLine = headReader.readLine()) != null || (givenLine = givenReader.readLine()) != null) {
                if (headLine == null && isFlag) {
                    while (givenLine != null) {
                        writer.write(givenLine);
                        givenLine = givenReader.readLine();
                    }
                    break;
                }
                if (givenLine == null && isFlag) {
                    while (headLine != null) {
                        writer.write(headLine);
                        headLine = headReader.readLine();
                    }
                    break;
                }

                if (headLine != null && givenLine != null && !headLine.equals(givenLine)) {
                    isFlag = false;
                } else if (headLine != null && givenLine != null && headLine.equals(givenLine)) {
                    writer.write(diffHead(headBranchName));
                    writer.write(headBuf.toString());
                    headBuf.setLength(0);
                    writer.write(diffBreak());
                    writer.write(givenBuf.toString());
                    givenBuf.setLength(0);
                    writer.write(diffEnd(givenBranchName));
                }
                if (!isFlag) {
                    writer.write(headLine + "\n");
                } else if (isFlag) {
                    headBuf.append(headLine);
                    givenBuf.append(givenLine);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper method to building diff format
     * 
     * @param branchName
     * @return
     */
    private static String diffHead(String branchName) {
        return "<<<<<<< " + branchName + "\n";
    }

    private static String diffBreak() {
        return "======= \n";
    }

    private static String diffEnd(String branchName) {
        return ">>>>>>> " + branchName + "\n";
    }

}