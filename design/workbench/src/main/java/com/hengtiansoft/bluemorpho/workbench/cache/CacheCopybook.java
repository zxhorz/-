package com.hengtiansoft.bluemorpho.workbench.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hengtiansoft.bluemorpho.workbench.dto.CopybookDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgramDetailItem;

/**
 * @Description: cache copybook
 * @author gaochaodeng
 * @date Jul 18, 2018
 */
public class CacheCopybook implements Serializable {
	private static final long serialVersionUID = 1L;

	private CopybookDetailItem copybookDetailItem;
	private List<ProgramDetailItem> programDetailItems = new ArrayList<ProgramDetailItem>();

	public CacheCopybook(CopybookDetailItem copybookDetailItem,
			List<ProgramDetailItem> programDetailItems) {
		super();
		this.copybookDetailItem = copybookDetailItem;
		this.programDetailItems = programDetailItems;
	}

	public CopybookDetailItem getCopybookDetailItem() {
		return copybookDetailItem;
	}

	public void setCopybookDetailItem(CopybookDetailItem copybookDetailItem) {
		this.copybookDetailItem = copybookDetailItem;
	}

	public List<ProgramDetailItem> getProgramDetailItems() {
		return programDetailItems;
	}

	public void setProgramDetailItems(List<ProgramDetailItem> programDetailItems) {
		this.programDetailItems = programDetailItems;
	}

}
