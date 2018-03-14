package com.changdu.apkpackage.view;

import com.changdu.apkpackage.TextView;
import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * 界面布局处理。
 */
public class MainPanel implements TextView {
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
    public JTextArea tf_output;
    private JTextArea ta_packages;
    private JRadioButton rd_pack;
    private JCheckBox cb_sign;
    private JCheckBox cb_align;
    private JPanel panel_key_store;
    private JPanel panel_pack;
    private JCheckBox cb_version;
    private JTextField versionCode;
    private JTextField versionName;
    private JPanel panel_change_version;
    private JRadioButton rd_normal;
    private JRadioButton rd_multi_channel;
    private JCheckBox cb_packageName;
    private JPanel panel_package;
    private JTextField packageName;


    PanelListener panelListener;

    public MainPanel(final PanelListener panelListener) {
        this.panelListener = panelListener;


        cb_align.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {


            }
        });
        cb_align.setSelected(true);
        cb_sign.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panel_key_store.setVisible(cb_sign.isSelected());

            }
        });
        cb_sign.setSelected(true);
        panel_key_store.setVisible(true);

        cb_version.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                boolean selected = cb_version.isSelected();
                panel_change_version.setVisible(selected);

                if(selected)
                {
                    cb_sign.setSelected(true);
                    cb_align.setSelected(true);
                }

            }
        });
        cb_version.setSelected(false);
        panel_change_version.setVisible(false);


        cb_packageName.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = cb_packageName.isSelected();
                panel_package.setVisible(selected);
                if(selected)
                {
                    cb_sign.setSelected(true);
                    cb_align.setSelected(true);
                }

            }
        });
        cb_packageName.setSelected(false);
        panel_package.setVisible(false);

        final ButtonGroup bg = new ButtonGroup();
        bg.add(rd_pack);
        bg.add(rd_normal);
        bg.add(rd_multi_channel);


        rd_pack.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panel_pack.setVisible(rd_pack.isSelected());
                cb_packageName.setVisible(!rd_pack.isSelected());
                if (rd_pack.isSelected()) {
                    cb_packageName.setSelected(false);
                    panel_package.setVisible(false);
                }


            }
        });
        rd_normal.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panel_pack.setVisible(!rd_normal.isSelected());

            }
        });
        rd_multi_channel.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panel_pack.setVisible(false);

            }
        });


        rd_normal.setSelected(true);
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


                int type = 0;
                if (rd_pack.isSelected()) {
                    type = PanelListener.WORK_TYPE_PACK;
                }

                if (rd_normal.isSelected()) {
                    type = PanelListener.WORK_TYPE_NORMAL;
                }


                Options options = new Options();
                options.align = cb_align.isSelected();
                options.changeVersion = cb_version.isSelected();
                options.sign = cb_sign.isSelected();
                options.changePackage = cb_packageName.isSelected();
                if (type == 0) return;


                panelListener.onJobStart(type, options);
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


        if (!StringUtil.isEmpty(configData.apkPackPath)) {


            StringBuilder text = new StringBuilder();
            File file = new File(configData.apkPackPath);
            if (file.exists()) {


               java.util.List< File> files=MainFrame.findAllFileToPack(file);
                for (File temp : files) {
                    text.append(temp.getName() + "        ");
                }

            }




            ta_packages.setText(text.toString());

        } else {
            ta_packages.setText("");
        }


    }


    public void setPackages(String packages) {

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
        tf_output.setEditable(b);
    }

    public String getVersionCode() {
        return versionCode.getText().trim();
    }

    public String getVersionName() {
        return versionName.getText().trim();
    }

    @Override
    public void setText(String text) {

        if (tf_output != null) {

            tf_output.setText(text);

        }
    }

    public String getPackageName() {
        return packageName.getText().trim();
    }


    interface PanelListener {

        int WORK_TYPE_PACK = 1;


        int WORK_TYPE_NORMAL = 10;

        void onJDKPick();

        void onStoreFilePick();


        void onJobStart(int type, Options options);

        void onApkPick();

        void onPickPack();

        void onPickApkTool();
    }


    public static class Options {

        boolean sign;
        boolean align;
        boolean changeVersion;
        boolean changePackage;

    }

    public void showMessage(String message) {

        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(root), message);

    }
}
