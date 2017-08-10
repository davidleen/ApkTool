package com.changdu.apkpackage.utlis;

import java.io.*;

/**
 * 本地缓存文件
 *
 */
public class LocalFileHelper {
    public static final String path = "localFile";


    static {

        File file = new File(path);

        file.mkdirs();

    }


    public static <T> void set(T object) {

        try {

            FileOutputStream fos = new FileOutputStream(new File(path, object.getClass().getName()));
            ObjectOutputStream obs = new ObjectOutputStream(fos);
            obs.writeObject(object);
            obs.close();
            fos.close();


        } catch (IOException io) {
            io.printStackTrace();
        }


    }

    public static void writeString(String fileName, String message) {

        try {

            FileOutputStream fos = new FileOutputStream(new File(path, fileName), true);
            ObjectOutputStream obs = new ObjectOutputStream(fos);

            obs.writeObject(message);


            obs.close();
            fos.close();


        } catch (IOException io) {
            io.printStackTrace();
        }


    }

    public static final <T> T get(Class<T> tClass) {

        T result = null;
        try {

            FileInputStream fos = new FileInputStream(new File(path, tClass.getName()));
            ObjectInputStream ois = new ObjectInputStream(fos);
            result = (T) ois.readObject();
            ois.close();
            fos.close();


        } catch (IOException io) {
            io.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return result;
    }


    public static final void printThrowable(Throwable e) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(path, "log_.txt"));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (fos != null) {
            PrintStream printStream = new PrintStream(fos);
            e.printStackTrace(printStream);
            printStream.close();
            try {
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }


}