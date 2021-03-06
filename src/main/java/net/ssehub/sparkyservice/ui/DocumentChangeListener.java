package net.ssehub.sparkyservice.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

abstract class DocumentChangeListener implements DocumentListener {

    public abstract void onChange(DocumentEvent e);
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        onChange(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onChange(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        onChange(e);
    }

}
