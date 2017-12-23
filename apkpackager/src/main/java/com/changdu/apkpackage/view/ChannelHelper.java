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


    public static Map<String, String> getChannelMap(File file, IPrintable printable) {

        String fileName = file.getName();
        File xlsFile = new File(file.getParent() + File.separator + "渠道id_" + fileName + ".xlsx");

        if (xlsFile.exists()) {

            printable.println("渠道数据读取");
            ExcelOperator operator = new POIExcelOperator(xlsFile.getPath());

            operator.open();

            int rowCount = operator.getRowCount(0);
            Map<String, String> map = new HashMap<>();
            int startRowIndex = 1;
            for (int i = 0; i < rowCount; i++) {


                String key = operator.getString(0, i + startRowIndex, 1);
                String value = operator.getString(0, i + startRowIndex, 2);
                map.put(key, value);


                printable.println("渠道对应值====key:" + key + ",value:" + value);

            }


            operator.close();

            return map;

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
