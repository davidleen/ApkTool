package com.changdu.apkpackage;


import com.changdu.apkpackage.dom.DomXml;
import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ResourceValue;
import com.changdu.apkpackage.utlis.FileUtil;
import org.w3c.dom.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Resource;

/**
 * 资源替换处理类
 * <p/>
 * Created by davidleen29 on 2017/8/8.
 */

public class ApkResourceHandler {


    private static final String ANDROID_MANIFEST_XML = "AndroidManifest.xml";
    private static final String STRING_XML = "strings.xml";
    private static final String INTEGER_XML = "integers.xml";
    private static final String BOOL_XML = "bools.xml";
    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_VERSION_CODE = "android:versionCode";
    private static final String ATTR_VERSION_NAME = "android:versionName";


    /**
     * 解压后的manifest文件， 旧的versioncode 在这个字段。
     */
    private static final String ATTR_PLATFORM_VERSION_CODE = "platformBuildVersionCode";

    /**
     * 解压后的manifest文件， 旧的versionname 在这个字段。
     */
    private static final String ATTR_PLATFORM_VERSION_NAME = "platformBuildVersionName";

    private static final String ATTR_ANDOIRD_NAME = "android:name";
    private static final String ATTR_ANDOIRD_VALUE = "android:value";
    private static final String ATTR_NAME = "name";

    private static final String RES = "res";
    /**
     * res资源路径
     */

    public static final String RES_VALUES_DIRECTORY = RES + File.separator + "values";
    private static final String APPLICATION = "application";

    /**
     * 备份文件文件夹名称， 长名称避免重名
     */
    public static final String TEMP = "temp_xxxxxxxxx_temp" + File.separator;



    private String apkFileDirectory;
    private IPrintable iPrintable;

    /**
     * android 资源类型 string integer boolean array
     */
    public String[] types = new String[]{"string", "integer", "bool", "string-array", "array", "integer-array"};
    /**
     * 不同资源类型  解包后， 会归并到不同的类型s的xml文件中。
     */
    public String[] typeFiles = new String[]{"strings.xml", "integers.xml", "bools.xml", "arrays.xml", "arrays.xml", "arrays.xml"};

    public ApkResourceHandler(String apkFileDirectory, IPrintable iPrintable) {


        this.apkFileDirectory = apkFileDirectory;

        this.iPrintable = iPrintable;


        //新建立 移除任何的备份文件。
        File tempFilePath = new File(getTempFilePath());
        if (tempFilePath.exists()) {
            FileUtil.deleteFile(tempFilePath);
        }


    }


    private String getTempFilePath() {
        return apkFileDirectory + File.separator + TEMP;
    }

    public void replace(File file,boolean shouldChangePackageName) throws CmdExecuteException {

        //文件名当做包名

        String packageName = file.getName();


        if(shouldChangePackageName) {
            changePackageName(packageName);
        }


        //改文件下 所有文件遍历过去。  替换解析出来的包Res 里面 里面相同的文件
        replaceResources(file, file);


    }

