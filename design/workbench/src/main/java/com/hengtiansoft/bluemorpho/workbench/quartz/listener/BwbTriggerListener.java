package com.hengtiansoft.bluemorpho.workbench.quartz.listener;

import org.quartz.listeners.TriggerListenerSupport;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 2:23:51 PM
 */
public class BwbTriggerListener extends TriggerListenerSupport {

	@Override
	public String getName() {
		return BwbTriggerListener.class.toString();
	}
}
