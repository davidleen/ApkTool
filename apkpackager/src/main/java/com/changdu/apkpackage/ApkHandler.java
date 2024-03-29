package com.changdu.apkpackage;

import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;
import com.changdu.apkpackage.utlis.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 打包各处理。
 * <p>
 * Created by davidleen29 on 2017/8/7.
 */

public class ApkHandler {


    public static final String ERRLOG_TXT = "errlog.txt";
    String keystorePath;
    String key_pass;
    String store_pass;
    String alias;


    public String apkToolDirectory;
    private String jdkHome;


    public static final String RELATIVE_PATH_JAR_SIGN = "bin/jarsigner.exe";
    public static final String RELATIVE_PATH_JAVA_EXE = "bin/java.exe";
    public static final String RELATIVE_PATH_KEY_TOOL = "bin/keytool.exe";


    private String jarSignerPath;


    private IPrintable iPrintable;


    private static String DECODE = "apktool d -f %s  -o %s  --only-main-classes ";
    private static String BUNDLE_UP = "apktool b   %s ";
    private static String BUNDLE_UP_WIDTH_DEST = "apktool b   %s  -o  %s";
    private static String APPEND_FILE_TO_APK = "aapt  a  -v  %s   %s";


    //签名处理。jarsigner -verbose -keystore PATH/TO/YOUR_RELEASE_KEY.keystore -storepass YOUR_STORE_PASS -keypass YOUR_KEY_PASS PATH/TO/YOUR_UNSIGNED_PROJECT.apk YOUR_ALIAS_NAME
    private static String SIGN = " -sigalg MD5withRSA     -digestalg SHA1  -keystore %s  -storepass %s -keypass %s   %s   %s  -signedjar %s   ";  // -verbose
// private static String SIGN = " -sigalg SHA256withRSA     -digestalg SHA1  -keystore %s  -storepass %s -keypass %s   %s   %s  -signedjar %s   ";  // -verbose

    /**
     * 是否输出命令执行详情
     */
    private boolean showCommandDetail = false;


    public ApkHandler(ConfigData configData, IPrintable iPrintable) {

        this(configData, iPrintable, false);
    }


    public ApkHandler(ConfigData configData, IPrintable iPrintable, boolean showCommandDetail) {

        this.keystorePath = configData.keyStoreFilePath;
        this.apkToolDirectory = configData.apkToolPath;
        jdkHome = configData.jdkHomePath;
        jarSignerPath = configData.jdkHomePath + RELATIVE_PATH_JAR_SIGN;
        this.iPrintable = iPrintable;
        key_pass = configData.keypass;
        store_pass = configData.storepass;
        alias = configData.alias;


        this.showCommandDetail = showCommandDetail;
    }

    /**
     * 反编译处理。
     */
    public final void deCompile(String apkFilePath, String decompileOutputDirectory) throws CmdExecuteException {

//

        String tempFileDirectory = decompileOutputDirectory;

        String cmd = apkToolDirectory + String.format(DECODE, apkFilePath, tempFileDirectory);

        printMessage("解包", cmd);

        Command.executeCmd(new String[]{cmd}, iPrintable);


    }
//
//    /**
//     * 资源替换处理
//     * @param packConfigFilePath
//     * @param apkDecompiledTempFilePath
//     */
//    public void replaceRes(File packConfigFilePath, String apkDecompiledTempFilePath) {
//        ApkResourceHandler apkResourceHandler = new ApkResourceHandler(apkDecompiledTempFilePath);
//        try {
//            apkResourceHandler.replace(packConfigFilePath);
//        } catch (CmdExecuteException e) {
//            e.printStackTrace();
//            printMessage("---------------warning--------------------");
//            printMessage(e.getMessage());
//        }
//
//
//    }


    /**
     * 生成apk包，
     *
     * @param tempFileDirectory 解包后apk相关文件所在的文件夹
     * @throws CmdExecuteException
     */
    public void bundleUp(String tempFileDirectory) throws CmdExecuteException {


        String cmd = apkToolDirectory + String.format(BUNDLE_UP, tempFileDirectory);

        cmd+=" 2>"+tempFileDirectory+ ERRLOG_TXT;
        printMessage("打包", cmd);


        Command.executeCmd(new String[]{cmd}, iPrintable);


    }

