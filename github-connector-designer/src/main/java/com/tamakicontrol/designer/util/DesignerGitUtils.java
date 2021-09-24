package com.tamakicontrol.designer.util;

import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.client.script.ClientSecurityUtilities;
import com.inductiveautomation.ignition.client.script.ClientUserUtilities;
import com.inductiveautomation.ignition.common.user.ContactInfo;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.common.user.User;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.tamakicontrol.GitException;
import com.tamakicontrol.util.AbstractGitUtilProvider;
import com.tamakicontrol.util.GitUtilProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DesignerGitUtils extends AbstractGitUtilProvider {

    private static final Logger logger = LoggerFactory.getLogger(DesignerGitUtils.class);

    private final GitUtilProvider rpc;
    private final DesignerContext context;

    public DesignerGitUtils(DesignerContext context){
        rpc = ModuleRPCFactory.create("com.tamakicontrol.github-connector", GitUtilProvider.class);
        this.context = context;
    }

    /**
     * @return Fetches current username and contact info to use in commits
     * */
    private String getContactInfo() throws Exception {
        ClientUserUtilities userUtils = new ClientUserUtilities();
        User user = userUtils.getUser(context.getAuthProfileName(), ClientSecurityUtilities.getUsername());

        String email = "";

        for(ContactInfo contact : user.getContactInfo()){
            if(ContactType.EMAIL.equals(contact.getContactType())){
                email = contact.getValue();
            }
        }

        logger.info(String.format("%s, %s", user.toString(), email));
        return email;
    }

    public void commit(String message) throws Exception {
        commitImpl(message, ClientSecurityUtilities.getUsername(), getContactInfo());
    }

    @Override
    protected void addImpl(String filePath) throws Exception {
        rpc.add(filePath);
    }

    @Override
    public String getCurrentBranchImpl() throws Exception{
        return rpc.getCurrentBranch();
    }

    @Override
    protected void addBranchImpl(String name) throws Exception {
        rpc.addBranch(name);
    }

    @Override
    protected void renameBranchImpl(String name, String newName) throws Exception {
        rpc.renameBranch(name, newName);
    }

    @Override
    protected void removeBranchImpl(String name) throws Exception  {
        rpc.removeBranch(name);
    }

    /**
     *
     * commitImpl
     *
     * @author Cody Warren
     * @since May 23, 2019
     *
     * Commit the current project to git.
     *
     * */
    @Override
    protected void commitImpl(String message, String author, String email) throws Exception {
        rpc.commit(message, author, email);
    }

    @Override
    protected void pullImpl() throws Exception {
        rpc.pull();
    }

    @Override
    protected void pushImpl() throws Exception {
        rpc.push();
    }

    @Override
    protected void checkoutImpl(String branch) throws GitException {
        rpc.checkout(branch);
    }


    @Override
    protected List<String> getBranchesImpl() throws Exception {
        return rpc.getBranches();
    }

    @Override
    protected void mergeImpl() throws Exception {
        rpc.merge();
    }

    @Override
    protected void rebaseImpl() throws Exception {
        rpc.rebase();
    }

    @Override
    protected void resetImpl() throws Exception {
        rpc.reset();
    }

    @Override
    protected void pullRequestImpl(String title, String message) throws Exception {
        rpc.pullRequest(title, message);
    }
}
