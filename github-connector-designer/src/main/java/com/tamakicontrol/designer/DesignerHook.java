package com.tamakicontrol.designer;

import com.inductiveautomation.factorypmi.application.script.builtin.SecurityUtilities;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.menu.JMenuMerge;
import com.inductiveautomation.ignition.designer.model.menu.MenuBarMerge;
import com.tamakicontrol.designer.util.DesignerGitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class DesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DesignerContext context;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
        this.context = context;

        BundleUtil.get().addBundle("GitConnector", getClass(), "GitConnector");
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public MenuBarMerge getModuleMenu() {
        MenuBarMerge menuBar =  super.getModuleMenu();

        if(menuBar == null){
            logger.warn("Super didn't do anything");
            menuBar = new MenuBarMerge("Version Control");
        }

        DesignerGitUtils gitUtils = new DesignerGitUtils(context);
        JMenuMerge versionControl = new JMenuMerge("Version Control");

        versionControl.add(new AbstractAction("Commit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Commit Selected");
                String message = JOptionPane.showInputDialog("Commit message");

                try{
                    gitUtils.commit(message);
                }catch (Exception e1){
                    logger.error("Exception thrown during commit", e1);
                }
            }
        });

        versionControl.add(new AbstractAction("Pull") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Pull Selected");
                gitUtils.pull();
            }
        });


        versionControl.add(new AbstractAction("Push") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Push Selected");
                gitUtils.push();
            }
        });

        menuBar.add(versionControl);
        return menuBar;
    }
}
