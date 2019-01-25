package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.GyPublicUtil;
import com.actionsoft.apps.gyxmgl.util.ObjectCopy;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * FORM_BEFORE_LOAD 表单构建前触发
 * 
 * @preserve 项目事务看板的公共加载类 com.actionsoft.apps.gyxmgl.event.BizBeforeTableForPMID
 */
public class BizBeforeTableForZJZYSQGY extends ExecuteListener {

	public BizBeforeTableForZJZYSQGY() {
		super.setDescription("(公共)项目ID表单加载前事件");
		super.setVersion("V1.0");
	}

	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		// 获取流程定义ID
		String processDefID = ctx.getProcessDef().id;
		// 当前的BO表名，从流程变量中获取
		String boName = SDK.getRepositoryAPI().getProcessDefinition(processDefID).getProcessVars().get("boName").getDefaultValue();
		// 单据类型，从流程变量中获取
		String djlx = SDK.getRepositoryAPI().getProcessDefinition(processDefID).getProcessVars().get("djlx").getDefaultValue();
		try {
			String xmid = SDK.getProcessAPI().getVariable(processInstId, "xmid").toString();
			if (xmid != "" && !"".equals(xmid) && xmid != null) {
				// 根据项目ID获取项目数据
				BO xmxxData = SDK.getBOAPI().getByKeyField(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, "xmid", xmid);
				// 例外字段数组，项目部门不进行COPY
				String exception = "FQBM";
				// 把项目信息表中的数据COPY至业务表中
				BO newData = ObjectCopy.ObjectCopyMtd(xmxxData, boName,exception);
				// ---单据信息
				// 单据编号
				UserContext uc = ctx.getUserContext();
				String djbh = djlx+ SDK.getRuleAPI().executeAtScript("@year@month")+ "-"+ SDK.getRuleAPI().executeAtScript("@sequence(" + djlx + ")");
				String fqrq = SDK.getRuleAPI().executeAtScript("@date");
				String fqbm = SDK.getRuleAPI().executeAtScript("@departmentName", uc);
				String fqr = SDK.getRuleAPI().executeAtScript("@userName", uc);
				String bgdh = SDK.getRuleAPI().executeAtScript("@userTel", uc);
				String jm = "内部公开";
				String jjcd = "普通";
				String bgqx = "永久";
				String bmqx = "永久";
				String sfyy = "否";
				newData.set("DJBH", djbh);
				newData.set("FQRQ", fqrq);
				newData.set("FQBM", fqbm);
				newData.set("FQR", fqr);
				newData.set("BGDH", bgdh);
				newData.set("JM", jm);
				newData.set("JJCD", jjcd);
				newData.set("BGGX", bgqx);
				newData.set("BMGX", bmqx);
				newData.set("SFYY", sfyy);
				BO recordDataForSJ = new BO();
				recordDataForSJ = this.getSJInfo(newData);
				int count = DBSql.getInt("select count(*) as c from " + boName+ " where bindid='" + processInstId + "'", "c");
				if (count == 0) {
					// 业务表存储数据
					SDK.getBOAPI().create(boName, recordDataForSJ,ctx.getProcessInstance(), ctx.getUserContext());

					// 向项目流程统计信息录入数据
					GyPublicUtil.getInstance().getPMStartUtil(processDefID,boName, processInstId, ctx.getUserContext(), 1);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 随机抽取中介机构方法
	 * 
	 * @param recordDataForVC
	 * @return
	 */
	public BO getSJInfo(BO recordDataForVC) {
		Connection conn = DBSql.open();
		Statement stem = null;
		ResultSet rs = null;
		String sql = "select id, zymc, zybh, LXR, Lxfs, lxryx, sfqy from (select a.*, dbms_random.random num from hxxtxt.BO_BCF_TRU_MD_XMZJXXB_M a "
				  + " where a.Zyid not in (select temp1 || ' ,' || temp4 from (select b.temp1, b.temp4 from hxxt.BO_ACT_PM_ZJZYSPB b where b.temp10 is null"
				  + " order by b.CREATEDATE desc)where rownum <= 2) order by num)"
				  + " where 1 = 1 and rownum <= 2 and (zylx = '正式') and sfqy = '是' and sfzy = '是' order by zybh";
		try {
			stem = conn.createStatement();
			rs = stem.executeQuery(sql);
			while (rs.next()) {
				if (rs.getRow() == 1) {
					recordDataForVC.set("TEMP1",rs.getString("id") == null ? "" : rs.getString("id"));
					recordDataForVC.set("TEMP2",rs.getString("zybh") == null ? "" : rs.getString("zybh"));
					recordDataForVC.set("TEMP3",rs.getString("zymc") == null ? "" : rs.getString("zymc"));
					recordDataForVC.set("TEMP12",rs.getString("LXR") == null ? "" : rs.getString("LXR"));
					recordDataForVC.set("TEMP13",rs.getString("Lxfs") == null ? "" : rs.getString("Lxfs"));

				} else if (rs.getRow() == 2) {
					recordDataForVC.set("TEMP4",rs.getString("id") == null ? "" : rs.getString("id"));
					recordDataForVC.set("TEMP5",rs.getString("zybh") == null ? "" : rs.getString("zybh"));
					recordDataForVC.set("TEMP6",rs.getString("zymc") == null ? "" : rs.getString("zymc"));
					recordDataForVC.set("TEMP14",rs.getString("LXR") == null ? "" : rs.getString("LXR"));
					recordDataForVC.set("TEMP15",rs.getString("Lxfs") == null ? "" : rs.getString("Lxfs"));
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBSql.close(conn, stem, rs);
		}
		recordDataForVC.set("TEMP11", "固有项目");
		return recordDataForVC;
	}

}
