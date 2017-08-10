package com.changdu.apkpackage.view;

import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 界面布局处理。
 */
public class MainPanel {
    private JTextField tf_store_file;
    private JButton pickStore;
    private JButton doPack;
    private JPanel root;
    private JTextField tf_jdk;
    private JButton pickJDk;
    private JButton pickApk;
    private JTextField tf_apk;
    private JButton pickPack;
    private JTextField tf_pack;
    private JButton pickApkTool;
    private JTextField tf_apk_tool;
    private JTextField tf_keypass;
    private JTextField tf_store_pass;
    private JTextField tf_alias;
    private JTextArea tf_output;
    private JTextArea ta_packages;


    PanelListener panelListener;

    public MainPanel(final PanelListener panelListener) {
        this.panelListener = panelListener;
        pickStore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onStoreFilePick();
            }
        });

        pickJDk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onJDKPick();
            }
        });

        doPack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onPackStart();
            }
        });
        pickApk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onApkPick();
            }
        });
        pickPack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onPickPack();
            }
        });

        pickApkTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onPickApkTool();
            }
        });
    }

    public String getKeyPass() {
        return tf_keypass.getText().trim();
    }

    public String getStorePass() {
        return tf_store_pass.getText().trim();
    }

    public String getAlias() {
        return tf_alias.getText().trim();
    }

    public JPanel getRoot() {
        return root;
    }


    public void setConfigData(ConfigData configData) {

        tf_store_file.setText(StringUtil.isEmpty(configData.keyStoreFilePath) ? "" : configData.keyStoreFilePath);
        tf_jdk.setText(StringUtil.isEmpty(configData.jdkHomePath) ? "" : configData.jdkHomePath);
        tf_apk.setText(StringUtil.isEmpty(configData.apkFilePath) ? "" : configData.apkFilePath);
        tf_apk_tool.setText(StringUtil.isEmpty(configData.apkToolPath) ? "" : configData.apkToolPath);
        tf_pack.setText(StringUtil.isEmpty(configData.apkPackPath) ? "" : configData.apkPackPath);
        tf_alias.setText(configData.alias);
        tf_keypass.setText(configData.keypass);
        tf_store_pass.setText(configData.storepass);


        if(!StringUtil.isEmpty(configData.apkPackPath))
        {


            StringBuilder text=new StringBuilder();
            File file=new File(configData.apkPackPath);
            if(file.exists())
            {

                File[] children=file.listFiles();

                for(File temp:children)
                {
                    text.append(temp.getName()+"        ");
                }

            }

            ta_packages.setText(text.toString());

        }else
        {
            ta_packages.setText("");
        }



    }


    public  void  setPackages(String packages)
    {

        ta_packages.setText(packages);

    }

    public void setOutput(String s) {
        tf_output.setText(s);
    }

    public void setEdiable(boolean b) {


        doPack.setEnabled(b);
        pickJDk.setEnabled(b);
        pickApk.setEnabled(b);
        pickApkTool.setEnabled(b);
        pickPack.setEnabled(b);
        pickStore.setEnabled(b);
    }


    interface PanelListener {
        void onJDKPick();

        void onStoreFilePick();

        void onPackStart();

        void onApkPick();

        void onPickPack();

        void onPickApkTool();
    }


    public void showMessage(String message) {

        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(root), message);

    }
}
