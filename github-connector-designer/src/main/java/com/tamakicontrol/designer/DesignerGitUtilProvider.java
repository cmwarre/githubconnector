package com.tamakicontrol.designer;

import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.tamakicontrol.AbstractGitUtilProvider;
import com.tamakicontrol.GitUtilProvider;

public class DesignerGitUtilProvider extends AbstractGitUtilProvider {

    private final GitUtilProvider rpc;

    public DesignerGitUtilProvider(DesignerContext context){
        rpc = ModuleRPCFactory.create("com.tamakicontrol.github-connector", GitUtilProvider.class);
    }

    @Override
    protected void commitImpl() {
        rpc.commit();
    }

    @Override
    protected void pullImpl() {
        rpc.pull();
    }

    @Override
    protected void pushImpl() {
        rpc.push();
    }

    @Override
    protected void checkoutImpl(String branch) {
        rpc.checkout(branch);
    }

}
