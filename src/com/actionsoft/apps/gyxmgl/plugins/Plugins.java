package com.actionsoft.apps.gyxmgl.plugins;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;

public class Plugins implements PluginListener {

	@Override
	public List<AWSPluginProfile> register(AppContext arg0) {
		
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		
		//@xmbhxh(*str) 项目编号序号公式注册
		list.add(new AtFormulaPluginProfile("获取项目编号序号", "@xmbhxh(*str)", GyXmpxhXHExpressionImpl.class.getName(), "", "返回获取项目编号序号"));
        
		//@departmentManager('*departmentName') 根据部门名称获取部门负责人
		list.add(new AtFormulaPluginProfile("根据部门名称获取部门负责人", "@departmentManager('*departmentName')", GyXmpxhXHExpressionImpl.class.getName(), "部门名称", "返回部门负责人名称"));
		return list;
	}

}
