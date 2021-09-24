package com.changdu.apkpackage.view;

import com.changdu.apkpackage.TextView;
import com.changdu.apkpackage.dom.StringUtil;
import com.changdu.apkpackage.entity.ConfigData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private JCheckBox cb_packageName;
    private JPanel panel_package;
    private JTextField packageName;
    private JCheckBox cb_channel;
    private JTextField tx_channel;
    private JButton pick_chacenl;
    private JPanel panel_channel;
    private JCheckBox jb_change_app_name;
    private JCheckBox jb_change_package_name;
    private JCheckBox cb_xls_dir;
    private JTextField jt_xls_dir;
    private JButton pick_xls_dir;
    private JPanel panel_xls_dir;
    private JButton btn_add_res;
    private JPanel jp_resource;
    private JCheckBox cb_update_resource;
    private JPanel panel_update_resouce;


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
        cb_align.setVisible(false);



        cb_sign.setSelected(true);
        panel_key_store.setVisible(true);

        cb_channel.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                panel_channel.setVisible(cb_channel.isSelected());
            }
        });
        cb_channel.setSelected(false);
        panel_channel.setVisible(false);


        jb_change_app_name.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {


                panelListener.onNewAppNameUse(  jb_change_app_name.isSelected());
            }
        });

        jb_change_package_name.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panelListener.onNewPackageNameUse(jb_change_package_name.isSelected());
            }
        });

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


        cb_update_resource.setSelected(false);
        panel_update_resouce.setVisible(false);

        final Dimension preferredSize = new Dimension(450   , 120);
        panel_update_resouce.setPreferredSize(preferredSize);
        panel_update_resouce.setMinimumSize(preferredSize);

        cb_update_resource.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = cb_update_resource.isSelected();
                panel_update_resouce.setMinimumSize(preferredSize);
                panel_update_resouce.setPreferredSize(preferredSize);
                panel_update_resouce.setVisible(selected);


            }
        });


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

        cb_xls_dir.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = cb_xls_dir.isSelected();
                panel_xls_dir.setVisible(selected);

            }
        });
        cb_xls_dir.setSelected(false);
        panel_xls_dir.setVisible(false);



        final ButtonGroup bg = new ButtonGroup();
        bg.add(rd_pack);
        bg.add(rd_normal);


        rd_pack.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panel_pack.setVisible(rd_pack.isSelected());
                cb_packageName.setVisible(!rd_pack.isSelected());
                cb_update_resource.setVisible(!rd_pack.isSelected());
                panel_update_resouce.setVisible(!rd_pack.isSelected());
                if (rd_pack.isSelected()) {
                    cb_packageName.setSelected(false);
                    panel_package.setVisible(false);


                }
                cb_channel.setVisible(false);
                panel_channel.setVisible(false);



            }
        });
        rd_normal.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                panel_pack.setVisible(!rd_normal.isSelected());

                cb_channel.setVisible(true);
                panel_channel.setVisible(cb_channel.isSelected());


            }
        });



        rd_normal.setSelected(true);
        pickStore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onStoreFilePick();
            }
        });


        btn_add_res.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.addNewResource();
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
//                options.align = cb_align.isSelected();
                options.changeVersion = cb_version.isSelected();
                options.updateResource = cb_update_resource.isSelected();
                options.sign = cb_sign.isSelected();
                options.changePackage = cb_packageName.isSelected();
                options.searchChannel = cb_xls_dir.isSelected();
                options.pickChannelFile = cb_channel.isSelected();

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

        pick_chacenl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onPickChannelFile();
            }
        });

        pick_xls_dir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelListener.onPickChannelDirectory();
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




    public JPanel getRoot() {
        return root;
    }

    DocumentUpdateAdapter storePassAdapter=new DocumentUpdateAdapter() {
        @Override
        protected void onUpdate(String text) {
            panelListener.onStorePassChange(text);
        }
    }; DocumentUpdateAdapter keyPassAdapter=new DocumentUpdateAdapter() {
        @Override
        protected void onUpdate(String text) {
            panelListener.onKeyPassChange(text);
        }
    }; DocumentUpdateAdapter aliasAdapter=new DocumentUpdateAdapter() {
        @Override
        protected void onUpdate(String text) {
            panelListener.onAliasChange(text);
        }
    };

    public void setConfigData(ConfigData configData) {


        tf_alias.getDocument().removeDocumentListener(aliasAdapter);
        tf_alias.setText(configData.alias);
        tf_alias.getDocument().addDocumentListener(aliasAdapter);

        tf_keypass.getDocument().removeDocumentListener(keyPassAdapter);
        tf_keypass.setText(configData.keypass);
        tf_keypass.getDocument().addDocumentListener(keyPassAdapter);

        tf_store_pass.getDocument().removeDocumentListener(storePassAdapter);
        tf_store_pass.setText(configData.storepass);
        tf_store_pass.getDocument().addDocumentListener(storePassAdapter);


        tf_store_file.setText(StringUtil.isEmpty(configData.keyStoreFilePath) ? "" : configData.keyStoreFilePath);
        tf_jdk.setText(StringUtil.isEmpty(configData.jdkHomePath) ? "" : configData.jdkHomePath);
        tf_apk.setText(StringUtil.isEmpty(configData.apkFilePath) ? "" : configData.apkFilePath);
        tf_apk_tool.setText(StringUtil.isEmpty(configData.apkToolPath) ? "" : configData.apkToolPath);
        tf_pack.setText(StringUtil.isEmpty(configData.apkPackPath) ? "" : configData.apkPackPath);
        jt_xls_dir.setText(StringUtil.isEmpty(configData.channelDirectory) ? "" : configData.channelDirectory);

        tx_channel.setText(configData.channelFilePath);

        jb_change_package_name.setSelected(configData.useNewPackageName);
        jb_change_app_name.setSelected(configData.useNewAppName);


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

        jp_resource.removeAll();
        if(configData.updateValueList!=null) {

            for (int i = 0; i < configData.updateValueList.size(); i++) {


                final JTextField jButton=new JTextField();jButton.setEditable(false);
                jButton.setText(configData.updateValueList.get(i).toString());
                jButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                       if(e.getClickCount()==2) {
                           panelListener.onRemoveResource(jButton.getText());
                       }
                    }
                });



                 jp_resource.add(jButton);


            }
            jp_resource.updateUI();
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
        pick_chacenl.setEnabled(b);
        tx_channel.setEnabled(b);
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
        void onPickChannelFile();
        void onPickChannelDirectory();

        void onNewAppNameUse(boolean use);
        void onNewPackageNameUse(boolean use);

        void addNewResource();

        void onRemoveResource(String text);

        void onAliasChange(String text);

        void onKeyPassChange(String text);

        void onStorePassChange(String text);
    }


    public static class Options {

        boolean sign;
//        boolean align;
        boolean changeVersion;
        boolean updateResource;
        boolean changePackage;
        boolean pickChannelFile;
        boolean searchChannel;
    }

    public void showMessage(String message) {

        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(root), message);

    }
}
