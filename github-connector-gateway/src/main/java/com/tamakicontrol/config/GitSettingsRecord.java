package com.tamakicontrol.config;

import com.inductiveautomation.ignition.gateway.localdb.persistence.*;
import com.inductiveautomation.ignition.gateway.web.components.editors.PasswordEditorSource;
import simpleorm.dataset.SFieldFlags;

public class GitSettingsRecord extends PersistentRecord {

    public static final RecordMeta<GitSettingsRecord> META = new RecordMeta<GitSettingsRecord>(
            GitSettingsRecord.class, "GitSettingsRecord").setNounKey("GitSettingsRecord.Noun").setNounPluralKey(
            "GitSettingsRecord.Noun.Plural");

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    public static final IdentityField id = new IdentityField(META);

    // Settings
    public static final BooleanField enabled = new BooleanField(META, "Enabled", SFieldFlags.SMANDATORY);
    public static final StringField url = new StringField(META, "URL");
    public static final StringField username = new StringField(META, "Username");
    //TODO encrypt this somehow... i'm not sure of the "correct" way to do this in an application...  Store key in FS?
    public static final StringField password = new StringField(META, "Password");

    /*
    * Sets up wicket editor to use password field in web form
    * */
    static {
        password.getFormMeta().setEditorSource(PasswordEditorSource.getSharedInstance());
    }

    private static final Category Settings = new Category("GitSettingsRecord.Category.Settings", 1000)
            .include(enabled, url, username, password);

    public long getId(){
        return getLong(id);
    }

    public void setId(long _id){
        setLong(id, _id);
    }

    public boolean getEnabled(){
        return getBoolean(enabled);
    }

    public void setEnabled(boolean _enabled){
        setBoolean(enabled, _enabled);
    }

    public String getURL(){
        return getString(url);
    }

    public void setURL(String _url){
        setString(url, _url);
    }

    public String getUsername(){
        return getString(username);
    }

    public void setUsername(String _username){
        setString(username, _username);
    }

    public String getPassword(){
        return getString(password);
    }

    public void setPassword(String _password){
        setString(password, _password);
    }

}
