package com.actionsoft.apps.expense.event;

import java.util.List;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilNumber;

public class UpdateCLFBXVT extends ExecuteListener{

	@Override
	public void execute(ProcessExecutionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		String bindid = arg0.getProcessInstance().getId();
		//保存后计算其他费用申请字表
		String  qtfy = "BO_ACT_CLFBXD_APPLY_S2";
		List<RowMap> list=DBSql.getMaps("select * from "+qtfy+" where BINDID=?", bindid);
		Double  zhusujk=0.00; 
		Double  canfeijk=0.00; 
		Double  jiaotongjk=0.00; 
		Double  qitajk=0.00; 
		Double  zhusushuie=0.00; 
		Double  canfeishuie=0.00; 
		Double  jiaotongshuie=0.00; 
		Double  qitashuie=0.00; 
		Double  ZSJE=0.00;
		Double  CFJE=0.00;
		Double  JTJE=0.00;
		Double  QTJE=0.00;
		int  ZSZS = 0;
		int  CFZS = 0;
		int  JTZS = 0;
		int  QTZS = 0;
		Double SEHJ  =0.00;
		
			if(list!=null){
				for (RowMap m :list) {
					String   xm =  m.get("XM")==null?"":m.get("XM").toString();
					String   fpzl =  m.get("FPZL")==null?"":m.get("FPZL").toString();
					String   jshjje =  m.get("JSHJJE")==null?"0":m.get("JSHJJE").toString();
					String   jk =  m.get("JK")==null?"0":m.get("JK").toString();
					String   se =  m.get("SE")==null?"0":m.get("SE").toString();
					String   djzs =  m.get("DJZS")==null?"0":m.get("DJZS").toString();
					String idString = m.get("ID").toString();
//					int subFormId = Integer.parseInt(idString);
					Double  btjks =  Double.parseDouble(jshjje)-Double.parseDouble(se);
					Double  btjk =UtilNumber.fixPoint(btjks, 2);
					if(djzs.equals("")){
						djzs="0";
					}
					if (xm.equals("住宿费")){
						zhusujk+=btjk;
						zhusushuie+=Double.parseDouble(se);
						ZSJE+=Double.parseDouble(jshjje);
						ZSZS+=Integer.parseInt(djzs);
					}else if(xm.equals("餐费")){
						canfeijk+=btjk;
						canfeishuie+=Double.parseDouble(se);
						CFJE+=Double.parseDouble(jshjje);
						CFZS+=Integer.parseInt(djzs);
					}else if(xm.equals("交通费")){
						jiaotongjk+=btjk;
						jiaotongshuie+=Double.parseDouble(se);
						JTJE+=Double.parseDouble(jshjje);
						JTZS+=Integer.parseInt(djzs);
					}else if(xm.equals("其他")){
						qitajk+=btjk;
						qitashuie+=Double.parseDouble(se);
						QTJE+=Double.parseDouble(jshjje);
						QTZS+=Integer.parseInt(djzs);
					}
					Double  SEDOU =  Double.parseDouble(se);
					SEHJ +=  UtilNumber.fixPoint(SEDOU, 2);
					
					//计算每条价款
					DBSql.update(" update   "+qtfy+"  set  JK=  "+btjk+"  where id ='"+idString+"'  ");
				}
			}
			//计算每项费用总价
			  DBSql.update(" update   BO_ACT_CLFBXD_APPLY  set  ZSFJKHJ ="+zhusujk+"  , CFJKHJ  = "+canfeijk+", JTFJKHJ  ="+jiaotongjk+" , QTJKHJ = "+qitajk+" , " +
									"   ZSFSEHJ  =  "+zhusushuie+" , CFSEHJ ="+canfeishuie+" , JTFSEHJ ="+jiaotongshuie+" ,QTSEHJ =  "+qitashuie+"  , " +
									"   ZSFJE = "+ZSJE+"  , CFJE = "+CFJE+" , JTFJE = "+JTJE+" ,QTFYJE = "+QTJE+", ZSFDJZS = "+ZSZS+" , JTFDJZS = "+JTZS+" , CFDJZS = "+CFZS+"  ,  QTFYDJZS = "+QTZS+" , SEHJ = "+SEHJ+" WHERE bindid ='"+bindid+"'  ");
		
			
			List<RowMap> list2=DBSql.getMaps("select * from BO_ACT_CLFBXD_APPLY where bindid=?", bindid);
			if(list2.size()!=0){
				for (RowMap m : list2) {
					String zje = m.get("ZSFJE").toString();
					String cje = m.get("CFJE").toString();
					System.out.println("餐费金额"+cje);
					String jje = m.get("JTFJE").toString();
					String qje = m.get("QTFYJE").toString();
				List<RowMap> list3=DBSql.getMaps("select * from BO_ACT_CLFBXD_APPLY_S where bindid=?", bindid);
				double ZSFJE;
				double CFJE1; 
				double JTFJE;
				double QTFYJE;
				if(!"".equals(zje)||zje!=null){
					ZSFJE = Double.valueOf(zje);
				}else{
					ZSFJE=0;
				}
				
				if(!"".equals(cje)||cje!=null){
					CFJE1 = Double.valueOf(cje);
				}else{
					CFJE1=0;
				}
				if(!"".equals(jje)||jje!=null){
					JTFJE = Double.valueOf(jje);
				}else{
					JTFJE=0;
				}
				if(!"".equals(qje)||qje!=null){
					QTFYJE = Double.valueOf(qje);
				}else{
					QTFYJE=0;
				}
				Double wtx = CFJE1;//报销餐费
				Double jtf=JTFJE;  //报销交通费
				double bzjehj = 0  ;//补助金额
				double bzjehej =0;
				double pjjehj=0;
				if (list3 != null) {
					for (RowMap m2:list3) {
						String ccts = m2.get("CCTS").toString()==null?"":m2.get("CCTS").toString();//出差天数
						String ccje = m2.get("CCJE").toString()==null?"":m2.get("CCJE").toString();//补助金额
						String ppje = m2.get("PJJE").toString()==null?"":m2.get("PJJE").toString();//票据金额
						String idString = m2.get("ID").toString();
//						int subFormId = Integer.parseInt(idString);
						Double btje = Double.parseDouble(ccts) * 80;//交通补贴			
						pjjehj+=Double.parseDouble(ppje); //jtf为主表单中的交通费
						if(jtf<=0&&wtx>0){
							//如果没有报销交通费
							DBSql.update("update BO_ACT_CLFBXD_APPLY_S set CCJE="+btje+" where id='"+idString+"'");//未填写餐费及交通费，按照80/人天计算到补助金额上
							bzjehj += btje;
							bzjehej += btje;
						}
						if (wtx <= 0&&jtf>0) {
							//如果没有报销餐费
							btje = Double.parseDouble(ccts) * 100;//餐费补贴为每天100
							DBSql.update("update BO_ACT_CLFBXD_APPLY_S set CCJE="+btje+" where id='"+idString+"'");//未填写餐费及交通费，按照80/人天计算到补助金额上
							bzjehj += btje;
							bzjehej += btje;
						}
						if(jtf<=0&&wtx <= 0){
							//如果没有报销餐费 并且没有报销交通费
							btje = Double.parseDouble(ccts) * 180;//餐费补贴+交通补贴为每天180	
							DBSql.update("update BO_ACT_CLFBXD_APPLY_S set CCJE="+btje+" where id='"+idString+"'");//未填写餐费及交通费，按照80/人天计算到补助金额上
							bzjehj += btje;
							bzjehej += btje;
						}
						else if (wtx > 0&&jtf>0){
		                   //报销了餐费和交通费  不再有补助金
							btje = Double.parseDouble(ccts) * 0;//餐费补贴+交通补贴为0	
							DBSql.update("update BO_ACT_CLFBXD_APPLY_S set CCJE="+btje+" where id='"+idString+"'");//未填写餐费及交通费，按照80/人天计算到补助金额上
							bzjehj += btje;
							bzjehej += btje;	
		                  /* 
							if(wtx-btje >=0){
								DBSql.executeUpdate("update BO_ACT_CLFBXD_APPLY_S set CCJE=0 where id="+subFormId);//填写的餐费大于等于每天80的总额，则补助金额为0
							}else if(wtx-btje<0){
								bzjehj = (btje-wtx);
								DBSql.executeUpdate("update BO_ACT_CLFBXD_APPLY_S set CCJE="+bzjehj+" where id="+subFormId);//填写的餐费金额小于每天80的总额，则补助金额为它们的差额
								bzjehej += bzjehj;
							}*/
						}
					}
					double PJJEHJ = pjjehj+JTFJE;
					//更新报销金额大写；项目金额合计；票据金额合计；报销总金额；补助金额合计
					DBSql.update("update BO_ACT_CLFBXD_APPLY set BXJEDX='"
							+ UtilNumber.toRMB(PJJEHJ + bzjehej + ZSFJE + CFJE1 + JTFJE +QTFYJE)
							+ "',XMJEHEJ='"+(ZSFJE + CFJE1+QTFYJE)+"',BXJE='"+(PJJEHJ + bzjehej + ZSFJE + CFJE1 +QTFYJE)+"',BZJEHJ='"+bzjehej+"',PJJEHJ='"+PJJEHJ+"' where bindid= '" + bindid+"'");
					
					Double bxje =PJJEHJ + bzjehej + ZSFJE + CFJE1 +QTFYJE;//总报销金额
					String yjlf = m.get("YJLF").toString();//预借旅费
					double szyjlf;
					double je;
					if(!"".equals(yjlf)||yjlf!=null){
						szyjlf = Double.valueOf(yjlf);
					}else{
						szyjlf=0;
					}
					je=bxje-szyjlf;//计算总报销金额和预借旅费之差
					if(je>=0){
						DBSql.update("update BO_ACT_CLFBXD_APPLY set BLJE='"+je+"',THJE='0' where bindid= '" + bindid +"'");
					}else{
						DBSql.update("update BO_ACT_CLFBXD_APPLY set THJE='"+(szyjlf-bxje)+"',BLJE='0' where bindid= '" + bindid +"'");
					}
				}
				
			}
	}

}
}