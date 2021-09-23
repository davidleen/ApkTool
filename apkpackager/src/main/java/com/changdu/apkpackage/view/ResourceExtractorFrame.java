package com.changdu.apkpackage.view;

import com.changdu.apkpackage.ApkPrintJob;
import com.changdu.apkpackage.ApkResourceHandler;
import com.changdu.apkpackage.utlis.FileUtil;

import javax.swing.*;
import java.io.File;

public class ResourceExtractorFrame extends JFrame {
    public static final String ZH_R_TW = "zh-rTW";
    boolean clearsourcenode=true;
    ResourceExtractorPanel resourceExtractorPanel;



    public ResourceExtractorFrame(String title) {
        super(title);
        resourceExtractorPanel=new ResourceExtractorPanel(new ResourceExtractorPanel.ResourceExtractorListener() {
            @Override
            public void start(String resDirectory, String apkFilePath, String xxx) {


                String[] languagues = new String[]{"en", "es", ZH_R_TW,"pt","fr"};
                String[] relativeValuesPath = new String[]{"/Products/Ereader/res/values", "/Products/ESpain/res/values", "/res/values-zh-rTW","/Products/PortugalReader/res/values","/Products/FrenchReader/res/values"};
                for (int i = 0; i < languagues.length; i++) {
                    String language = languagues[i];

                    String stringFileName = "strings_international.xml";
                    String srcFilePath = resDirectory + File.separator + "values" + File.separator + stringFileName;
                    String destFilePath = resDirectory + File.separator + "values-" + language + File.separator + stringFileName;
                    if(!new File(destFilePath).exists())
                        FileUtil.copyFile(srcFilePath, destFilePath);

                  extractValues(apkFilePath + relativeValuesPath[i], destFilePath);

                }



            }
        }, new ResourceExtractorPanel.ResourceExtractorListener() {
            @Override
            public void start(String destResFilePath, String fromResourcePath, String xxx) {

                //遍历所有drawable 文件夹
                String[] languagues = new String[]{"en", "es","pt","fr", ZH_R_TW,""};

                File res = new File(destResFilePath);
                if (res.isDirectory()) {

                    for (File child : res.listFiles()) {

                        String childDirectoryName = child.getName();
                        if (childDirectoryName.contains("drawable")) {


                            int dividerIndex = childDirectoryName.indexOf("-");
                            String drawbleDirectoryhdpi = dividerIndex > -1 ? childDirectoryName.substring(dividerIndex) : "";


                            File[] drawableFiles = child.listFiles();
                            for (File drawable : drawableFiles) {

                                String drawablename = drawable.getName();
                                for (int i = 0; i < languagues.length; i++) {
                                    String language = languagues[i];
                                    String destDirectory = "drawable" +( language==""?"":("-"+language) )+ drawbleDirectoryhdpi;
                                    String destfilepath = destResFilePath + File.separator + destDirectory + File.separator + drawablename;

                                    String srcFilePath = null;
                                    switch (language) {
                                        case "en":
                                            srcFilePath = fromResourcePath + File.separator + "Products/Ereader/res" + File.separator + childDirectoryName + File.separator + drawablename;


                                            break;
                                        case "es":
                                            srcFilePath = fromResourcePath + File.separator + "Products/ESpain/res" + File.separator + childDirectoryName + File.separator + drawablename;


                                            break;
                                        case ZH_R_TW:
                                        case "":
                                            srcFilePath = fromResourcePath + File.separator + "res" + File.separator + destDirectory + File.separator + drawablename;
                                            break;

                                    }

                                    if (srcFilePath != null) {
                                        FileUtil.copyFile(srcFilePath, destfilepath);
                                        FileUtil.deleteFile(srcFilePath);
                                    }


                                }
                            }
                        }
                    }


                }


            }
        });
        setContentPane(resourceExtractorPanel.getRoot());








    }

    private void extractValues(String fromDirectory, String destFilePath) {
        ApkPrintJob printJob = new ApkPrintJob(resourceExtractorPanel);
        ApkResourceHandler apkResourceHandler = new ApkResourceHandler(fromDirectory, printJob);
        apkResourceHandler.extractStringResource(destFilePath,clearsourcenode);
        printJob.print("资源抽取完成");
    }




}
