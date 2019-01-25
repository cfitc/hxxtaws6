package com.actionsoft.apps.gyxmgl.event;

import net.sf.json.JSONObject;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.ObjectCopy;
import com.actionsoft.apps.gyxmgl.util.WorkFlowUtil;
import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.cc.SOAPAPI;
//import com.ibm.db2.jcc.am.jd;

/**
 * 项目储备数据同步至项目信息表中
 * @author Administrator
 *
 */
public class XmcbToXmxxEvent extends ExecuteListener {
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// TODO Auto-generated method stub
		String processID = ctx.getProcessInstance().getId();
		String logUid = ctx.getUserContext().getUserModel().getUID();
		String createUserName = ctx.getUserContext().getUserModel().getUserName();
		BO XmcbData = SDK.getBOAPI().query(GlobleParams.BO_ACT_PM_GYXMCB_TABLE).detailByBindId(processID);
		String bindId ="";
		if (XmcbData != null) {
			String XmcbID = XmcbData.get("ID").toString();// 项目ID
			String XmcbXmjl = XmcbData.get("XMJL").toString();// 项目经理
			String XmcbXmzcy = XmcbData.get("XMZCY").toString();// 项目组成员
			String Xmmc = XmcbData.get("XMMC").toString();
			String MD_XMZT = DBSql.getString("select XMZT from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + XmcbID + "'", "XMZT");
			bindId= DBSql.getString("select BINDID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + XmcbID + "'", "BINDID");
			String MD_XMXX_BOID = DBSql.getString("select ID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + XmcbID + "'", "ID");
			// 项目储备表中状态为空或者主数据项目信息表中项目状态为项目储备的情况，才同步到主数据
			if ("".equals(XmcbData.get("XMZT").toString()) || MD_XMZT.equals("项目储备")) {
				//获取固有项目信息表的BO对象
				BO GyxmxxBo = ObjectCopy.ObjectCopyMtd(XmcbData, GlobleParams.BO_ACT_PM_GYXMXXB_TABLE);
				GyxmxxBo.set("XMID", XmcbID);
				GyxmxxBo.set("XMZT", GlobleParams.gyxmcbStatus);
				GyxmxxBo.setId(MD_XMXX_BOID);
				String strCre = logUid + "<" + createUserName + ">";
				// 处理下项目经理是否被选到项目组成员了,如果选了就去掉。
				System.out.println("XmcbXmzcy:" + XmcbXmzcy + ",XmcbXmjl="
						+ XmcbXmjl);
				if (XmcbXmzcy.contains(XmcbXmjl)) {
					XmcbXmzcy = XmcbXmzcy.replace(XmcbXmjl + " ", "");
					XmcbXmzcy = XmcbXmzcy.replace(" " + XmcbXmjl, "");
				}
				// 处理下项目组成员,起草者,填写项目组成员时,未将自己选入项目组成员时,程序自动将起草者加到项目组成员中.
				if (!strCre.equals(XmcbXmjl) && !XmcbXmzcy.contains(strCre)) {
					XmcbXmzcy = XmcbXmzcy + " " + strCre;
				}
				GyxmxxBo.set("XMZCY", XmcbXmzcy);// 项目组成员
				//处理原交易对手信息ID和原阶段ID
				String YJYDSID=DBSql.getString("select KHXXID from BO_ACT_PM_KHXXB where id='"+XmcbData.get("JYDSID").toString()+"'");
				String YJDID=DBSql.getString("select YJDID from BO_ACT_PM_XMJDB where id='"+XmcbData.get("XMJDID").toString()+"'");
				GyxmxxBo.set("YJYDSID", YJYDSID);
				GyxmxxBo.set("YJDID", YJDID);
				if ("".equals(MD_XMZT)) {
					// 创建一个仅存储的数据维护实例，并初始化BO数据
					bindId = WorkFlowUtil.CreateJCCData(GlobleParams.GYXMXX_UUID,logUid, Xmmc, GlobleParams.BO_ACT_PM_GYXMXXB_TABLE,GyxmxxBo);
				} else {
					// 更新成功返回1,未成功返回0
					SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, GyxmxxBo);
				}
				// 给项目储备中的状态(项目储备),项目ID(此行数据ID的值),项目阶段ID(根据目前阶段查出阶段ID)赋予值
				DBSql.update("update "
						+ GlobleParams.BO_ACT_PM_GYXMCB_TABLE
						+ " set XMZT='" + GlobleParams.gyxmcbStatus
						+ "',XMID=ID,XMZCY='" + XmcbXmzcy
						+ "' where bindid='" + processID+"'");
				/**
				 * 同步项目数据到5平台
				 */
				ProjectInfoSynchroClient.xmxxToPmsFive(bindId);
			}
		}
	}
	
}
