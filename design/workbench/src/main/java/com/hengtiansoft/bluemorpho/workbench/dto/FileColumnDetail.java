package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.List;

public class FileColumnDetail {
    private String fileNodeId;
    private String fileName;
    private String fileTags;
    List<FileDetailItem> allFileDetailItems;

    public FileColumnDetail(String fileNodeId, String fileName, String fileTags, List<FileDetailItem> allFileDetailItems) {
        super();
        this.fileNodeId = fileNodeId;
        this.fileName = fileName;
        this.fileTags = fileTags;
        this.allFileDetailItems = allFileDetailItems;
    }

    public String getFileNodeId() {
        return fileNodeId;
    }

    public void setFileNodeId(String fileNodeId) {
        this.fileNodeId = fileNodeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileTags() {
        return fileTags;
    }

    public void setFileTags(String fileTags) {
        this.fileTags = fileTags;
    }

    public List<FileDetailItem> getAllFileDetailItems() {
        return allFileDetailItems;
    }

    public void setAllFileDetailItems(List<FileDetailItem> allFileDetailItems) {
        this.allFileDetailItems = allFileDetailItems;
    }

}
