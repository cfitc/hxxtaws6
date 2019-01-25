package com.actionsoft.apps.gyxmgl.event;

import java.util.List;

import com.actionsoft.apps.gyxmgl.util.StringUtil;
import com.actionsoft.bpms.bo.design.model.BOItemModel;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridFilterListener;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridRowLookAndFeel;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.form.design.model.FormItemModel;

/**
 * 附件信息子表(PROCESS_FORM_GRID_FILTER流程事件触发器[显示子表列表前被触发])
 * @author Administrator
 *
 */
public class BizFormLoadFileDataBiz extends FormGridFilterListener {
	public BizFormLoadFileDataBiz(){
		super.setDescription("控制附件的删除权限，不能删除非自己的附件信息");
		super.setVersion("V1.0");
	}
	@Override
	public FormGridRowLookAndFeel acceptRowData(ProcessExecutionContext context,
			List<BOItemModel> boItemList, BO boData) {
		String tableName = context.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BONAME);
		String loginUid = context.getUserContext().getUID();
		if(tableName.equals("BO_ACT_ATTACH")){
			FormGridRowLookAndFeel diyLookAndFeel = new FormGridRowLookAndFeel();
			//文档类别
			String wdlb = boData.getString("WDLB");
			//更新已有文件
			String gxyywj = boData.getString("GXYYWJ");
			//文件状态
			String wjzt = boData.getString("WJZT");
			//备注
			String memo = boData.getString("MEMO");
			//上传人
			String scr = boData.getString("SCR");
			String uid = StringUtil.getUserStr(scr);
			if(!loginUid.equals(uid)){
				 diyLookAndFeel.setLink(false);// 设置这行数据不展示链接
		         diyLookAndFeel.setRemove(false);// 设置这行数据不允许删除
		        // diyLookAndFeel.setCellCSS("style='background-color:yellow;font-color: ffffff;font-weight: bold;height: 125px'");
			}else if(loginUid.equals(uid)){
				//可修改
				diyLookAndFeel.setLink(true);
			}
			boData.set("WDLB", wdlb);
			boData.set("GXYYWJ", gxyywj);
			boData.set("WJZT", wjzt);
			boData.set("MEMO", memo);
			boData.set("SCR", scr);
			return diyLookAndFeel;
		}else{
			return null;
		}
		
	}

	@Override
	public String getCustomeTableHeaderHtml(ProcessExecutionContext arg0,
			FormItemModel arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String orderByStatement(ProcessExecutionContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
