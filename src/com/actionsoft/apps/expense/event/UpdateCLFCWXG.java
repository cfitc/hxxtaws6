package com.actionsoft.apps.expense.event;

import java.util.List;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

/**
 * 财务修改补助金额更新
 * com.actionsoft.apps.expense.event.UpdateCLFCWXG
 * @author CHENDX
 *
 */
public class UpdateCLFCWXG extends ExecuteListener{

	@SuppressWarnings("deprecation")
	@Override
	public void execute(ProcessExecutionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		String bindid = arg0.getProcessInstance().getId();
		RowMap map=DBSql.getMap("select * from BO_ACT_CLFBXD_APPLY where bindid=?", bindid);
		List<RowMap> list=DBSql.getMaps("select * from BO_ACT_CLFBXD_APPLY_S where bindid=?", bindid);
		String PJJEHJ =map.get("PJJEHJ").toString()==null?"0":map.get("PJJEHJ").toString();
		String XMJEHEJ =map.get("XMJEHEJ").toString()==null?"0":map.get("XMJEHEJ").toString();
		String YJLF =map.get("YJLF").toString()==null?"0":map.get("YJLF").toString();//预借旅费
		double jehj = Double.valueOf(PJJEHJ)+Double.valueOf(XMJEHEJ);//票据金额合计+项目金额合计
		double bzjehj = 0;
		if (list!=null) {
			for (RowMap m:list) {
				String ccje = m.get("CCJE").toString()==null?"":m.get("CCJE").toString();//补助金额
				double bzje = Double.valueOf(ccje);//补助金额
				bzjehj += bzje;
			}
		}
		double yjlf = Double.valueOf(YJLF);
		double bxje = jehj+bzjehj;//报销总金额
		double blje = bxje-yjlf;//补领金额
		if(blje >= 0 ){
			DBSql.update("update BO_OA_CLFBXD_APPLY set BZJEHJ='"+bzjehj+"',BXJE='"+bxje+"',BLJE='"+blje+"',THJE='0' where bindid="+bindid);
		}else{
			DBSql.update("update BO_OA_CLFBXD_APPLY set BZJEHJ='"+bzjehj+"',BXJE='"+bxje+"',THJE='"+(yjlf-bxje)+"',BLJE='0' where bindid="+bindid);
		}
	}
	}

