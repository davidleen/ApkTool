package com.changdu.apkpackage.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class POIExcelOperator implements ExcelOperator {
    private final String filePath;
    Workbook workbook;

    public POIExcelOperator(String filePath) {

        this.filePath = filePath;


    }

    @Override
    public void open() {
        try {
            if (filePath.toLowerCase().endsWith("xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(filePath));
            }
            workbook = new XSSFWorkbook(new FileInputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getString(int sheetIndex, int rowIndex, int columnIndex) {

        Sheet sheetAt = workbook.getSheetAt(sheetIndex);
        Row row = sheetAt.getRow(rowIndex);
        if (row == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {

            try {
                return cell.getStringCellValue();
            } catch (Throwable t) {
                t.printStackTrace();

                try {


                    return new DecimalFormat("0.#######").format(cell.getNumericCellValue());

                } catch (Throwable t1) {
                    t1.printStackTrace();
                }

            }
        }

        return null;
    }

    @Override
    public void close() {

        if (workbook != null) {

            try {
                workbook.close();
                ;
            } catch (Throwable t) {
            }
        }

    }

    @Override
    public int getRowCount(int sheetIndex) {
        Sheet sheetAt = workbook.getSheetAt(sheetIndex);

        return sheetAt.getLastRowNum() - sheetAt.getFirstRowNum();

    }
}
