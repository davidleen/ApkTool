package com.changdu.apkpackage.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class POIExcelOperator implements ExcelOperator {
    private final String filePath;
    Workbook workbook;
    public POIExcelOperator(String filePath)
    {

        this.filePath=filePath;





    }
    @Override
    public void open() {
        try {
            if(filePath.toLowerCase().endsWith("xls"))
            {
                workbook=new HSSFWorkbook(new FileInputStream(filePath));
            }
               workbook=new XSSFWorkbook(new FileInputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getString(int sheetIndex,int rowIndex, int columnIndex) {

        Sheet sheetAt = workbook.getSheetAt(sheetIndex);
        Cell cell=        sheetAt.getRow(rowIndex).getCell(columnIndex);
        if(cell!=null)
        {
            return cell.getStringCellValue();
        }

        return null;
    }
    @Override
    public void close() {

        if(workbook!=null)
        {

            try{
                workbook.close();;
            }catch (Throwable t)
            {}
        }

    }

    @Override
    public int getRowCount(int sheetIndex) {
        Sheet sheetAt = workbook.getSheetAt(sheetIndex);

       return  sheetAt.getLastRowNum()-sheetAt.getFirstRowNum();

    }
}
