package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jul 2, 2018 4:27:34 PM
 */
@SuppressWarnings("serial")
public class CorpusImportResponse implements Serializable {

	private int successCount;
	private int failedCount;

	public CorpusImportResponse() {
	}

	public CorpusImportResponse(int successCount, int failedCount) {
		super();
		this.successCount = successCount;
		this.failedCount = failedCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

}
