package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.List;
import java.util.Map;

import com.hengtiansoft.bluemorpho.workbench.domain.PicIndexAndBase64;

public class ProgramInfo {
    private String projectName;
    private String rootName;
    private String programName;
    private List<FileStructureNode> rows;
    private Map<String, PicIndexAndBase64> ctrlFlowImgMap;
    private List<PicIndexAndBase64> ctrlFlowImgList;
    private String dependencyImg;
    private List<String> sourceCode;
    private ProgramDetailItem programDetailItem;
    private String[][] ExternalDependencies;
    
    public List<PicIndexAndBase64> getCtrlFlowImgList() {
        return ctrlFlowImgList;
    }

    public void setCtrlFlowImgList(List<PicIndexAndBase64> ctrlFlowImgList) {
        this.ctrlFlowImgList = ctrlFlowImgList;
    }

    public String[][] getExternalDependencies() {
        return ExternalDependencies;
    }

    public void setExternalDependencies(String[][] externalDependencies) {
        ExternalDependencies = externalDependencies;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public List<FileStructureNode> getRows() {
        return rows;
    }

    public void setRows(List<FileStructureNode> rows) {
        this.rows = rows;
    }

    public Map<String, PicIndexAndBase64> getCtrlFlowImgMap() {
        return ctrlFlowImgMap;
    }

    public void setCtrlFlowImgMap(Map<String, PicIndexAndBase64> ctrlFlowImgMap) {
        this.ctrlFlowImgMap = ctrlFlowImgMap;
    }

    public String getDependencyImg() {
        return dependencyImg;
    }

    public void setDependencyImg(String dependencyImg) {
        this.dependencyImg = dependencyImg;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public List<String> getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    public ProgramDetailItem getProgramDetailItem() {
        return programDetailItem;
    }

    public void setProgramDetailItem(ProgramDetailItem programDetailItem) {
        this.programDetailItem = programDetailItem;
    }


}
