package nju.quadra.quantra.utils;

import nju.quadra.quantra.AppRunner;

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

    private Process process;
    private OutputHandler outputHandler, errorHandler;

    public static void extractEngine(String engine, String path) throws IOException {
        File filePath = new File(path);
        filePath.mkdirs();
        InputStream is = AppRunner.class.getResourceAsStream("python/" + engine + ".py");
        FileOutputStream os = new FileOutputStream(path + "/" + engine + ".py");
        byte[] buf = new byte[is.available()];
        is.read(buf);
        os.write(buf);
        os.flush();
        is.close();
        os.close();
    }

    public static void extractEngine(String path) throws IOException {
        extractEngine("engine", path);
    }

    public PPAP(String cmd, String path) throws IOException {
        process = Runtime.getRuntime().exec(cmd, null, new File(path));
        // stdout thread
        new Thread(() -> {
            InputStream is = process.getInputStream();
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
        // stderr thread
        new Thread(() -> {
            InputStream is = process.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    if (errorHandler != null) {
                        errorHandler.onOutput(line);
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

    public void setErrorHandler(OutputHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public interface OutputHandler {
        void onOutput(String output);
    }

}
