package com.actionsoft.apps.expense.event;
import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;

/**
 * 初始化差旅费报销子表
 * @author CHENDX
 */
public class IniSonDataCLFBX extends ExecuteListener implements ExecuteListenerInterface {
	
	public IniSonDataCLFBX(){
		super.setDescription("表单加载前，初始化差旅报销子表数据。");
		super.setVersion("1.0");
	}
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		String[] xmlist = { "", "住宿费", "餐费", "交通费", "其他"};
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		String uid=ctx.getUserContext().getUID();
		for (int i = 1; i < 5; i++) {
			if (!CheckRow(processInstId, i)) {
				BO formData=new BO(); 
				formData.set("XM", xmlist[i]);
				formData.set("ROWNO", i);
				try {
					SDK.getBOAPI().create("BO_ACT_CLFBXD_APPLY_S", formData, processInstId, uid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public Boolean CheckRow(String processInstId, int rowNo) {
		try {
			String sql="select ID from "+GlobleParams.BO_ACT_CLFBXD_APPLY_S_TABLE+" where bindid='"
					+ processInstId + "' and ROWNO=" + rowNo
					+ " order by ROWNO";
			int ID = DBSql.getInt(
					"select ID from "+GlobleParams.BO_ACT_CLFBXD_APPLY_S_TABLE+" where bindid='"
							+ processInstId + "' and ROWNO=" + rowNo
							+ " order by ROWNO", "ID");
			if (ID > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

}

