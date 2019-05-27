package com.tamakicontrol.config;

import com.inductiveautomation.ignition.gateway.localdb.persistence.FormMeta;
import com.inductiveautomation.ignition.gateway.model.IgnitionWebApp;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditMode;
import com.inductiveautomation.ignition.gateway.web.models.LenientResourceModel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Application;
import org.apache.wicket.markup.repeater.RepeatingView;
import simpleorm.dataset.SFieldMeta;
import simpleorm.dataset.SRecordInstance;

import java.util.Map;

public class GitSettingsPage extends RecordEditForm {

    public GitSettingsPage(final IConfigPage configPage){
        super(configPage, null, new LenientResourceModel("GitConnector.nav.settings.panelTitle"),
                ((IgnitionWebApp) Application.get()).getContext().getPersistenceInterface().find(GitSettingsRecord.META, 0L));

    }

    @Override
    public Pair<String, String> getMenuLocation() {
        return Pair.of("git", "Settings");
    }

    @Override
    protected void addField(RepeatingView rowRepeater, FormMeta formMeta, RecordEditMode mode, Map<SFieldMeta, SRecordInstance> recordLookup) {
        super.addField(rowRepeater, formMeta, mode, recordLookup);
    }
}
