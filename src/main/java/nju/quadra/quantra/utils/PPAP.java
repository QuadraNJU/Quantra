package nju.quadra.quantra.utils;

import java.io.*;

/**
 * I have a pen
 * I have an apple
 * Ugh! Apple-Pen!
 * I have a pen
 * I have a pineapple
 * Ugh! Pineapple-Pen!
 * Apple-Pen!
 * Pineapple-Pen!
 * Ugh! Pen-Pineapple-Apple-Pen!
 */
public class PPAP {

    private final String rootPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    private Process process;
    private OutputHandler outputHandler;

    public PPAP(String cmd, String path) throws IOException {
        process = Runtime.getRuntime().exec(cmd, null, new File(rootPath + '/' + path));
        InputStream is = process.getInputStream();
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    if (outputHandler != null) {
                        outputHandler.onOutput(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void waitEnd() {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendInput(String input) {
        if (process != null) {
            try {
                OutputStream os = process.getOutputStream();
                os.write(input.getBytes("UTF-8"));
                os.write('\n');
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOutputHandler(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }

    public interface OutputHandler {
        void onOutput(String output);
    }

}
