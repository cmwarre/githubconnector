package com.tamakicontrol;

public interface GitUtilProvider {

    void commit();

    void pull();

    void push();

    void checkout(String branch);



}
