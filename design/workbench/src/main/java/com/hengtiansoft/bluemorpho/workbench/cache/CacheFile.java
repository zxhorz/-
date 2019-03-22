package com.hengtiansoft.bluemorpho.workbench.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hengtiansoft.bluemorpho.workbench.dto.FileDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.FileItem;

/**
 * @Description: cache file
 * @author gaochaodeng
 * @date Jul 18, 2018
 */
public class CacheFile implements Serializable {

	private static final long serialVersionUID = 1L;
	private FileItem fileItem;
	private List<FileDetailItem> fileDetailItems = new ArrayList<FileDetailItem>();

	public CacheFile(FileItem fileItem, List<FileDetailItem> fileDetailItems) {
		super();
		this.fileItem = fileItem;
		this.fileDetailItems = fileDetailItems;
	}

	public FileItem getFileItem() {
		return fileItem;
	}

	public void setFileItem(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	public List<FileDetailItem> getFileDetailItems() {
		return fileDetailItems;
	}

	public void setFileDetailItems(List<FileDetailItem> fileDetailItems) {
		this.fileDetailItems = fileDetailItems;
	}
}