    /**
     * 生成apk包，
     *
     * @param tempFileDirectory 解包后apk相关文件所在的文件夹
     * @param destApkPath   指定合成的apk文件路径
     * @throws CmdExecuteException
     */
    public void bundleUp(String tempFileDirectory,String destApkPath) throws CmdExecuteException {


        String cmd = apkToolDirectory + String.format(BUNDLE_UP_WIDTH_DEST, tempFileDirectory,destApkPath);
        cmd+=" 2>"+tempFileDirectory+ERRLOG_TXT;
        printMessage("打包到"+destApkPath, cmd);


        Command.executeCmd(new String[]{cmd}, iPrintable);


    }


    /**
     * 签名处理。
     */
    public String jarSignUp(String unSignedApkPath) throws CmdExecuteException {


        String apkPath = unSignedApkPath;

        String resultFileName = splitFileName(apkPath, "_signed");


        File resultFile = new File(resultFileName);
        if (resultFile.exists()) resultFile.delete();

//        String path = "E:/Program Files/Java/jdk1.7.0_80/bin/jarsigner.exe";
//        //  String path = "jarsigner.exe";
        String path = jarSignerPath;

        String cmd = String.format(((showCommandDetail ? " -verbose " : "") + SIGN), keystorePath, key_pass, store_pass, apkPath, alias, resultFileName);
        String[] array = cmd.split(" ");
        List<String> commandList = new ArrayList<>();
        commandList.add(path);
        for (String temp : array) {
            commandList.add(temp);
        }


        printMessage("签名", commandList);

        Command.execute(commandList, iPrintable);


        //验证签名


        cmd = String.format(" -verify    -certs %s", resultFileName);
        array = cmd.split(" ");
        commandList = new ArrayList<>();
        commandList.add(path);
        for (String temp : array) {
            commandList.add(temp);
        }


        printMessage("验证签名", commandList);

        Command.execute(commandList, iPrintable);


        return resultFileName;
    }

    /**
     * 签名处理。
     */
    public String apkSignUp(String unSignedApkPath) throws CmdExecuteException {


        String apkPath = unSignedApkPath;

        String resultFileName = splitFileName(apkPath, "_signed_v2+");


        File resultFile = new File(resultFileName);
        if (resultFile.exists()) resultFile.delete();

//        String path = "E:/Program Files/Java/jdk1.7.0_80/bin/jarsigner.exe";
//        //  String path = "jarsigner.exe";
        String javaPath=jdkHome+RELATIVE_PATH_JAVA_EXE;
        String path = apkToolDirectory+"apksigner.jar";

        String text = "%s -jar %s  sign --ks %s  --ks-key-alias %s   --ks-pass pass:%s   --key-pass pass:%s  --in %s --out %s ";
        String apk_sign_command = String.format( text,javaPath,path, keystorePath,alias, key_pass, store_pass, apkPath,  resultFileName);
        String[] array = apk_sign_command.split(" ");
        List<String> commandList = new ArrayList<>();
        for (String temp : array) {
            commandList.add(temp);
        }


        printMessage("Apk签名V2+", commandList);

        Command.execute(commandList, iPrintable);


        //验证签名

        String apk_sign_verify_command = javaPath+" -jar "+path +"  verify --in "+resultFileName+"  --verbose --print-certs" ;

        array = apk_sign_verify_command.split(" ");
        commandList = new ArrayList<>();
        for (String temp : array) {
            commandList.add(temp);
        }


        printMessage("验证Apk签名", commandList);

        Command.execute(commandList, iPrintable);


        return resultFileName;
    }

