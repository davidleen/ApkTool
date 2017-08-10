package com.changdu.apkpackage;

import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;
import com.changdu.apkpackage.utlis.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by davidleen29 on 2017/8/7.
 */

public class ApkHandler {


    String keystorePath;
    String key_pass;
    String store_pass;
    String alias;


    //解析生成的临时文件夹， 完成后 移除
    public static String tempFileDirectory = "";
    public String apkToolDirectory = "E:\\开发工具\\反编译\\";


    public static final String RELATIVE_PATH_JAR_SIGN = "bin/jarsigner.exe";


    private String jarSignerPath;
    private OutputStream outputStream;

    /**
     * apk原包所在的文件夹
     */
    private String apkDirectory = "";



    /**
     * apk生成包所在的文件夹
     */
    private String apkDestDirectory = "";

    public String apkName = "";
    public String apkPath;


    ApkResourceHandler apkResourceHandler;
    /**
     * 指定property文件
     */
    private static final String PROPERTY_FILE = "cmds/apk.properties";
    private static String DECODE = "apktool d -f %s  -o %s";
    private static String BUNDLE_UP = "apktool b   %s ";

    //签名处理。jarsigner -verbose -keystore PATH/TO/YOUR_RELEASE_KEY.keystore -storepass YOUR_STORE_PASS -keypass YOUR_KEY_PASS PATH/TO/YOUR_UNSIGNED_PROJECT.apk YOUR_ALIAS_NAME
    private static String SIGN = "  -verbose -keystore %s  -storepass %s -keypass %s   %s   %s    ";

//    static {
//        Properties properties = new Properties();
//        ClassLoader classLoader = ApkHandler.class.getClassLoader();
//        InputStream resourceAsStream = classLoader.getResourceAsStream(PROPERTY_FILE);
//        try {
//            properties.load(resourceAsStream);
//
//            //  BUNDLE_UP = properties.getProperty("bundleup");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private String destApkPath;


    public ApkHandler(ConfigData configData, OutputStream outputStream) {
        this.apkPath = configData.apkFilePath;
        this.keystorePath = configData.keyStoreFilePath;
        this.apkToolDirectory = configData.apkToolPath;
        jarSignerPath = configData.jdkHomePath + RELATIVE_PATH_JAR_SIGN;
        this.outputStream = outputStream;
        File apkPathFile = new File(apkPath);
        apkName = apkPathFile.getName();
        apkDirectory = apkPathFile.getParent();
        apkDestDirectory=apkDirectory+File.separator+"dest"+File.separator;
        tempFileDirectory = apkDirectory + "\\temp";

        key_pass = configData.keypass;
        store_pass = configData.storepass;
        alias = configData.alias;

        apkResourceHandler = new ApkResourceHandler(tempFileDirectory);

//        OutputStream outputStream=new OutputStream() {
//                @Override
//                public void write(int i) throws IOException {
//                    JLabel jLabel=new JLabel();
//                    jLabel.setText(i);
//                }
//        };


    }

    /**
     * 反编译处理。
     */
    public final void deCompile() throws CmdExecuteException {

//
        String cmd = apkToolDirectory + String.format(DECODE, apkPath, tempFileDirectory);


        printMessage("解包", cmd);

        Command.executeCmd(new String[]{cmd}, outputStream);


    }

    public void replaceRes(File file) {

        apkResourceHandler.replace(file);


    }


    public void bundleUp() throws CmdExecuteException {


        String cmd = apkToolDirectory + String.format(BUNDLE_UP, tempFileDirectory);

        printMessage("打包", cmd);


        Command.executeCmd(new String[]{cmd}, outputStream);


    }


    /**
     * 签名处理。
     */
    public void signUp() throws CmdExecuteException {


        String apkPath = tempFileDirectory + "\\dist\\" + apkName;
//        String path = "E:/Program Files/Java/jdk1.7.0_80/bin/jarsigner.exe";
//        //  String path = "jarsigner.exe";
        String path = jarSignerPath;

        String cmd = String.format(SIGN, keystorePath, key_pass, store_pass, apkPath, alias);
        String[] array = cmd.split(" ");
        List<String> commandList = new ArrayList<>();
        commandList.add(path);
        for (String temp : array) {
            commandList.add(temp);
        }


        printMessage("签名", commandList);

        Command.execute(commandList, outputStream);
    }


    private List<String> combineComandToList(String command, String params) {
        String[] array = params.split(" ");
        List<String> commandList = new ArrayList<>();
        commandList.add(command);

        for (String temp : array) {
            commandList.add(temp);
        }

        return commandList;
    }


    /**
     * 签名后的包 对齐
     */
    public void zipAlign() throws CmdExecuteException {


        String alignCmdPath = apkToolDirectory + "zipalign.exe";
        String apkPath = tempFileDirectory + "\\dist\\" + apkName;
        String[] split = apkName.split("\\.");
        destApkPath = tempFileDirectory + "\\dist\\" + split[0] + "_aligned" + "." + split[1];

        //  String path = "jarsigner.exe";
        String cmd = "    -v 4  " + apkPath + "  " + destApkPath;
        String[] array = cmd.split(" ");
        List<String> commandList = new ArrayList<>();
        commandList.add(alignCmdPath);
        for (String temp : array) {
            commandList.add(temp);
        }

        printMessage("对齐", commandList);


        Command.execute(commandList, outputStream);

    }


    private void printMessage(String messageName, String cmd) {
        PrintWriter pw = getPrintWriter();
        pw.println("=================execute command 执行命令  " + messageName + " ========================================");
        pw.println(cmd);
        pw.flush();


    }

    private void printMessage(String messageName, List<String> commandList) {
        PrintWriter pw = getPrintWriter();

        pw.println("=================execute command 执行命令  " + messageName + " ========================================");

        for (String item : commandList) {

            pw.print(item + " ");
        }
        pw.println();
        pw.flush();

    }

    public void move(File packFile) {


        if (!StringUtil.isEmpty(destApkPath)) {
            File file = new File(destApkPath);
            printMessage(destApkPath + ",exist:" + file.exists());
            File dest = new File(apkDestDirectory+packFile.getName() + ".apk");

            if (dest.exists()) {
                dest.delete();
            } else {
                FileUtil.makeDir(dest);
            }

            boolean result = file.renameTo(dest);

            printMessage(dest.getAbsolutePath() + ",result:" + result);
        }

    }


    private void printMessage(String message) {

        PrintWriter pw = getPrintWriter();
        pw.println(message);
        pw.flush();


    }


    PrintWriter printWriter ;
    private PrintWriter getPrintWriter()
    {

        if(printWriter==null)
        {
            printWriter=new PrintWriter(outputStream);
        }
        return printWriter;


    }

    /**
     * 清理现场
     */
    public void clear() {
        printMessage("==================清除临时文件============"+tempFileDirectory);
        if (!StringUtil.isEmpty(tempFileDirectory)) {

            FileUtil.deleteFile(new File(tempFileDirectory));

        }

        printMessage("==================打包后文件存放在  "+apkDestDirectory+",总共"+new File(apkDestDirectory).listFiles().length+",个包============");


        printMessage("==================批量打包结束================");
    }
}
