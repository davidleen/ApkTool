package com.changdu.apkpackage.view;

import com.changdu.apkpackage.IPrintable;
import com.changdu.apkpackage.excel.ExcelOperator;
import com.changdu.apkpackage.excel.POIExcelOperator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 渠道值 获取帮助类
 */
public class ChannelHelper {


    public static Map<String, String> getChannelMap(File xlsFile, IPrintable printable) {


        if (xlsFile.exists()) {

            printable.println("渠道数据读取");
            ExcelOperator operator = new POIExcelOperator(xlsFile.getPath());

            operator.open();

            int rowCount = operator.getRowCount(0);
            Map<String, String> map = new HashMap<>();
            int startRowIndex = 1;
            int keyColumn=2;
            int valueColumn=1;
            for (int i = 0; i < rowCount; i++) {


                String key = operator.getString(0, i + startRowIndex, keyColumn);
                if(key==null||"null".equalsIgnoreCase(key)||"".equalsIgnoreCase(key.trim()))
                {

                    printable.println("渠道对应值读取失败,对应单元格:i="+i+",j="+keyColumn);
                    continue;
                }
                String value = operator.getString(0, i + startRowIndex, valueColumn);
                if(map.containsKey(key))
                {
                    printable.println("渠道对应值重复:key="+key+",value="+value);
                }
                map.put(key, value);
                printable.println("渠道对应值====key:" + key + ",value:" + value);

            }


            operator.close();
            printable.println("总共渠道数量:"+map.size());
            return map;

        }else
        {

            printable.println("没有找到渠道值文件 :"+xlsFile.getPath());

        }

        return null;
    }

    public static String chanelTEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<data>\n" +
            "  <channel>%s</channel>\n" +
            "</data>";

    public static String createChannelFile(String directory,String key ) {


        File file = new File(directory);
        if(!file.exists())
        {
            file.mkdirs();
        }

        File filePath = new File(directory+File.separator+"mmiap.xml");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);


            String text = String.format(chanelTEXT, key);

            fileOutputStream.write(text.getBytes());
            fileOutputStream.flush();

            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return filePath.getPath();

    }

}
