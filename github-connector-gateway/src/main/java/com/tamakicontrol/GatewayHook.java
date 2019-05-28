package com.tamakicontrol;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.DefaultConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.tamakicontrol.config.GitSettingsPage;
import com.tamakicontrol.config.GitSettingsRecord;
import com.tamakicontrol.util.GatewayGitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private GatewayContext context;
    private GatewayGitUtils gitUtils;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        BundleUtil.get().addBundle("GitConnector", getClass(), "GitConnector");

        verifySchema(gatewayContext);
        maybeCreateGitSettings(gatewayContext);

        gitUtils = new GatewayGitUtils(gatewayContext);
        initOrLoadGitRepo(gitUtils);
    }

    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {
        BundleUtil.get().removeBundle("GitConnector");
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }

    @Override
    public Object getRPCHandler(ClientReqSession session, String projectName) {
        super.getRPCHandler(session, projectName);
        return gitUtils;
    }


    private void initOrLoadGitRepo(GatewayGitUtils gitUtils){
        if(gitUtils.getGitDir().exists())
            gitUtils.load();
        else
            gitUtils.init();
    }

    /*
     *
     * */
    private void verifySchema(GatewayContext context) {
        try {
            context.getSchemaUpdater().updatePersistentRecords(GitSettingsRecord.META);
        } catch (SQLException e) {
            logger.error("Error verifying persistent record schemas for GitConnector records.", e);
        }
    }

    public void maybeCreateGitSettings(GatewayContext context) {
        logger.trace("Attempting to create GitConnector Settings Record");
        try {
            GitSettingsRecord settingsRecord = context.getLocalPersistenceInterface().createNew(GitSettingsRecord.META);
            settingsRecord.setId(0L);

            /*
             * This doesn't override existing settings, only replaces it with these if we didn't
             * exist already.
             */
            context.getSchemaUpdater().ensureRecordExists(settingsRecord);
        } catch (Exception e) {
            logger.error("Failed to establish GitConnector Record exists", e);
        }

        logger.trace("TamakiScripting Settings Record Established");
    }

    /**
     * This sets up the config panel
     */
    public static final ConfigCategory CONFIG_CATEGORY = new ConfigCategory("gitconnector", "GitConnector.nav.header", 700);

    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(CONFIG_CATEGORY);
    }

    public static final IConfigTab GITHUB_CONNECTOR_CONFIG_ENTRY = DefaultConfigTab.builder()
            .category(CONFIG_CATEGORY)
            .name("settings")
            .i18n("GitConnector.nav.settings.title")
            .page(GitSettingsPage.class)
            .terms("git github connector settings")
            .build();

    @Override
    public List<? extends IConfigTab> getConfigPanels() {
        return Arrays.asList(
                GITHUB_CONNECTOR_CONFIG_ENTRY
        );
    }

}
