package com.changdu.apkpackage;

import com.changdu.apkpackage.view.MainFrame;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import static java.awt.Frame.MAXIMIZED_BOTH;

/**
 * 打包工具应用程序入口
 */
public class Main {

    public static void main(String [] args)
    {


        System.out.println("start java --------------------------------------");


         final MainFrame frame=new MainFrame("打包工具");
        frame.setVisible(true);

    }
}
