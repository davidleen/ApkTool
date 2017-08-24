package com.changdu.apkpackage.utlis;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by HP on 2017/8/8.
 */

public class FileUtil {


    public static void copyFile(String srcPath, String retPath) {
        File file = new File(srcPath);

        if (!file.exists()) {
            return;
        }
        File retFile = new File(retPath);

        if (file.isFile()) {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                File parentFile = retFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }

                fis = new FileInputStream(file);
                fos = new FileOutputStream(retFile);

                byte[] buffer = new byte[1024 * 8];
                int n;
                while ((n = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, n);
                }

            } catch (Exception e) {
                System.out.print(e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        System.out.print(e);
                    }
                }

                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        System.out.print(e);
                    }
                }
            }
        } else if (file.isDirectory()) {
            if (!retFile.exists()) {
                retFile.mkdir();
            }
            String[] fileList;
            fileList = file.list();
            for (int i = 0; i < fileList.length; i++) {
                copyFile(srcPath + "/" + fileList[i], retPath + "/" + fileList[i]);
            }
        }
    }


    public static File getSelectedDirectory() {


        return getSelectedFile(null, JFileChooser.DIRECTORIES_ONLY);
    }


    public static File getSelectedDirectory(File file) {


        return getSelectedFile(file, JFileChooser.DIRECTORIES_ONLY);
    }

    public static File getSelectedFilePath() {

        return getSelectedFile(null, JFileChooser.FILES_ONLY);
    }


    public static File getSelectedFilePath(File file) {

        return getSelectedFile(file, JFileChooser.FILES_ONLY);
    }

    private static File getSelectedFile(File selectFile, int mode) {


        JFileChooser fileChooser = new JFileChooser(".");
        //下面这句是去掉显示所有文件这个过滤器。
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(mode);
        if (selectFile != null)
            fileChooser.setSelectedFile(selectFile);
        int result = fileChooser.showOpenDialog(null);
        File file = null;
        if (result == JFileChooser.APPROVE_OPTION) {

            file = fileChooser.getSelectedFile();

        }
        return file;

    }

    public static void makeDir(File dest) {

        if (dest != null && dest.getParent() != null) {
            File file = new File(dest.getParent());
            file.mkdirs();
        }


    }


    public static void deleteAllFiles(String filePath) {
        deleteFile(new File(filePath));
    }

    /**
     * @param file
     */
    public static void deleteFile(File file) {

        if (file.isDirectory()) {

            File[] children = file.listFiles();
            for (File child : children) {
                deleteFile(child);
            }
        }

        file.delete();

    }
}
