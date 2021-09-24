package com.tamakicontrol.util;

import com.tamakicontrol.GitException;

import java.util.List;

public abstract class AbstractGitUtilProvider implements GitUtilProvider {

    @Override
    public void add(String filePath) throws Exception {
        addImpl(filePath);
    }

    protected abstract void addImpl(String filePath) throws Exception;

    @Override
    public String getCurrentBranch() throws Exception {
        return getCurrentBranchImpl();
    }

    public abstract String getCurrentBranchImpl() throws Exception;

    @Override
    public void addBranch(String name) throws Exception {
        addBranchImpl(name);
    }

    protected abstract void addBranchImpl(String name) throws Exception;

    @Override
    public void removeBranch(String name) throws Exception {
        removeBranchImpl(name);
    }

    protected abstract void removeBranchImpl(String name) throws Exception;

    @Override
    public void commit(String message, String author, String email) throws Exception {
        commitImpl(message, author, email);
    }

    @Override
    public void renameBranch(String name, String newName) throws Exception {
        renameBranchImpl(name, newName);
    }

    protected abstract void renameBranchImpl(String name, String newName) throws Exception;

    protected abstract void commitImpl(String message, String author, String email) throws Exception;

    @Override
    public void pull() throws Exception {
        pullImpl();
    }

    protected abstract void pullImpl() throws Exception;

    @Override
    public void push() throws Exception {
        pushImpl();
    }

    protected abstract void pushImpl() throws Exception;

    @Override
    public void checkout(String branch) throws GitException {
        checkoutImpl(branch);
    }

    protected abstract void checkoutImpl(String branch) throws GitException;

    @Override
    public List<String> getBranches() throws Exception {
        return getBranchesImpl();
    }

    protected abstract List<String> getBranchesImpl() throws Exception;

    @Override
    public void merge() throws Exception {
        mergeImpl();
    }

    protected abstract void mergeImpl() throws Exception;

    @Override
    public void rebase() throws Exception {
        rebaseImpl();
    }

    protected abstract void rebaseImpl() throws Exception;

    @Override
    public void reset() throws Exception {
        resetImpl();
    }

    protected abstract void resetImpl() throws Exception;

    @Override
    public void pullRequest(String title, String message) throws Exception {
        pullRequestImpl(title, message);
    }

    protected abstract void pullRequestImpl(String title, String message) throws Exception;


}
