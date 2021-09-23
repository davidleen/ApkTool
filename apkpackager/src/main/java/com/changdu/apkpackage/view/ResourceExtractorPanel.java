package com.changdu.apkpackage.view;

import com.changdu.apkpackage.TextView;
import com.changdu.apkpackage.utlis.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ResourceExtractorPanel implements TextView {
    private JPanel root;
    private JTextField tf_file_path;
    private JTextField tf_apk_path;
    private JComboBox cb_language;
    private JButton execute;
    private JButton pick_apk;
    private JButton pick_xml;
    private JTextArea log;
    private JButton extractDrawable;

    public Container getRoot() {

        return root;
    }

    public ResourceExtractorPanel(final ResourceExtractorListener stringExtractListener, final ResourceExtractorListener drawableExtractListener) {


        pick_apk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                File file = FileUtil.getSelectedFilePath( );
                if(file!=null)
                     tf_apk_path.setText(file.getAbsolutePath());

            }
        });


        pick_xml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File file = FileUtil.getSelectedFilePath( );
                if(file!=null)
                    tf_file_path.setText(file.getAbsolutePath());
            }
        });



        execute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                if(cb_language.getSelectedIndex()==0)
//                {
//                    JOptionPane.showMessageDialog(getRoot(),"请选择语言");
//                    return;
//                }

                String filePath=tf_file_path.getText().trim();
                if(filePath.equalsIgnoreCase(""))
                {
                    JOptionPane.showMessageDialog(getRoot(),"请选择资源xml文件包");
                    return;
                }

                String apkPath=tf_apk_path.getText().trim();
                if(apkPath.equalsIgnoreCase(""))
                {
                    JOptionPane.showMessageDialog(getRoot(),"请选择apk包");
                    return;
                }
                stringExtractListener.start(filePath,apkPath,(String) cb_language.getSelectedItem());

            }
        });
            extractDrawable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                if(cb_language.getSelectedIndex()==0)
//                {
//                    JOptionPane.showMessageDialog(getRoot(),"请选择语言");
//                    return;
//                }

                String filePath=tf_file_path.getText().trim();
                if(filePath.equalsIgnoreCase(""))
                {
                    JOptionPane.showMessageDialog(getRoot(),"请选择资源xml文件包");
                    return;
                }

                String apkPath=tf_apk_path.getText().trim();
                if(apkPath.equalsIgnoreCase(""))
                {
                    JOptionPane.showMessageDialog(getRoot(),"请选择apk包");
                    return;
                }
                drawableExtractListener.start(filePath,apkPath,(String) cb_language.getSelectedItem());

            }
        });



        cb_language.addItem("选择语言");
        cb_language.addItem("en");
        cb_language.addItem("es");
        cb_language.addItem("zh-rTW");

        cb_language.setVisible(false);

    }

    @Override
    public void setText(String text) {


        log.setText(text);

    }


    interface ResourceExtractorListener
    {
        void start(String destResFilePath,String fromResourcePath,String language);
    }

}
