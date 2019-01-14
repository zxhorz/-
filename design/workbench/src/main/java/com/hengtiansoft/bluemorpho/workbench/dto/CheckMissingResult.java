package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.List;

public class CheckMissingResult {
    List<CheckMissingItem> checkMissiongItems;
    Integer tableLength;
    Integer programLength;
    Integer copybookLength;
    
    public CheckMissingResult() {
        super();
    }
    public List<CheckMissingItem> getCheckMissiongItems() {
        return checkMissiongItems;
    }
    public void setCheckMissiongItems(List<CheckMissingItem> checkMissiongItems) {
        this.checkMissiongItems = checkMissiongItems;
    }
    public Integer getTableLength() {
        return tableLength;
    }
    public void setTableLength(Integer tableLength) {
        this.tableLength = tableLength;
    }
    public Integer getProgramLength() {
        return programLength;
    }
    public void setProgramLength(Integer programLength) {
        this.programLength = programLength;
    }
    public Integer getCopybookLength() {
        return copybookLength;
    }
    public void setCopybookLength(Integer copybookLength) {
        this.copybookLength = copybookLength;
    }
    
}
