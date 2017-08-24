package com.changdu.apkpackage;

import com.changdu.apkpackage.dom.DomXml;
import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.utlis.FileUtil;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源替换处理类   对values 的数据   目前不支持国际化配置。
 * <p>
 * Created by davidleen29 on 2017/8/8.
 */

public class ApkResourceHandler {


    private static final String ANDROID_MANIFEST_XML = "AndroidManifest.xml";
    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_VERSION_CODE = "android:versionCode";
    private static final String ATTR_VERSION_NAME = "android:versionName";

    /**
     * res资源路径
     */

    public static final String RES_VALUES_DIRECTORY = "res" + File.separator + "values" + File.separator;

    private String apkFileDirectory;

    /**
     * android 资源类型 string integer boolean array
     */
    public String[] types = new String[]{"string", "integer", "bool", "string-array", "array", "integer-array"};
    /**
     * 不同资源类型  解包后， 会归并到不同的类型s的xml文件中。
     */
    public String[] typeFiles = new String[]{"strings.xml", "integers.xml", "bools.xml", "arrays.xml", "arrays.xml", "arrays.xml"};

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
     * <p>
     * res/values 底下的文件  只需要处理String 类型
     *
     * @param topFile
     * @param file
     */
    private void replaceResources(File topFile, File file) {

        if (file.isFile()) {
            String relativePath = file.getAbsolutePath().substring(topFile.getAbsolutePath().length());

            String destPath = apkFileDirectory + relativePath;


            File destFile = new File(destPath);
            //目标文件不存在， 并且是 res/values/ 下的文件。 需要从对应的
            //目标替换文件不存在。  合并到 strings.xml类似的文件中了。
//                    //移除掉strings.xml，integer.xml ...中的相应配置数据。

            if (!destFile.exists() && file.getAbsolutePath().indexOf(RES_VALUES_DIRECTORY) > -1) {


                int typeSize = types.length;
                for (int i = 0; i < typeSize; i++) {

                    List<String> nodeNames = readNodeNamesInNewFile(types[i], file.getAbsolutePath());
                    String stringsFilePath = apkFileDirectory + RES_VALUES_DIRECTORY + typeFiles[i];
                    removeXmlNode(nodeNames, stringsFilePath);


                }


            }
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
     * 读取要修改的配置的节点名称。  对应不同的类型  string bool  integer
     *
     * @param type     资源类型
     * @param filePath 修改配置的文件
     * @return
     */
    private List<String> readNodeNamesInNewFile(String type, String filePath) {

        //遍历要替换的values 资源。 找出要替换的资源
        List<String> nodeNameOfRes = new ArrayList<>();
        DomXml domXml = new DomXml(filePath);
        Document doc = domXml.openFile();
        if (doc != null) {

            Node firstChild = doc.getFirstChild();
            if (firstChild.hasChildNodes()) {
                NodeList childNodes = firstChild.getChildNodes();
                int childNodeCount = childNodes.getLength();
                for (int i = 0; i < childNodeCount; i++) {
                    Node node = childNodes.item(i);
                    String name = node.getNodeName();
                    if (name.equalsIgnoreCase(type)) {


                        Node stringNameNode = node.getAttributes().getNamedItem("name");
                        if (stringNameNode != null && !StringUtil.isEmpty(stringNameNode.getTextContent())) {
                            nodeNameOfRes.add(stringNameNode.getTextContent());
                        }


                    }


                }

            }

        }

        domXml.close();

        return nodeNameOfRes;

    }

    /**
     * 在strings.xml 文件中 移除被修改了配置的节点。
     *
     * @param nodeName    资源的名称
     * @param xmlFilePath 资源对应的资源文件  strings.xml, integers.xml  ...
     */
    private void removeXmlNode(List<String> nodeName, String xmlFilePath) {

        if (nodeName == null || nodeName.size() == 0) return;
        if (!new File(xmlFilePath).exists()) return;

        DomXml domXml = new DomXml(xmlFilePath);
        Document doc = domXml.openFile();
        if (doc != null) {

            Node firstChild = doc.getFirstChild();

            if (firstChild.hasChildNodes()) {
                NodeList childNodes = firstChild.getChildNodes();

                List<Node> nodeToBeRemoved = new ArrayList<>();
                int childNodeCount = childNodes.getLength();
                for (int i = 0; i < childNodeCount; i++) {
                    Node node = childNodes.item(i);
                    //找到对应的类型节点。


                    Node stringNameNode = node.getAttributes() == null ? null : node.getAttributes().getNamedItem("name");
                    if (stringNameNode != null && !StringUtil.isEmpty(stringNameNode.getTextContent())) {
                        String name = stringNameNode.getTextContent();
                        if (nodeName.indexOf(name) > -1) {
                            nodeToBeRemoved.add(node);
                        }
                    }


                }


                if (nodeToBeRemoved.size() > 0) {

                    for (Node node : nodeToBeRemoved)
                        firstChild.removeChild(node);
                    domXml.saveFile(doc);

                }


            }

        }

        domXml.close();


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

    public void changeVersionCodeAndName(String newVersionCode, String newVersionName) {


        String manifestFilePath = apkFileDirectory + File.separator + ANDROID_MANIFEST_XML;
        DomXml domXml = new DomXml(manifestFilePath);


        Document doc = domXml.openFile();

        if (doc != null) {
            Node manifest = doc.getFirstChild();

            if (manifest != null) {
                Node versionCodeAttr = null;
                Node versionNameAttr = null;
                if (manifest.hasAttributes()) {
                    NamedNodeMap attr = manifest.getAttributes();
                    versionCodeAttr = attr.getNamedItem(ATTR_VERSION_CODE);
                    versionNameAttr = attr.getNamedItem(ATTR_VERSION_NAME);


                }
                if (versionCodeAttr == null) {
                    ((Element) manifest).setAttribute(ATTR_VERSION_CODE, newVersionCode);
                } else {
                    versionCodeAttr.setTextContent(newVersionCode);
                }
                if (versionNameAttr == null) {
                    ((Element) manifest).setAttribute(ATTR_VERSION_NAME, newVersionName);
                } else {
                    versionNameAttr.setTextContent(newVersionName);
                }


            }
        }
        domXml.saveFile(doc);


        domXml.close();


    }
}
