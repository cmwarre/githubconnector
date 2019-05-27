package com.tamakicontrol.designer.util;

import com.inductiveautomation.factorypmi.application.script.builtin.ClientUserUtilities;
import com.inductiveautomation.factorypmi.application.script.builtin.SecurityUtilities;
import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.common.user.ContactInfo;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.common.user.User;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.tamakicontrol.util.AbstractGitUtilProvider;
import com.tamakicontrol.util.GitUtilProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesignerGitUtils extends AbstractGitUtilProvider {

    private static final Logger logger = LoggerFactory.getLogger(DesignerGitUtils.class);

    private final GitUtilProvider rpc;
    private final DesignerContext context;


    /**
     *
     *
     *
     * */
    public DesignerGitUtils(DesignerContext context){
        rpc = ModuleRPCFactory.create("com.tamakicontrol.github-connector", GitUtilProvider.class);
        this.context = context;
    }

    /**
     *
     * getUserInfo
     *
     * @author Cody Warren
     * @since May 23, 2019
     *
     * Fetches current username and contact info to use in commits
     *
     * */
    private String getContactInfo() throws Exception {
        ClientUserUtilities userUtils = new ClientUserUtilities();
        User user = userUtils.getUser(context.getAuthProfileName(), SecurityUtilities.getUsername());

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
        commitImpl(message, SecurityUtilities.getUsername(), getContactInfo());
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
     * TODO add support for username/email/messages
     *
     * */
    @Override
    protected void commitImpl(String message, String author, String email) {
        rpc.commit(message, author, email);
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
