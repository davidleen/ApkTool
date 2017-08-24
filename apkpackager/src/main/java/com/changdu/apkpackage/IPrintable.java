package com.changdu.apkpackage;

public interface IPrintable {

    void  print();
    void print(byte[] bytes);
    void print(String message);
    void  clear();
    void  close();

    void println(String s);

    void println();
}
