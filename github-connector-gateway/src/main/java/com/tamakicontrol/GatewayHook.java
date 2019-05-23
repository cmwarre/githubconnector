package com.tamakicontrol;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private GatewayContext context;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
    }

    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public Object getRPCHandler(ClientReqSession session, String projectName) {
        super.getRPCHandler(session, projectName);
        return new GatewayGitUtilProvider(context);
    }

}
