package com.changdu.apkpackage.excel;

public interface ExcelOperator {

      void open();

      String getString (int sheetIndex, int rowIndex, int columnIndex);

      void close();

      int getRowCount(int sheetIndex);
}
