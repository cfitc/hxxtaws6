package com.actionsoft.apps.finance.web;

import java.util.List;

import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

public class ReimbursementStepWeb {

	public String ReimbursementStep(UserContext me, String bindid){
		@SuppressWarnings("unused")
		List<TaskCommentModel>  comments1 = SDK.getProcessAPI().getCommentsById(bindid);
		
		return null;
	}
}
