package com.tamakicontrol.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.menu.JMenuMerge;
import com.inductiveautomation.ignition.designer.model.menu.MenuBarMerge;
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
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public MenuBarMerge getModuleMenu() {
        MenuBarMerge menuBar =  super.getModuleMenu();

        DesignerGitUtilProvider gitUtils = new DesignerGitUtilProvider(context);
        JMenuMerge versionControl = new JMenuMerge("Version Control");
        versionControl.add(new AbstractAction("Commit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Commit Selected");
                gitUtils.commit();
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
