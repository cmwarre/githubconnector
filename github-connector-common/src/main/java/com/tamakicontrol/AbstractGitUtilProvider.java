package com.tamakicontrol;

public abstract class AbstractGitUtilProvider implements GitUtilProvider {

    @Override
    public void commit() {
        commitImpl();
    }

    protected abstract void commitImpl();

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
