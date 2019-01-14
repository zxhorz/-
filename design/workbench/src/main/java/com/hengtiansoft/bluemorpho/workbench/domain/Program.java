package com.hengtiansoft.bluemorpho.workbench.domain;

import java.util.List;
import java.util.Map;

import com.hengtiansoft.bluemorpho.workbench.dto.FileStructureNode;

public class Program {
    private String projectName;
    private String rootName;
    private String programName;
    private List<FileStructureNode> fileStructureNodes;
    private Map<String, PicIndexAndBase64> ctrlFlowImgMap;
    private String dependencyImg;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public List<FileStructureNode> getFileStructureNodes() {
        return fileStructureNodes;
    }

    public void setFileStructureNodes(List<FileStructureNode> fileStructureNodes) {
        this.fileStructureNodes = fileStructureNodes;
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
}
