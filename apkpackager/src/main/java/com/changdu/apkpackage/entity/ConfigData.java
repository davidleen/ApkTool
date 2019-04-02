package com.changdu.apkpackage.entity;

import java.io.Serializable;

public class ConfigData implements Serializable {


    private static final long serialVersionUID = 7825224039759649113l;

    public String apkFilePath;


    public String jdkHomePath;

    public String apkToolPath;


    //签名文件
    public String keyStoreFilePath;
    //存储密码
    public String storepass;
    //密钥
    public String keypass;
    //别名
    public String alias;


    //打包文件配置的路径
    public String apkPackPath;

    /**
     * 渠道文件路径
     */
    public String channelFilePath;


}
