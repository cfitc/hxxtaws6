package com.actionsoft.apps.cfgl.util;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;

/**
 * 注册插件
 */
public class Plugins implements PluginListener {
    public Plugins() {
    }

    public List<AWSPluginProfile> register(AppContext context) {
        // 存放本应用的全部插件扩展点描述
        List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
        // 注册AT公式
        list.add(new AtFormulaPluginProfile("字符串", "@getmanagerid(*boname,*fieldname,processid)", GETmanagementID.class.getName(), "根据部门名称获取部门管理者账号", "返回部门管理者账号"));
        return list;
    }
}