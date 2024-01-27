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

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(filepath));
            BufferedReader headReader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(headBlob.content)));
            BufferedReader givenReader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(givenBlob.content)));
            String headLine = headReader.readLine(), givenLine = givenReader.readLine();

            boolean isFlag = false;
            StringBuilder headBuf = new StringBuilder(), givenBuf = new StringBuilder();
            // TODO || givenline havent load
            while (headLine != null || givenLine != null) {
                if (headLine == null) {
                    while (givenLine != null) {
                        writer.write(givenLine + "\n");
                        givenLine = givenReader.readLine();
                    }
                    if (isFlag) {
                        writer.write(diffHead(headBranchName));
                        writer.write(headBuf.toString());
                        writer.write(diffBreak());
                        writer.write(givenBuf.toString());
                        writer.write(diffEnd(givenBranchName));
                    }
                    break;
                }
                if (givenLine == null) {
                    while (headLine != null) {
                        writer.write(headLine + "\n");
                        headLine = headReader.readLine();
                    }
                    if (isFlag) {
                        writer.write(diffHead(headBranchName));
                        writer.write(headBuf.toString());
                        writer.write(diffBreak());
                        writer.write(givenBuf.toString());
                        writer.write(diffEnd(givenBranchName));
                    }

                    break;
                }

                if (headLine != null && givenLine != null && !headLine.equals(givenLine)) {
                    isFlag = true;
                } else if (headLine != null && givenLine != null && headLine.equals(givenLine) && isFlag) {
                    writer.write(diffHead(headBranchName));
                    writer.write(headBuf.toString());
                    headBuf.setLength(0);
                    writer.write(diffBreak());
                    writer.write(givenBuf.toString());
                    givenBuf.setLength(0);
                    writer.write(diffEnd(givenBranchName));
                    isFlag = false;
                }
                if (!isFlag) {
                    writer.write(headLine + "\n");
                } else if (isFlag) {
                    headBuf.append(headLine + "\n");
                    givenBuf.append(givenLine + "\n");
                }

                headLine = headReader.readLine();
                givenLine = givenReader.readLine();
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