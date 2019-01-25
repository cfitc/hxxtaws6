package com.actionsoft.apps.gyxmgl.util;


import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.exception.AWSAPIException;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;

public class GyPublicUtil {
	private static GyPublicUtil GyPublicUtil_api = null;
	
	private GyPublicUtil() {}
	
	public static GyPublicUtil getInstance() {
		if(GyPublicUtil_api == null) {
			GyPublicUtil_api = new GyPublicUtil();
		}
		return GyPublicUtil_api;
	}
	/**
	 * (节点事件)流程的第1节点办理后或者暂存后(项目储备)
	 * @param processDefId
	 * @param boName
	 * @param processInstId
	 * @param uc
	 * @param processType 1:流程数据;0:维护数据(仅存储)
	 * @return
	 */
	public boolean getPMStartUtil(String processDefId,String boName, String processInstId, UserContext uc, int processType){
		BO xmData = SDK.getBOAPI().query(boName).detailByBindId(processInstId);
		if(xmData!=null){
			//项目ID
			String xmid = xmData.getString("XMID");
			//项目阶段ID
			String xmjdid = xmData.getString("XMJDID") == null ? DBSql.getString("select XMJDID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid+ "'", "XMJDID"):xmData.getString("XMJDID").toString();//项目阶段ID
			//项目状态
			String xmzt = xmData.getString("XMZT")==null?DBSql.getString("select XMZT from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid+ "'", "XMZT"):xmData.getString("XMZT").toString();//项目状态
			//流程组名称
			String wfzmc = SDK.getRepositoryAPI().getProcessDefinition(processDefId).getProcessGroupName();
			//流程开始时间
			String wfbegintime = UtilDate.datetimeFormat24(SDK.getProcessAPI().getInstanceById(processInstId).getStartTime());
			//流程结束时间
			String wfendtime = UtilDate.datetimeFormat24(SDK.getProcessAPI().getInstanceById(processInstId).getEndTime());
			//流程标题
			String wftitle = SDK.getProcessAPI().getInstanceById(processInstId).getTitle();
			//发起人
			String wfstartuser = xmData.getString("FQR").toString();//发起人
			//发起部门
			String wfstartdept = xmData.getString("FQBM").toString();//发起部门
			//是否完毕
			String iswfend = processType == 0?"1":"0";//是否完毕   0:否;1:是;
			//BO对象
			BO recordData = new BO();
			recordData.set("XMID", xmid);
			recordData.set("XMJDID", xmjdid);
			recordData.set("XMZT",xmzt);
			recordData.set("LCID", processDefId);
			recordData.set("LCBINDID", processInstId);
			recordData.set("LCZ", wfzmc);
			recordData.set("KSSJ", wfbegintime);
			recordData.set("JSSJ", wfendtime==null?wfbegintime:wfendtime);
			recordData.set("LCBT", wftitle);
			recordData.set("FQR", wfstartuser);
			recordData.set("FQBM", wfstartdept);
			recordData.set("SFWB", iswfend);
			int tjxx_count = DBSql.getInt("select count(*) as c from " + GlobleParams.BO_ACT_PM_WF_TJXX_TABLE + " where xmid='" + xmid + "' and lcid='" + processDefId + "' and xmjdid='" + xmjdid + "' and lcbindid='" + processInstId + "'", "c");
			if(tjxx_count==0){
				//创建一个仅存储的数据维护实例，并初始化BO数据  03c4bb527ec29cab68ed90ead678db5b:项目流程统计信息UUID
				try {
					ProcessInstance inst= SDK.getProcessAPI().createBOProcessInstance("obj_4ee8f8ce4ab84a3784fd2b336f610ef9", "admin","项目统计");
					SDK.getBOAPI().create(GlobleParams.BO_ACT_PM_WF_TJXX_TABLE, recordData, inst,uc);
					
					
					//SDK.getBOAPI().createDataBO(GlobleParams.BO_ACT_PM_WF_TJXX_TABLE, recordData, uc);
				} catch (AWSAPIException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
	/**
	 * (流程事件)流程删除后处理业务
	 * @param processDefId
	 * @param processInstId
	 * @param boName
	 * @return
	 */
	public boolean deleteTjxxdata(String processDefId, String processInstId,String boName){
		BO xmData = SDK.getBOAPI().query(boName).detailByBindId(processInstId);
		if(xmData!=null){
			String xmid = xmData.getString("XMID");
			if(!"".equals(xmid)){
				try {
					String tjxxId = DBSql.getString("select ID from " + GlobleParams.BO_ACT_PM_WF_TJXX_TABLE + " where XMID='" + xmid + "' and LCID='" + processDefId + "' and LCBINDID='" + processInstId + "'", "ID");
					SDK.getBOAPI().remove(GlobleParams.BO_ACT_PM_WF_TJXX_TABLE, tjxxId);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * (流程事件)流程结束后处理业务
	 * @param processDefId
	 * @param processInstId
	 * @param boName
	 * @return
	 */
	public boolean getPMEndUtil(String processDefId, String processInstId,String boName){
		BO xmData = SDK.getBOAPI().query(boName).detailByBindId(processInstId);
		if(xmData!=null){
			//项目ID
			String xmid = xmData.getString("XMID");
			//项目状态
			String xmzt = xmData.getString("XMZT")==null?DBSql.getString("select XMZT from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid=" + xmid, "XMZT"):xmData.getString("XMZT").toString();//项目状态
			//流程结束时间
			String wfendtime = UtilDate.datetimeFormat24(SDK.getProcessAPI().getInstanceById(processInstId).getEndTime());
			//是否完毕
			String iswfend = "1";//是否完毕   0:否;1:是;
			//BO对象
			BO newData = new BO();
			newData.set("XMZT",xmzt);
			newData.set("ID",  xmData.getString("ID"));
			newData.set("JSSJ", wfendtime);
			newData.set("SFWB", iswfend);
			try {
				String tjxxId = DBSql.getString("select ID from " + GlobleParams.BO_ACT_PM_WF_TJXX_TABLE + " where XMID='" + xmid + "' and LCID='" + processDefId + "'", "ID");
				/**
				 *修改仅存储维护数据,由于需修改结束时间(JSSJ),updateBOData方法中Hatable对于日期格式为yyyy-MM-dd,这样时间不够精确。
				 *解决方案:1.修改存储模型中数据类型改为文本;2.修改平台BOInstanceAPI的updateBOData方法对于日期格式支持yyyy-MM-dd HH:mm:ss。
				 *目前用解决方案一,JSSJ日后要再做String转换Date。
				 */
				newData.set("ID", tjxxId);
				SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_WF_TJXX_TABLE, newData);
			} catch (Exception e) {
			}
		}
		return true;
	}
}
