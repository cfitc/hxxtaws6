package com.actionsoft.apps.expense.event;

import java.text.DecimalFormat;
import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilNumber;

public class UpdateBXPDZBV extends ExecuteListener implements ExecuteListenerInterface{
	//单据保存后，计算子表价款
	@Override
	public void execute(ProcessExecutionContext arg0) throws Exception {
		String BINDID=arg0.getProcessInstance().getId();//流程实例ID
		Double HJ=0.0;
		//保存后计算其他费用申请字表
		List<RowMap> list=DBSql.getMaps("select * from "+GlobleParams.BO_ACT_BXPD_APPLY_S_TABLE+" where BINDID=?", BINDID);
		for (RowMap m : list) {
			String   jshjje =  m.get("JE")==null?"0":m.get("JE").toString();
			String   se =  m.get("SE")==null?"0":m.get("SE").toString();
			String idString = m.get("ID").toString();
			Double  btjks =  Double.parseDouble(jshjje)-Double.parseDouble(se);
			DecimalFormat df = new DecimalFormat("#.00");
			Double  btjk =Double.parseDouble(df.format(btjks));
			//计算每条价款
			DBSql.update("update "+GlobleParams.BO_ACT_BXPD_APPLY_S_TABLE+" set JK="+btjk+" where id ='"+idString+"'");
			HJ=HJ+Double.parseDouble(jshjje);
			System.out.println(HJ);
		}
		String DXHJ=UtilNumber.toRMB(HJ);
		DBSql.update("update BO_ACT_BXPD_APPLY set JEHJXX="+HJ+",BXJEDX='"+DXHJ+"' where bindid ='"+BINDID+"'");
		return;
		
	}

}
