package com.hengtiansoft.bluemorpho.workbench.domain;

import java.util.List;

public class ScriptHistoryDetail {
    private String scriptName;
    private String commandLineOptions;
    private String log;
    private List<String> outPuts;

    public ScriptHistoryDetail(String scriptName, String commandLineOptions, String log, List<String> outPuts) {
        super();
        this.scriptName = scriptName;
        this.commandLineOptions = commandLineOptions;
        this.log = log;
        this.outPuts = outPuts;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getCommandLineOptions() {
        return commandLineOptions;
    }

    public void setCommandLineOptions(String commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public List<String> getOutPuts() {
        return outPuts;
    }

    public void setOutPuts(List<String> outPuts) {
        this.outPuts = outPuts;
    }
}
