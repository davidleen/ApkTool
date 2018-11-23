package com.changdu.apkpackage.view;

import com.changdu.apkpackage.*;
import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;
import com.changdu.apkpackage.entity.StoreFileConfig;
import com.changdu.apkpackage.entity.StoreFileHistory;
import com.changdu.apkpackage.utlis.FileUtil;
import com.changdu.apkpackage.utlis.LocalFileHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by davidleen29 on 2017/8/7.
 */

public class MainFrame extends JFrame {

    public static final String VERSION_PREFIX = "_V";
    MainPanel mainPanel;


    public ConfigData configData;

    //打印接口
    private IPrintable printJob;


    ExecutorService executor;

    public MainFrame(String title) {
        super(title);
        executor = Executors.newFixedThreadPool(3);


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
                    configData.alias="";
                    configData.keypass="";
                    configData.storepass="";
                    StoreFileHistory history = LocalFileHelper.get(StoreFileHistory.class);
                    if(history!=null) {
                        StoreFileConfig config =  history.getItem(configData.keyStoreFilePath);
                        if(config!=null)
                        {
                            configData.alias=config.alias;
                            configData.keypass=config.keypass;
                            configData.storepass=config.storepass;
                        }
                    }


                    bindData();
                }

            }

            @Override
            public void onJobStart(int type, MainPanel.Options options) {


                preWork();


                switch (type) {
                    case MainPanel.PanelListener.WORK_TYPE_PACK:
                        doPack(options);

                        break;
                    case MainPanel.PanelListener.WORK_TYPE_NORMAL:
                        if (!checkFirst(options)) {
                            return;
                        }
                        doNormalInThread(options);
                        break;


                }


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

            @Override
            public void onPickChannelFile() {

                File preSelectFile = getPreSelectedFile(configData.channelFilePath);
                File file = FileUtil.getSelectedFilePath(preSelectFile);
                if (file != null && file.exists()) {
                    configData.channelFilePath = file.getAbsolutePath()  ;
                    bindData();

                }


            }
        });


        setContentPane(mainPanel.getRoot());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initMenuBar();
            }
        });

        printJob = new ApkPrintJob(mainPanel);


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

                    printJob.close();


                    System.exit(0);
                }
            }
        });


        init();
        pack();


    }

    private void initMenuBar() {
        JMenuBar menuBar;

        menuBar = new JMenuBar();

        JMenu menu;


        menu = new JMenu("开发使用");
        menuBar.add(menu);

        JMenuItem menuItem;

        menuItem = new JMenuItem("解压APK包");

        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                decompileApk();
            }
        });

        menuItem = new JMenuItem("打包APK包");

        menu.add(menuItem);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                bundleUpApk();


            }
        });

        menuItem = new JMenuItem("查看签名信息");

        menu.add(menuItem);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                viewSignInfo();


            }
        });

        menuItem = new JMenuItem("清除框架缓存");

        menu.add(menuItem);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


              clearFramework();


            }
        });
        setJMenuBar(menuBar);
    }


    private void preWork() {


        if (configData == null) configData = new ConfigData();
        configData.storepass = mainPanel.getStorePass();
        configData.keypass = mainPanel.getKeyPass();
        configData.alias = mainPanel.getAlias();


        if(!StringUtil.isEmpty(configData.keyStoreFilePath))
             saveKeyStoreFileHistory();

        saveToLocal();

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
            if (!StringUtil.isEmpty(jdkHome))
                configData.jdkHomePath = jdkHome + File.separator;
        }


        //读取缓存文件
        bindData();

    }


    private boolean checkFirst(MainPanel.Options options) {

        if (StringUtil.isEmpty(configData.jdkHomePath)) {
            mainPanel.showMessage("jdkHome路径未配置");
            return false
                    ;
        }

        if (StringUtil.isEmpty(configData.apkFilePath)) {
            mainPanel.showMessage("apk文件未选择");
            return false;
        }
        if (StringUtil.isEmpty(configData.apkToolPath)) {
            mainPanel.showMessage("Apk打包工具路径未指定");
            return false
                    ;
        }

        if (options == null) return true;

        if (options.sign && StringUtil.isEmpty(configData.keyStoreFilePath)) {
            mainPanel.showMessage("签名文件未选择");
            return false
                    ;
        }

        String versionCode = mainPanel.getVersionCode();
        String versionName = mainPanel.getVersionName();

        if (options.changeVersion && (StringUtil.isEmpty(versionCode) || StringUtil.isEmpty(versionName))) {
            mainPanel.showMessage("请输入版本号CODE NAME");
            return false
                    ;
        }


        String packageName = mainPanel.getPackageName();
        if (options.changePackage && StringUtil.isEmpty(packageName)) {
            mainPanel.showMessage("请输入版本号包名");
            return false
                    ;
        }


        return true;

    }

    /**
     * 解压apk包
     */
    private void decompileApk() {

        if (!checkFirst(null)) return;


        mainPanel.setEdiable(false);
        executor.execute(new Runnable() {


            public void run() {

                ApkHandler apkHandler = new ApkHandler(configData, printJob);

                printJob.println("开始解包:" + configData.apkFilePath);
                printJob.print();

                File apkFile = new File(configData.apkFilePath);
                String apkDecompiledDirectory = apkFile.getParent() + File.separator + "temp" + File.separator;
                FileUtil.deleteAllFiles(apkDecompiledDirectory);
                try {
                    apkHandler.deCompile(configData.apkFilePath, apkDecompiledDirectory);
                } catch (CmdExecuteException e) {
                    e.printStackTrace();
                }
                printJob.print();
                //临时文件未生成 失败
                if (!new File(apkDecompiledDirectory).exists()) {

                    showMessage("反编译失败。。。");
                    return;


                }

                printJob.println("解包成功，路径：" + apkDecompiledDirectory);

                int option = JOptionPane.showConfirmDialog(MainFrame.this, "文件路径在:\n" + apkDecompiledDirectory + ",  \n是否前往文件夹?", "操作成功", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        Desktop.getDesktop().open(new File(apkDecompiledDirectory));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mainPanel.setEdiable(true);

            }
        });


    }

    /**
     * 合成apk包
     */
    private void bundleUpApk() {

        if (!checkFirst(null)) return;

        executor.execute(new Runnable() {


            public void run() {

                ApkHandler apkHandler = new ApkHandler(configData, printJob);

                printJob.println("开始打包:" + configData.apkFilePath);
                printJob.print();

                File apkFile = new File(configData.apkFilePath);
                String apkDecompiledDirectory = apkFile.getParent() + File.separator + "temp" + File.separator;

                if (!new File(apkDecompiledDirectory).exists()) {
                    showMessage("apkapk解压后的文件夹不存在:" + apkDecompiledDirectory);
                    return;
                }

                try {
                    apkHandler.bundleUp(apkDecompiledDirectory);
                } catch (CmdExecuteException e) {
                    e.printStackTrace();
                }
                printJob.print();
                String unSignApkFilePath = apkDecompiledDirectory + "dist" + File.separator + apkFile.getName();

                if (!new File(unSignApkFilePath).exists()) {
                    showMessage("合成未签名apk失败。。。");
                    return;
                }

                printJob.println("打包apk成功，路径：" + unSignApkFilePath);

                int option = JOptionPane.showConfirmDialog(MainFrame.this, "文件路径在:\n" + unSignApkFilePath + ",  \n是否前往文件夹?", "操作成功", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        Desktop.getDesktop().open(new File(unSignApkFilePath).getParentFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mainPanel.setEdiable(true);

            }
        });

    }


    private void  clearFramework()
    {

        executor.execute(new Runnable() {


            public void run() {

                ApkHandler  apkHandler = new ApkHandler(configData, printJob);

                printJob.println();

                try {
                    apkHandler.clearFramework();
                } catch (CmdExecuteException e) {
                    e.printStackTrace();
                }

                mainPanel.setEdiable(true);

            }
        });

    }

    /**
     * 查看签名信息
     */
    public void viewSignInfo() {
        if (StringUtil.isEmpty(configData.keyStoreFilePath)) {
            mainPanel.showMessage("签名文件未选择");
            return;

        }

        preWork();


        executor.execute(new Runnable() {


            public void run() {

                ApkHandler  apkHandler = new ApkHandler(configData, printJob);

                printJob.println();

                try {
                    apkHandler.viewSignInfo();
                } catch (CmdExecuteException e) {
                    e.printStackTrace();
                }

                mainPanel.setEdiable(true);

            }
        });
    }


    private void doNormalInThread(final MainPanel.Options options) {

        mainPanel.setEdiable(false);
        executor.execute(new Runnable() {

            @Override
            public void run() {


                printJob.clear();
                printJob.println("=============执行普通处理====================");
                printJob.println();
                try {
                    doNormal(options);
                } catch (CmdExecuteException e) {
                    e.printStackTrace();

                    mainPanel.showMessage(e.getMessage());

                }
                mainPanel.setEdiable(true);
            }
        });

    }


    /**
     * 执行普通操作   改包名 or 修改版本 or 签名  or 对齐
     */
    private void doNormal(MainPanel.Options options) throws CmdExecuteException {


        if (options.changePackage || options.sign || options.changeVersion || options.align) {

            ApkHandler apkHandler = new ApkHandler(configData, printJob);


            String outputFilePath = configData.apkFilePath;

            File apkFile = new File(outputFilePath);
            //最终目标路径
            String apkDestDirectory = apkFile.getParent() + File.separator + "out" + File.separator;
            FileUtil.deleteAllFiles(apkDestDirectory);


            if (options.changeVersion || options.changePackage) {


                String apkDecompiledDirectory = apkFile.getParent() + File.separator + "temp" + File.separator;
                FileUtil.deleteAllFiles(apkDecompiledDirectory);

                apkHandler.deCompile(configData.apkFilePath, apkDecompiledDirectory);


                //临时文件未生成 失败
                if (!new File(apkDecompiledDirectory).exists()) {

                    showMessage("反编译失败。。。");
                    return;

                }

                ApkResourceHandler apkResourceHandler = new ApkResourceHandler(apkDecompiledDirectory, printJob);

                if (options.changePackage) {
                    String packageName = mainPanel.getPackageName();

                    apkResourceHandler.changePackageName(packageName);
                }
                if (options.changeVersion) {
                    String newVersionCode = mainPanel.getVersionCode();
                    String newVersionName = mainPanel.getVersionName();
                    apkResourceHandler.changeVersionCodeAndName(newVersionCode, newVersionName);
                }

                apkHandler.bundleUp(apkDecompiledDirectory);


                String unSignApkFilePath = apkDecompiledDirectory + "dist" + File.separator + apkFile.getName();

                if (!new File(unSignApkFilePath).exists()) {
                    showMessage("合成未签名apk失败。。。");
                    return;
                }

                outputFilePath = apkDestDirectory + apkFile.getName();
                boolean result = FileUtil.move(unSignApkFilePath, outputFilePath);
                printJob.println("文件移动：" + outputFilePath + ",result:" + result);


            }


            if(options.pickChannelFile)
            {

                apkHandler.appendFileToApk(outputFilePath,  configData.channelFilePath);


            }


            if (options.sign) {

                String signedApkFilePath = null;

                signedApkFilePath = apkHandler.signUp(outputFilePath);

                outputFilePath = signedApkFilePath;

                printJob.print();


                if (!new File(signedApkFilePath).exists()) {

                    showMessage("apk包签名失败");
                    return;
                }
            }


            if (options.align) {
                //第六步  对齐
                String alignedApkFilePath = null;

                alignedApkFilePath = apkHandler.zipAlign(outputFilePath);


                outputFilePath = alignedApkFilePath;
                printJob.print();

                if (!new File(alignedApkFilePath).exists()) {

                    showMessage("apk包对齐失败");
                    return;
                }
            }


            String finalPath = apkFile.getParent() + File.separator + "out" + File.separator + new File(outputFilePath).getName();
            if (!outputFilePath.equals(finalPath)) {

                boolean result = FileUtil.move(outputFilePath, finalPath);
                printJob.println("文件移动：" + finalPath + ",result:" + result);
                outputFilePath = finalPath;
            }


            int option = JOptionPane.showConfirmDialog(this, "文件路径在:\n" + outputFilePath + ",  \n是否前往文件夹?", "操作成功", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Desktop.getDesktop().open(new File(outputFilePath).getParentFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } else {
            showMessage("普通方式至少选择一种，修改包名or修改版本号or签名or对齐   ");
        }


    }


    private void doPack(MainPanel.Options options) {

        if (!checkFirst(options)) {
            return;
        }

        executePack(options);


    }


    /**
     * 查找打包文件底下所有的配置文件
     *
     * @param file
     * @return
     */
    public static  java.util.List<File> findAllFileToPack(File file) {


        if (!file.isDirectory()) return null;
        java.util.List list = new ArrayList();
        if (file.getName().startsWith("com.")) {
            list.add(file);
            return list;
        }

        File[] files = file.listFiles();
        for (File temp : files) {

            java.util.List<File> result = findAllFileToPack(temp);
            if (result != null) {
                list.addAll(result);
            }
        }

        return list;


    }

    /**
     * 执行批量打包
     */
    private void executePack(final MainPanel.Options options) {

        mainPanel.setEdiable(false);
        executor.execute(new Runnable() {

            @Override
            public void run() {

                printJob.clear();
                printJob.println("=============执行多产品打包处理====================");
                printJob.println();
                ApkHandler apkHandler = new ApkHandler(configData, printJob);

//                {
//                    File[] files = new File(configData.apkPackPath).listFiles();
//                    for (File file : files) {
//                        Map<String,String > channel=ChannelHelper.getChannelMap(file,printJob);
//                    }
//                }
//                //第一步 反编译
                try {


                    File apkFile = new File(configData.apkFilePath);
                    //最终目标路径
                    String apkDestDirectory = apkFile.getParent() + File.separator + "out" + File.separator;
                    FileUtil.deleteAllFiles(apkDestDirectory);

                    String apkDecompiledDirectory = apkFile.getParent() + File.separator + "temp" + File.separator;
                    FileUtil.deleteAllFiles(apkDecompiledDirectory);
                    apkHandler.deCompile(configData.apkFilePath, apkDecompiledDirectory);
                    printJob.print();
                    //临时文件未生成 失败
                    if (!new File(apkDecompiledDirectory).exists()) {

                        showMessage("反编译失败。。。");
                        return;


                    }


                    List<File> allFileToPack = findAllFileToPack(new File(configData.apkPackPath));
                    File[] files = new File[allFileToPack.size()];
                    allFileToPack.toArray(files);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault());
                    String dateTime= formatter.format(Calendar.getInstance().getTime());

                    ApkResourceHandler apkResourceHandler = new ApkResourceHandler(apkDecompiledDirectory, printJob);



                    String apkFileName = apkFile.getName();
                    //找出名字
                    int index=apkFileName.indexOf("-");
                   String  apkName=index==-1?apkFileName:apkFileName.substring(0,index);

//                    //找出默认文件名上版本号。
//
                    String versionPathFromFileName="";

                  int indexVS=  apkFileName.toLowerCase().indexOf(VERSION_PREFIX.toLowerCase());
                  if(indexVS>-1) {
                      String versionString=apkFileName.substring(indexVS+ VERSION_PREFIX.length());
                      int indexVEnd =versionString .indexOf("_");
                      if(indexVEnd>-1)
                      {
                          versionPathFromFileName= VERSION_PREFIX +versionString.substring(0,indexVEnd);
                      }
                  }



                    //循环打包
                    for (File file : files) {

                        if (!file.isDirectory()) continue;



                        Map<String, String> channel = ChannelHelper.getChannelMap(file, printJob);


                        apkResourceHandler.resetResources();

                        // 资源替换  修改包名
                        printJob.println("资源替换");


                        apkResourceHandler.replace(file);


                        File parentFile = file.getParentFile();
                        String appName = parentFile.getName();
                        apkResourceHandler.changeApkName(appName, printJob);



                        //合成后未签名的包地址。
                        String versionPath="";
                        String unSignApkFilePath = apkDecompiledDirectory + "dist" + File.separator +appName+"-"+ file.getName();
                        if (options.changeVersion) {
                            String versionCode = mainPanel.getVersionCode();
                            String versionName = mainPanel.getVersionName();
                            versionPath= VERSION_PREFIX + versionName;
                            unSignApkFilePath = unSignApkFilePath +versionPath ;

                            apkResourceHandler.changeVersionCodeAndName(versionCode, versionName);

                        }else
                        {
                            versionPath=versionPathFromFileName;
                        }

                        unSignApkFilePath = unSignApkFilePath+"-"+dateTime + ".apk";




                        //  编译打包
                        apkHandler.bundleUp(apkDecompiledDirectory, unSignApkFilePath);
                        printJob.print();

                        if (!new File(unSignApkFilePath).exists()) {

                            showMessage("apk重新打包失败");
                            return;
                        }


                        File appendedFile = new File(file, Constant.APPENDIX_FILE_PATH);
                        if (appendedFile.exists()) {
                            if (appendedFile.isDirectory()) {

                                for (File append : appendedFile.listFiles()) {


                                    apkHandler.appendFileToApk(unSignApkFilePath, append.getPath());

                                }

                            }

                        }


                        //没有渠道  循环打包一次， 否则 循环打包 渠道次数
                        int loopTime = channel != null && channel.size() > 0 ? channel.size() : 1;

                        //抓取出渠道键对值
                        String[] keys = null;
                        if (channel != null) {
                            keys = new String[channel.size()];
                            channel.keySet().toArray(keys);
                        }


                        //多渠道处理
                        for (int i = 0; i < loopTime; i++) {

                            String outPutFile = unSignApkFilePath;

                            String chanelName="";

                            if (keys != null && keys.length > 0) {

                                //多渠道 ，每个apk 都需要独立复制一份
                                String newOutPutFile = ApkHandler.splitFileName(outPutFile, "_" + keys[i]);
                                chanelName="_" + keys[i];
                                FileUtil.copyFile(outPutFile, newOutPutFile);

                                //渠道文件生成
                                String channelFile = ChannelHelper.createChannelFile(apkDecompiledDirectory, channel.get(keys[i]));
                                //追加到apk文件中
                                apkHandler.appendFileToApk(newOutPutFile, channelFile);


                                outPutFile = newOutPutFile;

                            }



                            {
                                if (options.sign) {
                                    //第五步 签名
                                    String signedApkFilePath = apkHandler.signUp(outPutFile);
                                    outPutFile = signedApkFilePath;
                                    printJob.print();


                                    if (!new File(signedApkFilePath).exists()) {

                                        showMessage("apk包签名失败");
                                        return;
                                    }

                                }
                            }


                            {
                                if (options.align) {

                                    //第六步  对齐
                                    String alignedApkFilePath = apkHandler.zipAlign(outPutFile);

                                    outPutFile = alignedApkFilePath;
                                    printJob.print();

                                    if (!new File(alignedApkFilePath).exists()) {

                                        showMessage("apk包对齐失败");
                                        return;
                                    }
                                }
                            }

                            {
                                //第六步  复制apk到指定目录下。
                                String finalFilePath = apkDestDirectory +  apkName+versionPath+"-"+dateTime+chanelName+".apk";

                                boolean result = FileUtil.move(outPutFile, finalFilePath);
                                printJob.println("文件移动：" + finalFilePath + ",result:" + result);
                            }


                        }


                    }

                    apkHandler.clear(apkDestDirectory);

                    printJob.print();


                    int option = JOptionPane.showConfirmDialog(MainFrame.this, "文件路径在:\n" + apkDestDirectory + ",  \n是否前往文件夹?", "操作成功", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        try {
                            Desktop.getDesktop().open(new File(apkDestDirectory));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mainPanel.setEdiable(true);

                } catch (CmdExecuteException e) {


                    mainPanel.showMessage(e.getMessage());

                }


            }
        });
    }


    private void showMessage(String message) {
        mainPanel.setEdiable(true);
        mainPanel.showMessage(message);
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

    private void saveKeyStoreFileHistory()
    {
        if(configData==null) return;
        StoreFileHistory history=  LocalFileHelper.get(StoreFileHistory.class );
        if(history==null) history=new StoreFileHistory();
        StoreFileConfig storeFileConfig=new StoreFileConfig();
        storeFileConfig.keyStoreFilePath=configData.keyStoreFilePath;
        storeFileConfig.storepass=configData.storepass;
        storeFileConfig.keypass=configData.keypass;
        storeFileConfig.alias=configData.alias;
        history.addItem(storeFileConfig);
        LocalFileHelper.set(history);
    }


}
