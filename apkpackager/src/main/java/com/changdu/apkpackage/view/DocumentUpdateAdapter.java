package com.changdu.apkpackage.view;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public abstract class DocumentUpdateAdapter implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
        handleEvent(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleEvent(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        handleEvent(e);
    }


    private void handleEvent(DocumentEvent event)
    {
        Document document= event.getDocument();
        try {
            onUpdate(document.getText(0,document.getLength()));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected abstract  void onUpdate(String text);
}
