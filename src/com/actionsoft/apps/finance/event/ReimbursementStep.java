package com.actionsoft.apps.finance.event;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class ReimbursementStep  extends ExecuteListener{


	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		//param.getParameterOfString(ListenerConst.);
		String bindid = param.getProcessInstance().getParentProcessInstId();
		String lcid = param.getProcessInstance().getProcessDefId();
		List<TaskCommentModel>  comments1 = SDK.getProcessAPI().getCommentsById(param.getProcessInstance().getId());
		for (int i = 0; i < comments1.size(); i++) {
			String taskid = comments1.get(i).getTaskInstId();
			String CreateUser = comments1.get(i).getCreateUser();
			Timestamp Createdate =comments1.get(i). getCreateDate();
			System.out.println(taskid+"          "+CreateUser+"              "+Createdate);
		}
		String CreateUser2 = comments1.get(0).getTaskInstId();
		// TODO Auto-generated method stub
		 //参数获取
        //注意：除特殊说明外，下列参数仅在该事件中场景有效
        //记录ID
        String boId = param.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BOID);
        //表单ID
        String formId = param.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_FORMID);
        //BO表名
        //获取表单所有的标签
        Map<String, Object> macroLibraries = param.getParameterOfMap(ListenerConst.FORM_EVENT_PARAM_TAGS);

        String fieldHtml = (String) macroLibraries.get("FIELDNAME");//一个字段名
        if (UtilString.isEmpty(fieldHtml)) {
            //说明没有该标签//
        } else {
            //1.可将HTML片段根据需求处理放回
            //2.可将该标签改为一个图片
            fieldHtml = "<img id='FIELDNAME' name='FIELDNAME' src=''/>";
            macroLibraries.put("FIELDNAME", fieldHtml);
        }
	}
}
