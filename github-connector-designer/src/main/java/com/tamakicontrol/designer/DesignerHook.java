package com.tamakicontrol.designer;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.menu.JMenuMerge;
import com.inductiveautomation.ignition.designer.model.menu.MenuBarMerge;
import com.tamakicontrol.GitException;
import com.tamakicontrol.designer.util.DesignerGitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class DesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DesignerContext context;
    private DesignerGitUtils gitUtils;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
        logger.info("Starting up GitConnector");
        this.context = context;
        this.gitUtils = new DesignerGitUtils(context);
        BundleUtil.get().addBundle("GitConnector", getClass(), "GitConnector");
    }

    @Override
    public void shutdown() {
        super.shutdown();
        BundleUtil.get().removeBundle("GitConnector");
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);
        manager.addScriptModule("system.git", new DesignerGitUtils(context));
    }

    // TODO refactor as a separate class?  This is a great chunk of this module
    private JMenu branchesMenu;

    @Override
    public MenuBarMerge getModuleMenu() {
        super.getModuleMenu();
        MenuBarMerge moduleMenu = new MenuBarMerge("com.tamakicontrol.github-connector");
        JMenuMerge versionControlMenu = new JMenuMerge("GitConnector.menu.name");

        versionControlMenu.add(new AbstractAction("Commit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Commit Selected");
                String message = JOptionPane.showInputDialog("Commit message");

                if(message != null && !message.isEmpty()) {
                    try {
                        gitUtils.commit(message);
                    } catch (Exception e1) {
                        logger.error("Exception thrown during commit", e1);
                    }
                }
            }
        });

        versionControlMenu.add(new AbstractAction("Pull") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Pull Selected");
                try {
                    gitUtils.pull();
                }catch (Exception e1){
                    logger.error("Exception during call to pull", e1);
                }
            }
        });

        versionControlMenu.add(new AbstractAction("Push") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Push Selected");
                try {
                    gitUtils.push();
                }catch (Exception e1){
                    logger.error("Error thrown while pushing repo", e1);
                }
            }
        });

        branchesMenu = new JMenu("Branches");
        generateBranchesMenu();

        versionControlMenu.add(branchesMenu);
        moduleMenu.add(versionControlMenu);
        return moduleMenu;
    }

    // TODO add a refresh function
    private void generateBranchesMenu(){
        branchesMenu.removeAll();

        try {
            // TODO replace this with a checkmark or something below
            branchesMenu.add(String.format("Current Branch: %s", gitUtils.getCurrentBranch()));
            branchesMenu.addSeparator();

            branchesMenu.add(new AbstractAction("New Branch") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String branch = JOptionPane.showInputDialog("New Branch Name");

                    if (branch != null && !branch.isEmpty()) {
                        try {
                            gitUtils.addBranch(branch);
                            gitUtils.checkout(branch);
                            reloadProject();
                            generateBranchesMenu();
                        } catch (Exception e1) {
                            logger.error("Exception throw while adding branch", e1);
                        }
                    }
                }
            });
            branchesMenu.addSeparator();

            for (String branch : gitUtils.getBranches()) {

                // Add submenus unless there is a current branch
                // TODO this is broken because of the ref path in the branch names
                if (!gitUtils.getCurrentBranch().equals(branch)) {


                    // TODO need error handling when checking out new branches.  Exceptions should be thrown and handled here
                    JMenu subMenu = new JMenu(branch);
                    subMenu.add(new AbstractAction("Checkout") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                gitUtils.checkout(branch);
                                reloadProject();
                                generateBranchesMenu();
                            }catch (GitException e1){
                                List<String> conflicts = e1.getConflictingPaths();
                                logger.warn(String.format("Branch Checkout Failed - the following conflicts exist: %s",
                                                StringUtils.join(conflicts.toArray(), "\n")));
                            }
                        }
                    });

                    subMenu.add(new AbstractAction("Rename") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String newName = JOptionPane.showInputDialog("New Branch Name");

                            if (branch != null && !branch.isEmpty()) {
                                try {
                                    gitUtils.renameBranch(branch, newName);
                                    reloadProject();
                                    generateBranchesMenu();
                                }catch (Exception e1){
                                    logger.error("Exception thrown while renaming branch", e1);
                                }
                            }
                        }
                    });


                    subMenu.add(new AbstractAction("Delete") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                logger.debug("Deleting branch");
                                gitUtils.removeBranch(branch);
                                generateBranchesMenu();
                            }catch (Exception e1){
                                logger.error("Exception thrown while deleting branch", e1);
                            }
                        }
                    });

                    branchesMenu.add(subMenu);
                }

            }
        }catch (Exception e){
            logger.error("Exception thrown while setting up branches menu", e);
        }
    }

    private void reloadProject(){
        // TODO
    }

}
