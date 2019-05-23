package com.tamakicontrol.designer;

import javax.swing.Action;
import java.beans.PropertyChangeListener;

public abstract class AbstractMenuItem implements Action {

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void putValue(String key, Object value) {

    }

    private boolean enabled = true;

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // nop
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // nop
    }

}
