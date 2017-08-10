package com.changdu.apkpackage.view;

import com.changdu.apkpackage.ApkHandler;
import com.changdu.apkpackage.CmdExecuteException;
import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;
import com.changdu.apkpackage.utlis.FileUtil;
import com.changdu.apkpackage.utlis.LocalFileHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

/**
 * Created by davidleen29 on 2017/8/7.
 */

public class MainFrame extends JFrame {

    MainPanel mainPanel;


    public ConfigData configData;


    private PrintMessageThread outPutThread;

    //屏幕输出结果

    ByteArrayOutputStream byteArrayOutputStream;


    public MainFrame(String title) {
        super(title);


        mainPanel = new MainPanel(new MainPanel.PanelListener() {
            @Override
            public void onJDKPick() {


                File preSelectFile = getPreSelectedFile(configData.jdkHomePath);
                File file = FileUtil.getSelectedDirectory(preSelectFile);
                if (file != null && file.exists()) {
                    configData.jdkHomePath = file.getAbsolutePath() + File.separator;
                    bindData();
                }


            }

            @Override
            public void onStoreFilePick() {

                File preSelectFile = getPreSelectedFile(configData.keyStoreFilePath);
                File file = FileUtil.getSelectedFilePath(preSelectFile);

                if (file != null && file.exists()) {
                    configData.keyStoreFilePath = file.getAbsolutePath();
                    bindData();
                }

            }

            @Override
            public void onPackStart() {

                doPack();
            }

            @Override
            public void onApkPick() {

                File preSelectFile = getPreSelectedFile(configData.apkFilePath);
                File file = FileUtil.getSelectedFilePath(preSelectFile);

                if (file != null && file.exists()) {
                    configData.apkFilePath = file.getAbsolutePath();
                    bindData();
                }
            }

            @Override
            public void onPickPack() {

                File preSelectFile = getPreSelectedFile(configData.apkPackPath);
                File file = FileUtil.getSelectedDirectory(preSelectFile);
                if (file != null && file.exists()) {
                    //解析文件夹  找出所有打包的文件


                    configData.apkPackPath = file.getAbsolutePath() + File.separator;

                    bindData();
                }

            }

            @Override
            public void onPickApkTool() {

                File preSelectFile = getPreSelectedFile(configData.apkToolPath);
                File file = FileUtil.getSelectedDirectory(preSelectFile);
                if (file != null && file.exists()) {
                    configData.apkToolPath = file.getAbsolutePath() + File.separator;
                    bindData();

                }

            }

        });


        setContentPane(mainPanel.getRoot());


        // frame.setExtendedState(MAXIMIZED_BOTH);
        setMaximumSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
        setLocationByPlatform(true);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        //主界面 移除通用监听器  强制退出提示
        for (WindowListener listener : getWindowListeners())
            removeWindowListener(listener);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //点击了退出系统按钮
                int option = JOptionPane.showConfirmDialog(MainFrame.this, "确定要退出吗?", " 提示", JOptionPane.OK_CANCEL_OPTION);
                if (JOptionPane.OK_OPTION == option) {
                    //点击了确定按钮
                    MainFrame.this.dispose();

                    if (outPutThread != null) {
                        outPutThread.setDestroy();
                        outPutThread.interrupt();
                        try {
                            outPutThread.join();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }


                    System.exit(0);
                }
            }
        });


        init();
        pack();


    }


    private File getPreSelectedFile(String filePath) {

        if (StringUtil.isEmpty(filePath))
            return null;
        return new File(filePath);
    }

    private void init() {

        configData = LocalFileHelper.get(ConfigData.class);


        if (configData == null) configData = new ConfigData();
        if (StringUtil.isEmpty(configData.jdkHomePath)) {
            Map<String, String> map = System.getenv();
            String jdkHome = map.get("JAVA_HOME");
            configData.jdkHomePath = jdkHome;
        }

        byteArrayOutputStream = new ByteArrayOutputStream();

        outPutThread = new PrintMessageThread();
        outPutThread.start();
        //读取缓存文件
        bindData();

    }


    private void doPack() {

        if (configData == null) return;

        if (StringUtil.isEmpty(configData.apkFilePath)) {
            mainPanel.showMessage("apk文件未选择");
            return
                    ;
        }


        if (StringUtil.isEmpty(configData.jdkHomePath)) {
            mainPanel.showMessage("jdkHome路径未配置");
            return
                    ;
        }


        if (StringUtil.isEmpty(configData.keyStoreFilePath)) {
            mainPanel.showMessage("签名文件未选择");
            return
                    ;
        }
        if (StringUtil.isEmpty(configData.apkToolPath)) {
            mainPanel.showMessage("Apk打包工具路径未指定");
            return
                    ;
        }

        configData.storepass = mainPanel.getStorePass();
        configData.keypass = mainPanel.getKeyPass();
        configData.alias = mainPanel.getAlias();
        saveToLocal();


        executePack();


    }


    /**
     * 执行批量打包
     */
    private void executePack() {

        mainPanel.setEdiable(false);
        new Thread() {
            @Override
            public void run() {


                byteArrayOutputStream.reset();

                ApkHandler apkHandler = new ApkHandler(configData, byteArrayOutputStream);
//                //第一步 反编译
                try {
                    apkHandler.deCompile();


                    File[] files = new File(configData.apkPackPath).listFiles();
                    //循环打包
                    for (File file : files) {
//        //第二步 资源替换  修改包名
                        apkHandler.replaceRes(file);


                        //第四步 编译打包
                        apkHandler.bundleUp();

                        //第五步 签名
                        apkHandler.signUp();

                        //第六步  对齐
                        apkHandler.zipAlign();

                        //第六步  复制apk到指定目录下。
                        apkHandler.move(file);


                    }

                    apkHandler.clear();


                } catch (CmdExecuteException e) {


                    mainPanel.showMessage(e.getMessage());

                }


                mainPanel.setEdiable(true);
                mainPanel.showMessage("打包结束,请查看输出记录情况...");

            }
        }.start();
    }

    private void printOutput() {

        if (byteArrayOutputStream != null) {

//            try {
            mainPanel.setOutput(byteArrayOutputStream.toString());
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
        }

    }


    public void bindData() {

        saveToLocal();


        mainPanel.setConfigData(configData);

    }

    /**
     * 将当前的配置写入本地文件中
     */
    private void saveToLocal() {
        LocalFileHelper.set(configData);
    }


    /**
     * 输出结果线程
     */
    private class PrintMessageThread extends Thread {
        boolean running = true;

        @Override
        public void run() {


            while (running) {

                printOutput();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }


        }

        public void setDestroy() {
            running = false;
        }
    }

}
