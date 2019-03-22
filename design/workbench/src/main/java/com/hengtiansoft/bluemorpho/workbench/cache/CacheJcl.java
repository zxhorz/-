package com.hengtiansoft.bluemorpho.workbench.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hengtiansoft.bluemorpho.workbench.dto.JclDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.JclStepItem;

/**
 * @Description: cache jcl
 * @author gaochaodeng
 * @date Jul 18, 2018
 */
public class CacheJcl implements Serializable {

	private static final long serialVersionUID = 1L;
	private JclDetailItem jclDetailItem;
	private List<JclStepItem> jclStepItems = new ArrayList<JclStepItem>();

	public CacheJcl(JclDetailItem jclDetailItem, List<JclStepItem> jclStepItems) {
		super();
		this.jclDetailItem = jclDetailItem;
		this.jclStepItems = jclStepItems;
	}

	public JclDetailItem getJclDetailItem() {
		return jclDetailItem;
	}

	public void setJclDetailItem(JclDetailItem jclDetailItem) {
		this.jclDetailItem = jclDetailItem;
	}

	public List<JclStepItem> getJclStepItems() {
		return jclStepItems;
	}

	public void setJclStepItems(List<JclStepItem> jclStepItems) {
		this.jclStepItems = jclStepItems;
	}

}
