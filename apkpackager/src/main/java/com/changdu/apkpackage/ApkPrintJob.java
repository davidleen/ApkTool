package com.changdu.apkpackage;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ApkPrintJob implements IPrintable {


    ByteArrayOutputStream byteArrayOutputStream = null;
    private TextView textView;

    PrintMessageThread printMessageThread;

    String lineSeparator;

    public ApkPrintJob(TextView textView) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        this.textView = textView;
        lineSeparator = java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("line.separator"));
        printMessageThread = new PrintMessageThread();
        printMessageThread.start();


    }

    @Override
    public void print() {

        printMessageThread.interrupt();
    }

    @Override
    public void print(byte[] bytes) {


        writeBytes(bytes);

        printMessageThread.interrupt();

    }

    private void writeBytes(byte[] bytes) {
        synchronized (byteArrayOutputStream) {
            try {
                byteArrayOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void print(String message) {

        print(message.getBytes());


        printMessageThread.interrupt();
    }

    @Override
    public void println(String s) {


        print(lineSeparator);
        print(s);
    }

    @Override
    public void println() {

        print(lineSeparator);


    }

    @Override
    public void clear() {


        byteArrayOutputStream.reset();
        printMessageThread.interrupt();


    }

    @Override
    public void close() {
        if (printMessageThread != null) {
            printMessageThread.setDestroy();
            printMessageThread.interrupt();
            try {
                printMessageThread.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * 输出结果线程
     */
    private class PrintMessageThread extends Thread {
        boolean running = true;

        @Override
        public void run() {


            while (running) {
                final String s;
                synchronized (byteArrayOutputStream) {

                    s = byteArrayOutputStream.toString();
                }


                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(s == null ? "" : s);
                    }
                });


                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {

                }
            }


        }

        public void setDestroy() {
            running = false;
        }
    }
}
