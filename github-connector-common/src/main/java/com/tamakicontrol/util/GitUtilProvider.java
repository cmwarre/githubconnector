package com.tamakicontrol.util;

import com.tamakicontrol.GitException;

import java.util.List;

public interface GitUtilProvider {

    void add(String filePath) throws Exception;

    String getCurrentBranch() throws Exception;

    void addBranch(String name) throws Exception;

    void removeBranch(String name) throws Exception;

    void renameBranch(String name, String newName) throws Exception;

    void commit(String message, String author, String email) throws Exception;

    void pull() throws Exception;

    void push() throws Exception;

    void merge() throws Exception;

    void rebase() throws Exception;

    void reset() throws Exception;

    void pullRequest(String title, String message) throws Exception;

    void checkout(String branch) throws GitException;

    List<String> getBranches() throws Exception;

}
