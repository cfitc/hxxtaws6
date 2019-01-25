package com.actionsoft.apps.gyxmgl.controller;

/**
 * 数据初始化
 */
import com.actionsoft.apps.gyxmgl.util.DataInitTool;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

/**
 * 数据初始化模块使用，初始化BINDID
 * @author Administrator
 *
 */
@Controller
public class DataInitController {
	@Mapping("com.actionsoft.apps.gyxmgl.dataInit")
	public String DataInit(){
		DataInitTool dit = new DataInitTool();
		String result = dit.DateInit();
		return result;
	}
}
