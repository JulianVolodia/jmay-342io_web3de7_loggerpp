//
// Burp Suite Logger++
// 
// Released as open source by NCC Group Plc - https://www.nccgroup.trust/
// 
// Developed by Soroush Dalili (@irsdl)
//
// Project link: http://www.github.com/nccgroup/BurpSuiteLoggerPlusPlus
//
// Released under AGPL see LICENSE for more information
//

package com.nccgroup.loggerplusplus.preferences;

import burp.api.montoya.http.message.HttpRequestResponse;
import com.coreyd97.BurpExtenderUtilities.Alignment;
import com.coreyd97.BurpExtenderUtilities.ComponentGroup;
import com.coreyd97.BurpExtenderUtilities.ComponentGroup.Orientation;
import com.coreyd97.BurpExtenderUtilities.PanelBuilder;
import com.coreyd97.BurpExtenderUtilities.Preferences;
import com.google.gson.reflect.TypeToken;
import com.nccgroup.loggerplusplus.LoggerPlusPlus;
import com.nccgroup.loggerplusplus.exports.*;
import com.nccgroup.loggerplusplus.filter.FilterExpression;
import com.nccgroup.loggerplusplus.filter.colorfilter.TableColorRule;
import com.nccgroup.loggerplusplus.filter.parser.ParseException;
import com.nccgroup.loggerplusplus.filter.savedfilter.SavedFilter;
import com.nccgroup.loggerplusplus.filter.tag.Tag;
import com.nccgroup.loggerplusplus.imports.LoggerImport;
import com.nccgroup.loggerplusplus.logentry.LogEntryField;
import com.nccgroup.loggerplusplus.logview.logtable.LogTableColumn;
import com.nccgroup.loggerplusplus.logview.logtable.LogTableColumnModel;
import com.nccgroup.loggerplusplus.util.MoreHelp;
import com.nccgroup.loggerplusplus.util.userinterface.renderer.TagRenderer;
import com.nccgroup.loggerplusplus.i18n.Messages;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import static com.nccgroup.loggerplusplus.util.Globals.*;

public class PreferencesPanel extends JScrollPane {

    private final PreferencesController preferencesController;
    private final Preferences preferences;

    private final JToggleButton tglbtnIsEnabled;
    private final JLabel esValueChangeWarning = new JLabel(
            Messages.getString("elastic.warning.change"));

    /**
     * Create the panel.
     */
    public PreferencesPanel(PreferencesController preferencesController) {
        this.preferencesController = preferencesController;
        this.preferences = preferencesController.getPreferences();
        this.esValueChangeWarning.setForeground(Color.RED);

        ComponentGroup statusPanel = new ComponentGroup(Orientation.HORIZONTAL, Messages.getString("status.title"));
        tglbtnIsEnabled = new JToggleButton(new AbstractAction(Messages.getString("status.running", APP_NAME)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton thisButton = (JToggleButton) e.getSource();
                toggleEnabledButton(thisButton.isSelected());
            }
        });
        statusPanel.add(tglbtnIsEnabled);
        tglbtnIsEnabled.setSelected(preferences.getSetting(PREF_ENABLED));

