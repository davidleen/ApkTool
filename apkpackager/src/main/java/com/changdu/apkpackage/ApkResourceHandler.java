package com.changdu.apkpackage;

import com.changdu.apkpackage.dom.DomXml;
import com.changdu.apkpackage.utlis.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;

/**
 * Created by davidleen29 on 2017/8/8.
 */

public class ApkResourceHandler {


    private static final String ANDROID_MANIFEST_XML = "AndroidManifest.xml";
    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_APP_NAME = "app_name";
    private static final String ATTR_BOOK_ID = "book_id";
    private static final String PATH_STRING_XML = "res/values/strings.xml";
    private static final String PATH_APP_ICON = "res/drawable-xxhdpi/icon.png";
    private String apkFileDirectory;

    public ApkResourceHandler(String apkFileDirectory) {


        this.apkFileDirectory = apkFileDirectory;

    }


    public void replace(File file) {

        //文件名当做包名

        String packageName = file.getName();
        changePackageName(packageName);


        //改文件下 所有文件遍历过去。  替换解析出来的包Res 里面 里面相同的文件




        replaceResources(file, file);


    }

    /**
     * 所有配置中的文件都替换过去。
     *
     * @param topFile
     * @param file
     */
    private void replaceResources(File topFile, File file) {

        if (file.isFile()) {
            String relativePath = file.getAbsolutePath().substring(topFile.getAbsolutePath().length());

            String destPath = apkFileDirectory + relativePath;

            //文件替换
            FileUtil.copyFile(file.getPath(), destPath);


        } else {
            File[] childs = file.listFiles();

            for (File temp : childs) {
                replaceResources(topFile, temp);
            }

        }


    }


    /**
     * 修改包名
     */
    public void changePackageName(String newPackageName) {


        String manifestFilePath = apkFileDirectory + File.separator + ANDROID_MANIFEST_XML;
        DomXml domXml = new DomXml(manifestFilePath);


        Document doc = domXml.openFile();

        if (doc != null) {
            Node manifest = doc.getFirstChild();
            if (manifest != null && manifest.hasAttributes()) {
                NamedNodeMap attr = manifest.getAttributes();
                Node nodeAttr = attr.getNamedItem(ATTR_PACKAGE);
                nodeAttr.setTextContent(newPackageName);
            }

        }
        domXml.saveFile(doc);


        domXml.close();


    }
}