    /**
     * 签名后的包 对齐
     *
     *
     * 如果您使用的是 apksigner，只能在为 APK 文件签名之前执行 zipalign。如果您在使用 apksigner 为 APK 签名之后对 APK 做出了进一步更改，签名便会失效。
     * 如果您使用的是 jarsigner，只能在为 APK 文件签名之后执行 zipalign。
     *
     *
     * @param apkFilePath 对齐前包路径
     * @return 对齐后包路径
     * @throws CmdExecuteException
     */
    public String zipAlign(String apkFilePath) throws CmdExecuteException {


        String alignCmdPath = apkToolDirectory + "zipalign.exe";

        String zipAlignedFilePath = splitFileName(apkFilePath, "_aligned");

        File file = new File(zipAlignedFilePath);
        if (file.exists()) file.delete();


        String cmd = (showCommandDetail ? " -v " : "") + "   4  " + apkFilePath + "  " + zipAlignedFilePath;  //
        String[] array = cmd.split(" ");
        List<String> commandList = new ArrayList<>();
        commandList.add(alignCmdPath);
        for (String temp : array) {
            commandList.add(temp);
        }

        printMessage("对齐", commandList);



            Command.execute(commandList, iPrintable);



        cmd = (showCommandDetail ? " -v " : "") + " -c    4  " +  "  " + zipAlignedFilePath;  //


        commandList.clear();
        commandList.add(alignCmdPath);
        for (String temp : cmd.split(" ")) {
            commandList.add(temp);
        }
        printMessage("对齐验证", commandList);

        Command.execute(commandList, iPrintable);





        return zipAlignedFilePath;

    }


    public static String splitFileName(String filePath, String appendixFileName) {

        int index = filePath.lastIndexOf(".");
        if (index > -1) {


            return filePath.substring(0, index) + appendixFileName + filePath.substring(index);

        } else
            throw new RuntimeException("非法文件路径名称" + filePath);


    }


    private void printMessage(String messageName, String cmd) {

        iPrintable.println("=================execute command 执行命令  " + messageName + " ========================================");
        iPrintable.println(cmd);


    }

    private void printMessage(String messageName, List<String> commandList) {


        iPrintable.println("=================execute command 执行命令  " + messageName + " ========================================");
        iPrintable.println();
        for (String item : commandList) {

            iPrintable.print(item + " ");
        }
        iPrintable.println();


    }




    private void printMessage(String message) {

        iPrintable.println(message);


    }


    /**
     * 清理现场
     */
    public void clear(String outputDirectory) {


        printMessage("==================打包后文件存放在  " + outputDirectory + ",总共" + new File(outputDirectory).listFiles().length + ",个包============");


        printMessage("==================批量打包结束================");
    }


    private static String VIEW_SIGN = "  -list  -v -keystore   %s     -storepass  %s  -alias %s ";

    /**
     * 查看签名文件信息
     */
    public void viewSignInfo() throws CmdExecuteException {


        printMessage("==================查看签名文件信息================");


        String path = jdkHome + RELATIVE_PATH_KEY_TOOL;

        String cmd = String.format(VIEW_SIGN, keystorePath, store_pass, alias);
        String[] array = cmd.split(" ");
        List<String> commandList = new ArrayList<>();
        commandList.add(path);
        for (String temp : array) {
            commandList.add(temp);
        }


        printMessage("查看签名文件信息", commandList);

        Command.execute(commandList, iPrintable);


    }

    /**
     * 将指定目录下文件追加到apk根目录下。
     *
     * @param unSignApkFilePath

     */
    public void appendFileToApk(String unSignApkFilePath, String filePath) throws CmdExecuteException {

        printMessage("==================追加额外文件到未签名apk================");


                String cmd = apkToolDirectory + String.format(APPEND_FILE_TO_APK, unSignApkFilePath,filePath);

                iPrintable.println();
                printMessage("追加额外文件", cmd);


                Command.executeCmd(new String[]{cmd}, iPrintable);



    }

    public void clearFramework() throws CmdExecuteException {
        printMessage("==================清除缓存文件================");
        String cmd = apkToolDirectory +"apktool empty-framework-dir --force";
        iPrintable.println();
        printMessage("清除缓存文件", cmd);
        Command.executeCmd(new String[]{cmd}, iPrintable);

    }
}