        ComponentGroup doNotLogPanel = new ComponentGroup(Orientation.HORIZONTAL, Messages.getString("logfilter.title"));
        JTextField doNotLogFilterField = new JTextField();
        doNotLogPanel.add(new JLabel(Messages.getString("logfilter.label")));
        GridBagConstraints gbc = doNotLogPanel.generateNextConstraints(true);
        gbc.weightx = 100;
        doNotLogPanel.add(doNotLogFilterField, gbc);
        JToggleButton applyButton = new JToggleButton(new AbstractAction(Messages.getString("logfilter.enable")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton thisButton = (JToggleButton) e.getSource();
                if(thisButton.isSelected()){
                    try {
                        FilterExpression expression = new FilterExpression(doNotLogFilterField.getText());
                        doNotLogFilterField.setText(expression.toString());
                        preferences.setSetting(PREF_DO_NOT_LOG_IF_MATCH, expression);
                        doNotLogFilterField.setEnabled(false);
                        thisButton.setText(Messages.getString("logfilter.disable"));
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(thisButton, Messages.getString("logfilter.parse.error", ex.getMessage()));
                        thisButton.setSelected(false);
                    }
                }else{
                    preferences.setSetting(PREF_DO_NOT_LOG_IF_MATCH, null);
                    doNotLogFilterField.setEnabled(true);
                    thisButton.setText(Messages.getString("logfilter.enable"));
                }
            }
        });
        FilterExpression filterExpression = preferences.getSetting(PREF_DO_NOT_LOG_IF_MATCH);
        if(filterExpression != null){
            doNotLogFilterField.setText(filterExpression.toString());
            applyButton.doClick();
        }
        doNotLogPanel.add(applyButton);


        ComponentGroup logFromPanel = new ComponentGroup(Orientation.VERTICAL, Messages.getString("logfrom.title"));
        logFromPanel.addPreferenceComponent(preferences, PREF_RESTRICT_TO_SCOPE, Messages.getString("logfrom.scope"));
        GridBagConstraints strutConstraints = logFromPanel.generateNextConstraints(true);
        strutConstraints.weighty = strutConstraints.weightx = 0;
        logFromPanel.add(Box.createVerticalStrut(10), strutConstraints);
        JCheckBox logAllTools = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_GLOBAL, Messages.getString("logfrom.alltools"));
        JCheckBox logSpider = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_SPIDER, Messages.getString("logfrom.spider"));
        JCheckBox logIntruder = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_INTRUDER, Messages.getString("logfrom.intruder"));
        JCheckBox logScanner = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_SCANNER, Messages.getString("logfrom.scanner"));
        JCheckBox logRepeater = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_REPEATER, Messages.getString("logfrom.repeater"));
        JCheckBox logSequencer = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_SEQUENCER, Messages.getString("logfrom.sequencer"));
        JCheckBox logProxy = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_PROXY, Messages.getString("logfrom.proxy"));
        JCheckBox logTarget = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_TARGET_TAB, Messages.getString("logfrom.target"));
        JCheckBox logExtender = logFromPanel.addPreferenceComponent(preferences, PREF_LOG_EXTENSIONS, Messages.getString("logfrom.extender"));

        strutConstraints = logFromPanel.generateNextConstraints(true);
        strutConstraints.weighty = strutConstraints.weightx = 0;
        logFromPanel.add(Box.createVerticalStrut(10), strutConstraints);
        // logFromPanel.addPreferenceComponent(preferences, PREF_LOG_OTHER_LIVE, "Log
        // Non-Proxy Tools Live");

        { // Disable check boxes if global logging is enabled.
            boolean globalDisabled = !logAllTools.isSelected();
            logSpider.setEnabled(globalDisabled);
            logIntruder.setEnabled(globalDisabled);
            logScanner.setEnabled(globalDisabled);
            logRepeater.setEnabled(globalDisabled);
            logSequencer.setEnabled(globalDisabled);
            logProxy.setEnabled(globalDisabled);
            logTarget.setEnabled(globalDisabled);
            logExtender.setEnabled(globalDisabled);
        }

        logAllTools.addChangeListener(changeEvent -> {
            boolean globalDisabled = !logAllTools.isSelected();
            logSpider.setEnabled(globalDisabled);
            logIntruder.setEnabled(globalDisabled);
            logScanner.setEnabled(globalDisabled);
            logRepeater.setEnabled(globalDisabled);
            logSequencer.setEnabled(globalDisabled);
            logProxy.setEnabled(globalDisabled);
            logTarget.setEnabled(globalDisabled);
            logExtender.setEnabled(globalDisabled);
        });

        ComponentGroup importGroup = new ComponentGroup(Orientation.VERTICAL, Messages.getString("import.title"));
        importGroup.addPreferenceComponent(preferences, PREF_AUTO_IMPORT_PROXY_HISTORY,
                Messages.getString("import.autoimport"));
        importGroup.add(new JButton(new AbstractAction(Messages.getString("import.burphistory")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int historySize = LoggerPlusPlus.montoya.proxy().history().size();
                int maxEntries = preferences.getSetting(PREF_MAXIMUM_ENTRIES);
                String message = Messages.getString("import.burphistory.message", historySize);
                if (historySize > maxEntries) {
                    message += "\n" + Messages.getString("import.burphistory.truncate", maxEntries);
                }

                int result = MoreHelp.askConfirmMessage(Messages.getString("import.burphistory.title"), message,
                        new String[] { Messages.getString("import.burphistory.buttons.import"), Messages.getString("import.burphistory.buttons.cancel") });

                if (result == JOptionPane.OK_OPTION) {
                    boolean sendToAutoExporters = false;
                    if (LoggerPlusPlus.instance.getExportController().getEnabledExporters().size() > 0) {
                        int res = JOptionPane.showConfirmDialog(LoggerPlusPlus.instance.getLoggerFrame(),
                                Messages.getString("import.autoexporter.message"),
                                Messages.getString("import.autoexporter.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        sendToAutoExporters = res == JOptionPane.YES_OPTION;
                    }

                    LoggerPlusPlus.instance.getLogProcessor().importProxyHistory(sendToAutoExporters);
                }
            }
        }));

        importGroup.add(new JButton(new AbstractAction(Messages.getString("import.wstalker")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<HttpRequestResponse> requests = LoggerImport.importWStalker();
                if (LoggerPlusPlus.instance.getExportController().getEnabledExporters().size() > 0) {
                    int res = JOptionPane.showConfirmDialog(LoggerPlusPlus.instance.getLoggerFrame(),
                            Messages.getString("import.autoexporter.message"),
                            Messages.getString("import.autoexporter.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    LoggerImport.loadImported(requests, res == JOptionPane.YES_OPTION);
                } else {
                    LoggerImport.loadImported(requests, false);
                }
            }
        }));

        importGroup.add(new JButton(new AbstractAction(Messages.getString("import.zap")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<HttpRequestResponse> requests;
                try{
                    requests = LoggerImport.importZAP();
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(LoggerPlusPlus.instance.getLoggerFrame(), Messages.getString("import.zap.error", ex.getMessage()), Messages.getString("import.zap.error.title"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (LoggerPlusPlus.instance.getExportController().getEnabledExporters().size() > 0) {
                    int res = JOptionPane.showConfirmDialog(LoggerPlusPlus.instance.getLoggerFrame(),
                            Messages.getString("import.autoexporter.message"),
                            Messages.getString("import.autoexporter.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    LoggerImport.loadImported(requests, res == JOptionPane.YES_OPTION);
                } else {
                    LoggerImport.loadImported(requests, false);
                }

                JOptionPane.showMessageDialog(LoggerPlusPlus.instance.getLoggerFrame(), Messages.getString("import.zap.success", requests.size()), Messages.getString("import.zap.success.title"), JOptionPane.INFORMATION_MESSAGE);
            }
        }));

        importGroup.add(new JButton(new AbstractAction(Messages.getString("import.json")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<HttpRequestResponse> requests = LoggerImport.importFromExportedJson();
                if (LoggerPlusPlus.instance.getExportController().getEnabledExporters().size() > 0) {
                    int res = JOptionPane.showConfirmDialog(LoggerPlusPlus.instance.getLoggerFrame(),
                            Messages.getString("import.autoexporter.message"),
                            Messages.getString("import.autoexporter.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    LoggerImport.loadImported(requests, res == JOptionPane.YES_OPTION);
                } else {
                    LoggerImport.loadImported(requests, false);
                }
            }
        }));

        ComponentGroup exportGroup = new ComponentGroup(Orientation.HORIZONTAL);
        HashMap<Class<? extends LogExporter>, LogExporter> exporters = LoggerPlusPlus.instance
                .getExportController().getExporters();
        exportGroup.add(((ExportPanelProvider) exporters.get(CSVExporter.class)).getExportPanel());
        exportGroup.add(((ExportPanelProvider) exporters.get(JSONExporter.class)).getExportPanel());
        exportGroup.add(((ExportPanelProvider) exporters.get(HARExporter.class)).getExportPanel());
        exportGroup.add(((ExportPanelProvider) exporters.get(ElasticExporter.class)).getExportPanel());

        ComponentGroup otherPanel = new ComponentGroup(Orientation.VERTICAL, Messages.getString("other.title"));
        JSpinner spnRespTimeout = otherPanel.addPreferenceComponent(preferences, PREF_RESPONSE_TIMEOUT,
                Messages.getString("other.responsetimeout"));
        ((SpinnerNumberModel) spnRespTimeout.getModel()).setMinimum(10);
        ((SpinnerNumberModel) spnRespTimeout.getModel()).setMaximum(600);
        ((SpinnerNumberModel) spnRespTimeout.getModel()).setStepSize(10);

        JSpinner spnMaxEntries = otherPanel.addPreferenceComponent(preferences, PREF_MAXIMUM_ENTRIES,
                Messages.getString("other.maxentries"));
        ((SpinnerNumberModel) spnMaxEntries.getModel()).setMinimum(10);
        ((SpinnerNumberModel) spnMaxEntries.getModel()).setMaximum(Integer.MAX_VALUE);
        ((SpinnerNumberModel) spnMaxEntries.getModel()).setStepSize(10);

        JSpinner spnSearchThreads = otherPanel.addPreferenceComponent(preferences, PREF_SEARCH_THREADS,
                Messages.getString("other.searchthreads"));
        ((SpinnerNumberModel) spnSearchThreads.getModel()).setMinimum(1);
        ((SpinnerNumberModel) spnSearchThreads.getModel()).setMaximum(50);
        ((SpinnerNumberModel) spnSearchThreads.getModel()).setStepSize(1);

        JSpinner maxResponseSize = otherPanel.addPreferenceComponent(preferences, PREF_MAX_RESP_SIZE,
                Messages.getString("other.maxrespsize"));
        ((SpinnerNumberModel) maxResponseSize.getModel()).setMinimum(0);
        ((SpinnerNumberModel) maxResponseSize.getModel()).setMaximum(1000000);
        ((SpinnerNumberModel) maxResponseSize.getModel()).setStepSize(1);

        JCheckBox tagStyle = otherPanel.addPreferenceComponent(preferences, PREF_TABLE_PILL_STYLE, Messages.getString("other.tagstyle"));

        preferences.addSettingListener((source, settingName, newValue) -> {
            if(Objects.equals(settingName, PREF_TABLE_PILL_STYLE)){
                LogTableColumnModel columnModel = LoggerPlusPlus.instance.getLogViewController().getLogViewPanel().getLogTable().getColumnModel();
                Optional<LogTableColumn> column = columnModel.getAllColumns().stream().filter(logTableColumn -> logTableColumn.getIdentifier() == LogEntryField.TAGS).findFirst();
                if(column.isEmpty()) return;
                if((boolean) newValue) {
                    column.get().setCellRenderer(new TagRenderer());
                }else{
                    column.get().setCellRenderer(new DefaultTableCellRenderer());
                }
            }
        });

        ComponentGroup savedFilterSharing = new ComponentGroup(Orientation.VERTICAL, Messages.getString("savedfilter.title"));
        savedFilterSharing.add(new JButton(new AbstractAction(Messages.getString("savedfilter.import")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String json = MoreHelp.showLargeInputDialog(Messages.getString("savedfilter.import"), null);
                ArrayList<SavedFilter> importedFilters = preferencesController.getGsonProvider().getGson()
                        .fromJson(json, new TypeToken<ArrayList<SavedFilter>>() {
                        }.getType());
                ArrayList<SavedFilter> savedFilters = preferences.getSetting(PREF_SAVED_FILTERS);
                for (SavedFilter importedFilter : importedFilters) {
                    if (!savedFilters.contains(importedFilter))
                        savedFilters.add(importedFilter);
                }
                preferences.setSetting(PREF_SAVED_FILTERS, savedFilters);
            }
        }));

        savedFilterSharing.add(new JButton(new AbstractAction(Messages.getString("savedfilter.export")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<SavedFilter> savedFilters = preferences.getSetting(PREF_SAVED_FILTERS);
                String jsonOutput = preferencesController.getGsonProvider().getGson().toJson(savedFilters);
                MoreHelp.showLargeOutputDialog(Messages.getString("savedfilter.export"), jsonOutput);
            }
        }));

        ComponentGroup colorFilterSharing = new ComponentGroup(Orientation.VERTICAL, Messages.getString("colorfilter.title"));
        colorFilterSharing.add(new JButton(new AbstractAction(Messages.getString("colorfilter.import")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String json = MoreHelp.showLargeInputDialog(Messages.getString("colorfilter.import"), null);
                Map<UUID, TableColorRule> colorFilterMap = preferencesController.getGsonProvider().getGson().fromJson(json,
                        new TypeToken<Map<UUID, TableColorRule>>() {
                        }.getType());
                if(colorFilterMap == null) return;
                for (TableColorRule tableColorRule : colorFilterMap.values()) {
                    LoggerPlusPlus.instance.getLibraryController().addColorFilter(tableColorRule);
                }
            }
        }));

        colorFilterSharing.add(new JButton(new AbstractAction(Messages.getString("colorfilter.export")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<UUID, TableColorRule> colorFilters = preferences.getSetting(PREF_COLOR_FILTERS);
                String jsonOutput = preferencesController.getGsonProvider().getGson().toJson(colorFilters);
                MoreHelp.showLargeOutputDialog(Messages.getString("colorfilter.export"), jsonOutput);
            }
        }));

        ComponentGroup tagSharing = new ComponentGroup(Orientation.VERTICAL, Messages.getString("tag.title"));
        tagSharing.add(new JButton(new AbstractAction(Messages.getString("tag.import")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String json = MoreHelp.showLargeInputDialog(Messages.getString("tag.import"), null);
                Map<UUID, Tag> tagMap = preferencesController.getGsonProvider().getGson().fromJson(json,
                        new TypeToken<Map<UUID, Tag>>() {
                        }.getType());
                if(tagMap == null) return;
                for (Tag tag : tagMap.values()) {
                    LoggerPlusPlus.instance.getLibraryController().addTag(tag);
                }
            }
        }));

        tagSharing.add(new JButton(new AbstractAction(Messages.getString("tag.export")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<UUID, Tag> tags = preferences.getSetting(PREF_TAG_FILTERS);
                String jsonOutput = preferencesController.getGsonProvider().getGson().toJson(tags);
                MoreHelp.showLargeOutputDialog(Messages.getString("tag.export"), jsonOutput);
            }
        }));

        ComponentGroup reflectionsPanel = new ComponentGroup(Orientation.HORIZONTAL, Messages.getString("reflections.title"));
        reflectionsPanel.add(new JButton(new AbstractAction(Messages.getString("reflections.configfilters")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoggerPlusPlus.instance.getReflectionController().showFilterConfigDialog();
            }
        }));
        reflectionsPanel.add(new JButton(new AbstractAction(Messages.getString("reflections.configtransform")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoggerPlusPlus.instance.getReflectionController().showValueTransformerDialog();
            }
        }));

        ComponentGroup resetPanel = new ComponentGroup(Orientation.VERTICAL, Messages.getString("reset.title"));
        resetPanel.add(new JButton(new AbstractAction(Messages.getString("reset.all")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null,
                        Messages.getString("reset.all.confirm"), Messages.getString("reset.all.confirm.title"),
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    preferences.resetSettings(preferences.getRegisteredSettings().keySet());
                    LoggerPlusPlus.instance.getLogViewController().getLogTableController()
                            .reinitialize();
                }
            }
        }));

        resetPanel.add(new JButton(new AbstractAction(Messages.getString("reset.clearlogs")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoggerPlusPlus.instance.getLogViewController().getLogTableController().reset();
            }
        }));

        ComponentGroup notesPanel = new ComponentGroup(Orientation.VERTICAL, Messages.getString("notes.title"));
        notesPanel.add(new JLabel(Messages.getString("notes.note0")));
        notesPanel.add(new JLabel(Messages.getString("notes.note1")));
        notesPanel.add(new JLabel(Messages.getString("notes.note2")));
        notesPanel.add(new JLabel(Messages.getString("notes.note3")));
        notesPanel.add(new JLabel(Messages.getString("notes.note4")));

        JPanel mainComponent = new PanelBuilder()
                .setAlignment(Alignment.TOPMIDDLE)
                .setScaleX(0)
                .setScaleY(0)
                .setComponentGrid(new JPanel[][] { new JPanel[] { statusPanel, statusPanel, statusPanel, statusPanel, statusPanel, statusPanel },
                        new JPanel[] { doNotLogPanel, doNotLogPanel, doNotLogPanel, doNotLogPanel, doNotLogPanel, doNotLogPanel},
                        new JPanel[] { logFromPanel, logFromPanel, importGroup, importGroup, importGroup, importGroup },
                        new JPanel[] { logFromPanel, logFromPanel, exportGroup, exportGroup, exportGroup, exportGroup },
                        new JPanel[] { savedFilterSharing, savedFilterSharing, colorFilterSharing, colorFilterSharing, tagSharing, tagSharing },
                        new JPanel[] { reflectionsPanel, reflectionsPanel, reflectionsPanel, reflectionsPanel, reflectionsPanel, reflectionsPanel },
                        new JPanel[] { otherPanel, otherPanel, otherPanel, otherPanel, otherPanel, otherPanel },
                        new JPanel[] { resetPanel, resetPanel, resetPanel, resetPanel, resetPanel, resetPanel },
                        new JPanel[] { notesPanel, notesPanel, notesPanel, notesPanel, notesPanel, notesPanel }, })
                .build();

        this.setViewportView(mainComponent);
    }

    private void toggleEnabledButton(boolean isSelected) {
        tglbtnIsEnabled.setText(isSelected ? Messages.getString("status.running", APP_NAME) : Messages.getString("status.stopped", APP_NAME));
        tglbtnIsEnabled.setSelected(isSelected);
        preferences.setSetting(PREF_ENABLED, isSelected);
    }
}
