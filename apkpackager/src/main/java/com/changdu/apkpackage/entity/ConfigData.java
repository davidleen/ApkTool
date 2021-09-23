package com.changdu.apkpackage.entity;


import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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



    /**
     * 是否使用配置文档得文件名作为新应用名称。
     */
    public boolean useNewAppName;

    /**
     * 配置文档中是否使用文件路径作为新包名
     */
    public boolean useNewPackageName;


    /**
     * 搜索渠道xls文件的目录
     */
    public  String  channelDirectory;



    public transient List<ResourceValue> updateValueList ;
}