    /**
     * 所有配置中的文件都替换过去。
     * <p/>
     * res/values 底下的文件
     *
     * @param topFile
     * @param file
     */
    private void replaceResources(File topFile, File file) throws CmdExecuteException {


        String fileName = file.getName();
        if (file.isFile()) {


            //window db 临时文件不做处理。
            if (fileName.toLowerCase().equals(Constant.WINDOW_DB_FILE.toLowerCase())) {
                return;
            }


            String relativePath = file.getAbsolutePath().substring(topFile.getAbsolutePath().length());

            String destPath = apkFileDirectory + relativePath;


            File destFile = new File(destPath);
            File destFile2 = null;
            //目标文件不存在的情况处理。
            if (!destFile.exists()) {
                //对jpg文件 进行兼容
                if (relativePath.toLowerCase().endsWith(".jpg")) {
                    destFile2 = new File(destFile.getPath().replace(".jpg", ".png"));
                }
                //对png文件 进行兼容
                if (relativePath.toLowerCase().endsWith(".png")) {
                    destFile2 = new File(destFile.getPath().replace(".png", ".jpg"));
                }
            }


            if (fileName.equals(ANDROID_MANIFEST_XML)) {
                //manifest文件处理


                updateManifest(file, destFile);

            } else {


                if (!destFile.exists() && (destFile2 == null || !destFile2.exists())) {


                    //目标文件不存在， 并且是 res/values[-*]*/ 下的文件。 需要从对应的
                    //目标替换文件不存在。  合并到 strings.xml类似的文件中了。
//                    //移除掉strings.xml，integer.xml ...中的相应配置数据。

                    if (file.getPath().contains(RES_VALUES_DIRECTORY)) {

                        int typeSize = types.length;
                        for (int i = 0; i < typeSize; i++) {


                            if (file.getName().equals(typeFiles[i])) {
                                throw new CmdExecuteException(0,"打包文档中的文件，不应该存在" + typeFiles[i] + "这样的文件");
                            }
                            //找到对应文件接下 对应类型的资源文件  values[-*]*

                            List<String> nodeNames = readNodeNamesInNewFile(types[i], file.getAbsolutePath());
                            String stringsFilePath = destFile.getParent() + File.separator + typeFiles[i];



                            backFile(stringsFilePath);
                            removeXmlNode(nodeNames, stringsFilePath);


                        }


                    }

                }


                boolean hasFile2 = destFile2 != null && destFile2.exists();
                backFile(destFile.getPath(), hasFile2 ? destFile2.getPath() : destFile.getPath());
                if(hasFile2)
                {
                    destFile2.delete();
                }
                //文件替换
                FileUtil.copyFile(file.getPath(), destFile.getPath());
                iPrintable.println("文件替换 from :" + file.getPath() + (destFile.exists() ? ("  --->  " + destPath) : ""));
            }


        } else {

            //额外追加到apk包的文件不需要拷贝到apk temp 目录下。

            if (fileName.equals(Constant.APPENDIX_FILE_PATH)) return;


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
     * 读取包名
     * @return
     */
    public String readpackageName()
    {
        String manifestFilePath = apkFileDirectory + File.separator + ANDROID_MANIFEST_XML;
        String oldPackageName = "";
        DomXml domXml = new DomXml(manifestFilePath);
        Document doc = domXml.openFile();

        if (doc != null) {
            Node manifest = doc.getFirstChild();
            if (manifest != null && manifest.hasAttributes()) {
                NamedNodeMap attr = manifest.getAttributes();
                Node nodeAttr = attr.getNamedItem(ATTR_PACKAGE);
                oldPackageName=  nodeAttr.getTextContent();

            }

        }
        domXml.saveFile(doc);


        domXml.close();
        return oldPackageName;
    }


    /**
     * 修改包名
     */
    public void changePackageName(String newPackageName) {

        iPrintable.println("==================修改包名================" + newPackageName);
        String manifestFilePath = apkFileDirectory + File.separator + ANDROID_MANIFEST_XML;
        backFile(manifestFilePath);


        String oldPackageName = "";
        DomXml domXml = new DomXml(manifestFilePath);


        Document doc = domXml.openFile();

        if (doc != null) {
            Node manifest = doc.getFirstChild();
            if (manifest != null && manifest.hasAttributes()) {
                NamedNodeMap attr = manifest.getAttributes();
                Node nodeAttr = attr.getNamedItem(ATTR_PACKAGE);
                oldPackageName = nodeAttr.getTextContent();
                nodeAttr.setTextContent(newPackageName);
            }

        }
        domXml.saveFile(doc);


        domXml.close();


        //替换manifest 文件中 所有与包名

        //在manifest 替换所有 以旧包名开头的配置， 比如actition等。
        if (!StringUtil.isEmpty(oldPackageName)) {
            //修改后的文件，临时存放
            String tempFilePath = manifestFilePath + "-temp";
            //是否在文件中 有改动到跟packageName相关的配置
            boolean doUpdatePackage = false;
            try {

                FileInputStream fileInputStream = new FileInputStream(manifestFilePath);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                FileOutputStream out = new FileOutputStream(tempFilePath);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                String text = "";

                String textAfterReplace;
                while ((text = bufferedReader.readLine()) != null) {


                    textAfterReplace = text.replace(oldPackageName, newPackageName);
                    if (text.contains(oldPackageName)) {
                        doUpdatePackage = true;
                        iPrintable.println(textAfterReplace);
                    }


                    bufferedWriter.write(textAfterReplace);
                    bufferedWriter.write("\n");


                }


                bufferedWriter.flush();

                bufferedWriter.close();
                outputStreamWriter.close();
                out.close();

                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            File newFile = new File(tempFilePath);

            //有修改 删除源文件， 临时文件覆盖源文件。
            if (doUpdatePackage) {
                File dest = new File(manifestFilePath);
                dest.delete();
                newFile.renameTo(dest);
            } else {

                //无，删除临时文件。
                newFile.delete();
            }

        }


    }


    /**
     * 修改版本号，更好的方式去修改 apktool.yml  这个文件。  这里简单处理， 直接manifest塞属性。
     * @param newVersionCode
     * @param newVersionName
     * @param outPut
     */
    public void changeVersionCodeAndName(String newVersionCode, String newVersionName,String[] outPut) {

        iPrintable.println("==================修改版本号================versionCode:" + newVersionCode + ",versionName:" + newVersionName);
        String manifestFilePath = apkFileDirectory + File.separator + ANDROID_MANIFEST_XML;
        backFile(manifestFilePath);
        DomXml domXml = new DomXml(manifestFilePath);


        Document doc = domXml.openFile();

        if (doc != null) {
            Node manifest = doc.getFirstChild();

            if (manifest != null) {
                Node versionCodeAttr = null;
                Node versionNameAttr = null;
                Node oldVersionCodeAttr=null;
                Node oldVerrsionNameAttr=null;
                if (manifest.hasAttributes()) {
                    NamedNodeMap attr = manifest.getAttributes();
                    versionCodeAttr = attr.getNamedItem(ATTR_VERSION_CODE);
                    versionNameAttr = attr.getNamedItem(ATTR_VERSION_NAME);
                    oldVersionCodeAttr = attr.getNamedItem(ATTR_PLATFORM_VERSION_CODE);
                    oldVerrsionNameAttr = attr.getNamedItem(ATTR_PLATFORM_VERSION_NAME);


                }
                if (versionCodeAttr == null) {
                    ((Element) manifest).setAttribute(ATTR_VERSION_CODE, newVersionCode);
                    outPut[0]=oldVersionCodeAttr==null?"":oldVersionCodeAttr.getTextContent();
                } else {
                    outPut[0]=versionCodeAttr.getTextContent();
                    versionCodeAttr.setTextContent(newVersionCode);
                }
                if (versionNameAttr == null) {
                    ((Element) manifest).setAttribute(ATTR_VERSION_NAME, newVersionName);
                    outPut[1]=oldVerrsionNameAttr==null?"":oldVerrsionNameAttr.getTextContent();
                } else {
                    outPut[1]=versionNameAttr.getTextContent();
                    versionNameAttr.setTextContent(newVersionName);
                }


            }
        }
        domXml.saveFile(doc);


        domXml.close();


    }


    /**
     * 修改版本号，更好的方式去修改 apktool.yml  这个文件。  这里简单处理， 直接manifest塞属性。
     * @param meta_data
     * @param value
     * @return  do update
     */
    public boolean updateMetaDataOnManifest(String meta_data,String value) {

        iPrintable.println("==================修改Manifest文件中Meta-data================meta_data:" + meta_data + ",value:" + value);
        String manifestFilePath = apkFileDirectory + File.separator + ANDROID_MANIFEST_XML;
        DomXml domXml=null;
        boolean result = false;
        try {
            domXml = new DomXml(manifestFilePath);


            Document doc = domXml.openFile();

            if (doc != null) {
                Node manifest = doc.getFirstChild();


                if (manifest != null) {

                    Node application = null;
                    NodeList childNodes = manifest.getChildNodes();
                    for (int i = 0, size = childNodes.getLength(); i < size; i++) {

                        Node child = childNodes.item(i);


                        if (child.getNodeName().equalsIgnoreCase("application")) {
                            application = child;
                            break;
                        }


                    }


                    if (application != null) {
                        childNodes = application.getChildNodes();
                        for (int i = 0, size = childNodes.getLength(); i < size; i++) {

                            Node child = childNodes.item(i);

                            if (child.getNodeName().equalsIgnoreCase("meta-data")) {

                                NamedNodeMap destAttr = child.getAttributes();
                                if (destAttr == null) continue;
                                final Node destNameAttr = destAttr.getNamedItem(ATTR_ANDOIRD_NAME);
                                if (destNameAttr == null) continue;
                                if (destNameAttr.getNodeValue().equalsIgnoreCase(meta_data)) {

                                    Node namedItem = destAttr.getNamedItem(ATTR_ANDOIRD_VALUE);
                                    if (namedItem != null) {
                                        namedItem.setNodeValue(value);
                                        result = true;
                                    }

                                }


                            }


                        }
                    }


                }
            }
            domXml.saveFile(doc);
        }catch (Throwable t)
        {
            t.printStackTrace();
        }finally {

            if(domXml!=null)
                domXml.close();
        }




        return result;

    }





    /**
     * 解包后 源文件备份， 任务完成后可以恢复原样。
     *
     * @param filePath
     */
    private void backFile(String filePath) {

        backFile(filePath, filePath);
    }

    /**
     * 解包后 源文件备份， 任务完成后可以恢复原样。
     * <p>
     * 在特殊情况  filePath, 替换的文件跟 备份的文件不一样，  .jpg  与.png 情况下
     *
     * @param filePathToPlace
     * @param filePathToBack
     */
    private void backFile(String filePathToPlace, String filePathToBack) {


        if (filePathToBack.startsWith(apkFileDirectory)) {
            File tempFilePath = new File(getTempFilePath() + filePathToBack.substring(apkFileDirectory.length()));


            //不存在才备份， 存在表示已经备份过了
            if (!tempFilePath.exists()) {
                FileUtil.copyFile(filePathToBack, tempFilePath.getAbsolutePath());

            }


            markUpdate(filePathToPlace);
        }

    }


    private void markUpdate(String filePath) {
        allFilePathUpdated.add(filePath);
    }

    /**
     * 对manifest文件进行替换处理。
     * <p/>
     * <p/>
     * 替换原则  application 下   新的节点 如果在解包后的manifest中存在， 替换， 否则 增加
     *
     * @param configFile
     * @param destFile
     */
    private void updateManifest(File configFile, File destFile) {


        DomXml configDom = new DomXml(configFile.getAbsolutePath());

        DomXml destDom = new DomXml(destFile.getAbsolutePath());


        //循环遍历application下的所有note

        final Document configDocument = configDom.openFile();
        Node configApplcation = findApplicationNode(configDocument);
        final Document destDocument = destDom.openFile();
        Node destAplication = findApplicationNode(destDocument);


        if (configApplcation == null) return;

        if (destAplication == null) return;


        NodeList nodeList = configApplcation.getChildNodes();


        int len = nodeList.getLength();
        for (int i = 0; i < len; i++) {

            Node node = nodeList.item(i);

            String nodeName = node.getNodeName();

            NamedNodeMap attr = node.getAttributes();
            if (attr == null) continue;
            final Node namedItem = attr.getNamedItem(ATTR_ANDOIRD_NAME);
            if (namedItem == null) continue;
            String androidName = namedItem.getNodeName();
            String androidNameValue = namedItem.getNodeValue();


            //在目标document 中找到该节点  如果存在移除

            NodeList destAplicationChildNodes = destAplication.getChildNodes();
            int destLength = destAplicationChildNodes.getLength();
            for (int j = 0; j < destLength; j++) {

                Node destNode = destAplicationChildNodes.item(j);

                String destNodeName = destNode.getNodeName();

                NamedNodeMap destAttr = destNode.getAttributes();
                if (destAttr == null) continue;
                final Node destNameAttr = destAttr.getNamedItem(ATTR_ANDOIRD_NAME);
                if (destNameAttr == null) continue;
                String destAndroidName = destNameAttr.getNodeName();


                String destAndroidNameValue = destNameAttr.getNodeValue();

                if (nodeName.equals(destNodeName) && androidName.equals(destAndroidName) && androidNameValue.equals(destAndroidNameValue)) {


                    destAplication.removeChild(destNode);
                    break;
                }


            }


            //添加到目标document中


            Node copyName = destDocument.importNode(node, true);


            destAplication.appendChild(copyName);


        }

        destDom.saveFile(destDocument);


        destDom.close();
        configDom.close();


    }

    private Node findApplicationNode(Document document) {


        Node firstChild = document.getFirstChild();

        if (firstChild.hasChildNodes()) {
            NodeList childNodes = firstChild.getChildNodes();

            int childNodeCount = childNodes.getLength();
            for (int i = 0; i < childNodeCount; i++) {
                Node node = childNodes.item(i);

                if (node.getNodeName().equals(APPLICATION))
                    return node;

            }

        }

        return null;


    }

    /**
     * 所有发生改变的文件， 包括添加， 修改
     * <p>
     * 恢复源文件时候 这些文件全部删除， 并从备份文件夹中 恢复原文件。
     */
    private Set<String> allFilePathUpdated = new HashSet<>();


    /**
     * 还原资源文件。
     */
    public void resetResources() {


        for (String filePath : allFilePathUpdated) {

            File file = new File(filePath);
            FileUtil.deleteFile(file);

        }
        allFilePathUpdated.clear();

        File tempFilePath = new File(getTempFilePath());

        moveTempOut(tempFilePath);


        FileUtil.deleteFile(tempFilePath);


        File distFile = new File(apkFileDirectory, "dist");
        FileUtil.deleteFile(distFile);


    }

    private void moveTempOut(File tempFile) {
        if (tempFile.isDirectory()) {
            File[] children = tempFile.listFiles();
            for (File child : children) {
                moveTempOut(child);

            }

        } else {

            File dest = new File(tempFile.getAbsolutePath().replace(TEMP, ""));

            if (dest.exists()) dest.delete();
            tempFile.renameTo(dest);
        }


    }

    /**
     * 修改应用名称   这个可以在资源中配置  res/value  也可以 根据文件夹指定
     * @param appName
     */
    public void changeApkName(String appName,IPrintable iPrintable) {

        boolean  changeApkName=false;
        changeApkName=updateResourceValue("app_name","string",appName,iPrintable);

        iPrintable.println("应用名称修改:"+changeApkName+ "  ===="+appName);


    }

    /**
     *
     * @param key
     */
    private boolean updateResourceValue(String key,String type,String value,IPrintable iPrintable) {



        String stringvaluexml = apkFileDirectory + File.separator + RES_VALUES_DIRECTORY + File.separator + type+"s.xml";

        return updateResourceValue(key,type,value,stringvaluexml,iPrintable);

    }
    /**
     *
     * @param key
     */
    private boolean updateResourceValue(String key,String type,String value,String filePath,IPrintable iPrintable) {
        String stringvaluexml =filePath;// apkFileDirectory+File.separator + RES_VALUES_DIRECTORY +File.separator+ STRING_XML;


        DomXml domXml = new DomXml(stringvaluexml);


        boolean  hasUpdate=false;
        Document doc = domXml.openFile();

        if (doc != null) {
            Node node = doc.getFirstChild();

            if (node != null) {
                NodeList nodeList=  node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {


                    Node childNode=nodeList.item(i);
                    if(type.equalsIgnoreCase(childNode.getNodeName())&& childNode.hasAttributes())
                    {
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes == null) continue;
                        Node nameItem = attributes.getNamedItem(ATTR_NAME);
                        if(nameItem ==null)continue;
                        if (key.equalsIgnoreCase(nameItem.getNodeValue())) {
                            childNode.setTextContent(value);
                            hasUpdate = true;
                            break;
                        }


                    }
                }



            }
        }
        domXml.saveFile(doc);


        domXml.close();

        return hasUpdate;


    }







    /**
     *  遍历所有filepath中的节点，抽取apk临时文件夹中的资源，替换。
     * @param filePath
     * @param language
     */
    public void extractStringResource(String filePath, String language) {

        String defaultStringXmlFilePath=   apkFileDirectory+File.separator + RES_VALUES_DIRECTORY +File.separator+ STRING_XML;
        String languageXmlFilePath=  apkFileDirectory+File.separator + RES_VALUES_DIRECTORY +"-"+language+File.separator+ STRING_XML;


        DomXml defaultStringDomXml=new DomXml(defaultStringXmlFilePath);
        Document defaultDocument = defaultStringDomXml.openFile();
        DomXml languageStringDomXml=new DomXml(languageXmlFilePath);
        Document languagueDocument = languageStringDomXml.openFile();

        DomXml domXml = new DomXml(filePath);
        Document doc = domXml.openFile();
        if (doc != null) {
            Node firstChild = doc.getFirstChild();
            if (firstChild.hasChildNodes()) {
                NodeList childNodes = firstChild.getChildNodes();

                int childNodeCount = childNodes.getLength();
                for (int i = 0; i < childNodeCount; i++) {
                    Node node = childNodes.item(i);
                    //找到对应的类型节点。
                    Node stringNameNode = node.getAttributes() == null ? null : node.getAttributes().getNamedItem(ATTR_NAME);
                    if (stringNameNode != null ) {
                        String nodeValue = stringNameNode.getNodeValue();

                        String content=getStringNodeValue(languagueDocument,nodeValue);
                        if(content==null)
                        {
                            content=getStringNodeValue(defaultDocument,nodeValue);
                        }
                        if(content!=null) {
                            node.setTextContent(StringEscapeUtils.escapeXml10(content));
                        }

                    }


                }





                    domXml.saveFile(doc);




            }
            domXml.close();

        }










    }




    /**
     *  遍历所有filepath中的节点，抽取apk临时文件夹中的资源，替换。
     * @param filePath
     */
    public void extractStringResource(String filePath,boolean clearSourceNode) {


        File directory=new File(apkFileDirectory);
        if(directory.isDirectory())
        {


            for (File file:directory.listFiles())
            {

                if(file.getName().endsWith(".xml")) {


                    DomXml defaultStringDomXml = new DomXml(file.getPath());
                    System.out.println("filePath:"+file.getPath());
                    Document defaultDocument = defaultStringDomXml.openFile();
                    DomXml domXml = new DomXml(filePath);
                    Document doc = domXml.openFile();
                    List<String> nodesRetrieve=new ArrayList<>();
                    if (doc != null) {
                        Node firstChild = doc.getFirstChild();
                        if (firstChild.hasChildNodes()) {
                            NodeList childNodes = firstChild.getChildNodes();

                            int childNodeCount = childNodes.getLength();
                            for (int i = 0; i < childNodeCount; i++) {
                                Node node = childNodes.item(i);


                                //找到对应的类型节点。
                                Node stringNameNode = node.getAttributes() == null ? null : node.getAttributes().getNamedItem(ATTR_NAME);
                                if (stringNameNode != null) {
                                    String nodeValue = stringNameNode.getNodeValue();
                                    String content = getStringNodeValue(defaultDocument, nodeValue);

                                    if (content != null) {
                                        System.out.println("nodeValue:"+nodeValue+",content:"+content);
                                        nodesRetrieve.add(nodeValue);
                                        node.setTextContent(StringEscapeUtils.escapeXml10(content));
                                    }

                                }


                            }


                            domXml.saveFile(doc);


                        }
                        domXml.close();
                        defaultStringDomXml.close();

                    }
                   if(clearSourceNode&&nodesRetrieve.size()>0)
                    {
                        removeXmlNode(nodesRetrieve,file.getPath());
                     }

                }
            }


        }
















    }











    private static  final String getStringNodeValue(Document document,String nodeName )
    {
        return getNodeValue(document,nodeName,"string");
    }


    /**
     * 获取节点对应的值。
     * @param document
     * @param nodeName
     * @param nodeTag
     * @return
     */
    private static  final String getNodeValue(Document document,String nodeName,String nodeTag)
    {

        Node node = document.getFirstChild();

        if (node != null) {
            NodeList nodeList=  node.getChildNodes();
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++) {


                Node childNode=nodeList.item(i);
                if(nodeTag.equalsIgnoreCase(childNode.getNodeName())&& childNode.hasAttributes())
                {
                    NamedNodeMap attributes = childNode.getAttributes();
                    if (attributes == null) continue;
                    Node nameItem = attributes.getNamedItem(ATTR_NAME);
                    if(nameItem ==null)continue;
                    if (nodeName.equalsIgnoreCase(nameItem.getNodeValue())) {
                        return  childNode.getTextContent();
                    }

                }
            }



        }
        return null;




    }

    public void updateResourceValue(List<ResourceValue> updateValueList,IPrintable iPrintable) {


        for (ResourceValue value:updateValueList)
        {

            boolean update=false;
            if(value.type.equalsIgnoreCase(ResourceValue.TYPE_META_DATA))
            {

                update=updateMetaDataOnManifest(value.key,value.value);
                iPrintable.println("修改Manifest资源meta-data值:"+value.toString()+ "  ===="+(update ? "成功" : "失败"));
            }else {
                update= updateResourceValue(value.key,value.type,value.value,iPrintable);

                iPrintable.println("修改Res资源值:"+value.toString()+ "  ===="+(update ? "成功" : "失败"));
            }








        }






    }
}
