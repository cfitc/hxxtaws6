package com.actionsoft.apps.skins.mportallocal.plugins;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.ACPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.DCPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.SkinsPluginProfile;
import com.actionsoft.bpms.commons.security.ac.model.ACAccessMode;

/**
 * 插件注册
 * 
 * @author wangsz
 */
public class Plugins implements PluginListener {
	public static DCPluginProfile faviconDc = new DCPluginProfile("!favicon", FaviconFileProcessor.class.getName(),
			"favicon文件", false);
	public static DCPluginProfile clogoDc = new DCPluginProfile("!clogo", ClogoFileProcessor.class.getName(), "logo文件",
			false);

	public Plugins() {
	}

	public List<AWSPluginProfile> register(AppContext context) {
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		list.add(new SkinsPluginProfile(MportalSkins.class.getName(), false));
		// 注册AC权限
		ACAccessMode[] accessModes = { new ACAccessMode("可访问门户权限", 0) };
		String[] assignmentTypes = { ACPluginProfile.ASSN_TYPE_COMPANY, ACPluginProfile.ASSN_TYPE_DEPARTMENT,
				ACPluginProfile.ASSN_TYPE_ROLE, ACPluginProfile.ASSN_TYPE_TEAM, ACPluginProfile.ASSN_TYPE_USER };
		list.add(new ACPluginProfile("mportallocal.plugin.AC", "门户权限设置", assignmentTypes, accessModes, false, true));
		list.add(faviconDc);
		list.add(clogoDc);
		return list;
	}

}
