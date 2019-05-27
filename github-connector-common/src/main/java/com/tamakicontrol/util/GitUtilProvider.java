package com.tamakicontrol.util;

public interface GitUtilProvider {

    void commit(String message, String author, String email);

    void pull();

    void push();

    void checkout(String branch);



}
