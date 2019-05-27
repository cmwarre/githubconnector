package com.tamakicontrol.util;

public abstract class AbstractGitUtilProvider implements GitUtilProvider {

    @Override
    public void commit(String message, String author, String email) {
        commitImpl(message, author, email);
    }

    protected abstract void commitImpl(String message, String author, String email);

    @Override
    public void pull() {
        pullImpl();
    }

    protected abstract void pullImpl();

    @Override
    public void push() {
        pushImpl();
    }

    protected abstract void pushImpl();

    @Override
    public void checkout(String branch) {
        checkoutImpl(branch);
    }

    protected abstract void checkoutImpl(String branch);

}
