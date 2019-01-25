package com.actionsoft.apps.expense.event;

import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilNumber;

public class UpdateCLBXJe extends ExecuteListener{

	@Override
	public void execute(ProcessExecutionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		String bindid = arg0.getProcessInstance().getId();
		List<RowMap> list=DBSql.getMaps("select * from "+GlobleParams.BO_ACT_CLFBXD_APPLY_TABLE+" where BINDID=?", bindid);
		for(RowMap r:list){
			double PJJEHJ = Double.valueOf(r.get("PJJEHJ").toString());
			double BZJEHJ = Double.valueOf(r.get("BZJEHJ").toString());
			String zje = r.get("ZSFJE").toString();
			String cje = r.get("CFJE").toString();
			String jje = r.get("JTFJE").toString();
			String yje = r.get("YDFJE").toString();
			String bje = r.get("BGFJE").toString();
			String qje = r.get("QTFYJE").toString();
			double ZSFJE;
			double CFJE; 
			double JTFJE;
			double YDFJE;
			double BGFJE;
			double QTFYJE;
			if(!"".equals(zje)||zje!=null){
				ZSFJE = Double.valueOf(zje);
			}else{
				ZSFJE=0;
			}
			
			if(!"".equals(cje)||cje!=null){
				CFJE = Double.valueOf(cje);
			}else{
				CFJE=0;
			}
			if(!"".equals(jje)||jje!=null){
				JTFJE = Double.valueOf(jje);
			}else{
				JTFJE=0;
			}
			if(!"".equals(yje)||yje!=null){
				YDFJE = Double.valueOf(yje);
			}else{
				YDFJE=0;
			}
			if(!"".equals(bje)||bje!=null){
				BGFJE = Double.valueOf(bje);
			}else{
				BGFJE=0;
			}
			if(!"".equals(qje)||qje!=null){
				QTFYJE = Double.valueOf(qje);
			}else{
				QTFYJE=0;
			}

			DBSql.update("update BO_OA_CLFBXD_APPLY set BXJEDX='"
					+ UtilNumber.toRMB(PJJEHJ + BZJEHJ + ZSFJE + CFJE + JTFJE + YDFJE + BGFJE +QTFYJE)
					+ "',XMJEHEJ='"+(ZSFJE + CFJE + JTFJE + YDFJE + BGFJE +QTFYJE)+"',BXJE='"+(PJJEHJ + BZJEHJ + ZSFJE + CFJE + JTFJE + YDFJE + BGFJE +QTFYJE)+"' where bindid= " + bindid);
			
			Double bxje =PJJEHJ + BZJEHJ + ZSFJE + CFJE + JTFJE + YDFJE + BGFJE +QTFYJE;//总报销金额
			String yjlf = r.get("YJLF").toString();//预借旅费
			double szyjlf;
			double je;
			if(!"".equals(yjlf)||yjlf!=null){
				szyjlf = Double.valueOf(yjlf);
			}else{
				szyjlf=0;
			}
			
			je=bxje-szyjlf;//计算总报销金额和预借旅费之差
				
			if(je>=0){
				DBSql.update("update BO_OA_CLFBXD_APPLY set BLJE='"+je+"',THJE='0' where bindid= " + bindid);
			}else{
				DBSql.update("update BO_OA_CLFBXD_APPLY set THJE='"+(szyjlf-bxje)+"',BLJE='0' where bindid= " + bindid);
			}
			
		}
	}

}
