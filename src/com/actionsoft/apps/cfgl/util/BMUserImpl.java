package com.actionsoft.apps.cfgl.util;

import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.bpmn.engine.performer.HumanPerformerInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;


public class BMUserImpl implements HumanPerformerInterface{
	@Override
	public String getPotentialOwner(UserContext arg0, ProcessInstance arg1,
			TaskInstance arg2, UserTaskModel arg3, Map<String, Object> arg4) {
		String userid="";
		try {
			//获得流程实例ID
//			String bindId = arg1.getId();
			//得到部门ID
			String depid=arg0.getDepartmentModel().getId();
			userid=DBSql.getString("select org.userid from orguser org " 
				       + " join  orgdepartment dept on org.departmentid  = dept.id" 
				       + " where dept.departmentid ='"+depid+"' and org.ismanager='1' "
				       + " union "
				       + " select b.userid from orgusermap a join orguser b "
				       + " on a.userid = b.userid and a.ismanager='1' "
				       + " join  orgdepartment dept on a.departmentid  = dept.id and dept.departmentid ='"+depid+"'");
        }catch (Exception e){
        	e.printStackTrace(System.err);
        }
		return userid;
	}
	@Override
	public String getHumanPerformer(UserContext arg0, ProcessInstance arg1,
			TaskInstance arg2, UserTaskModel arg3, Map<String, Object> arg4) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getHumanPerformerByHook(UserContext arg0,
			ProcessInstance arg1, TaskInstance arg2, UserTaskModel arg3,
			Map<String, Object> arg4) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPage(UserContext arg0, boolean arg1, ProcessInstance arg2,
			TaskInstance arg3, UserTaskModel arg4, Map<String, Object> arg5) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getSetting(UserContext arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
