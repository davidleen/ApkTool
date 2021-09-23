package com.changdu.apkpackage.view;

import com.changdu.apkpackage.entity.ResourceValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panel_UpdateResource {
    private JButton btn_add;
    public  JPanel root;
    private JTextField tf_key;
    private JTextField tf_value;
    private JComboBox jb_type;

   public  ResourceValue result;


    public Panel_UpdateResource(final Dialog dialog)
    {
        DefaultComboBoxModel<String> stringComboBoxModel=new DefaultComboBoxModel();



        for(String type: ResourceValue.TYPES)
        {
            stringComboBoxModel.addElement(type);
        }
        jb_type.setModel(stringComboBoxModel);










        btn_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String key = tf_key.getText().trim();
                if(key.equalsIgnoreCase(""))
                {
                    JOptionPane.showMessageDialog(dialog,"资源名称不能为空");
                    return;
                }
                String type= (String) jb_type.getSelectedItem();
                if(type.trim().equalsIgnoreCase(""))
                {
                    JOptionPane.showMessageDialog(dialog,"资源类型未选择");
                    return;
                }

                String value=tf_value.getText().trim();
                switch (type) {
                    case ResourceValue.TYPE_BOOL : {
                        String reg = "^[0|1]{1}$";
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(value);
                        if (!matcher.find()) {
                            JOptionPane.showMessageDialog(dialog, "bool类型的值 只能是0 或者 1");
                            return;
                        }

                        value = value.trim().equalsIgnoreCase("1") ? "true" : "false";
                        break;
                    }

                    case ResourceValue.TYPE_INTEGER:
                    {
                        String reg = "^[1-9]\\d*|0$";
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(value);
                        if (!matcher.find()) {
                            JOptionPane.showMessageDialog(dialog, "integer类型的值 只能是整数值");
                            return;
                        }

                        break;

                    }



                }



                ResourceValue resourceValue=new ResourceValue();
                resourceValue.key=key;
                resourceValue.type=type;
                resourceValue.value=value;
                result=resourceValue;
                dialog.setVisible(false);





            }
        });

    }
}
