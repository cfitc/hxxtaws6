package com.actionsoft.apps.gyxmgl.event;



import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;


import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
/**
 * 
 * @author h
 * com.actionsoft.apps.gyxmgl.event.BizForKhxxZJLX
 */
public class BizForKhxxZJLX  extends  InterruptListener implements InterruptListenerInterface {

	public BizForKhxxZJLX(){
		super.setDescription("客户信息保存前事件，判断客户名称是否重复，股东信息子表是否必填");
		super.setVersion("1.0");
	}
	@Override
	public boolean execute(ProcessExecutionContext ctx) throws Exception {
    	// 获取流程实例ID
		String ProcessId = ctx.getProcessInstance().getId();
		String KHMC=null;
		String KHFL=null;
		String KHLX=null;
		String ZJHM=null;
		boolean RTN = true;
		BO formData = (BO) ctx.getParameter(ListenerConst.FORM_EVENT_PARAM_FORMDATA);
		if(formData!=null){
			 KHMC =(String)formData.get("KHMC")==null?"":(String)formData.get("KHMC");
			 KHFL =(String)formData.get("KHFL")==null?"":(String)formData.get("KHFL");
			 KHLX =(String)formData.get("KHLX")==null?"":(String)formData.get("KHLX");
			 ZJHM =(String)formData.get("ZJHM")==null?"":(String)formData.get("ZJHM");
		
			 //判断客户名称是否存在

			int kh_count = DBSql.getInt("select count(*) num from BO_ACT_PM_KHXXB a where a.khmc ='"+KHMC+"' and a.bindid != '"+ProcessId+"'", "num");
			
			if(kh_count>0){
				RTN = false;
				throw new BPMNError("011",  "该客户名称已存在，请重新填写！");
			}
			//判断证件号码是否存在；
			int zj_count = DBSql.getInt("select count(*) num from BO_ACT_PM_KHXXB a where a.zjhm ='"+ZJHM+"' and a.bindid != '"+ProcessId+"'", "num");
			
			if(zj_count>0){
				RTN = false;
				throw new BPMNError("012",  "该证件号码已存在，请重新填写！");
			}
		    //验证是否是交易对手及机构客户，以及股东交易信息表不能为空
			if(KHLX.equals("1-机构")&&KHFL.contains("交易对手")){
				int khgd_count = DBSql.getInt("select count(*) cnt from BO_ACT_PM_KHGDXX where bindid='"+ProcessId+"'","cnt");
				if(khgd_count==0){
					RTN = false;
					throw new BPMNError("010",  "客户股东信息不能为空!");	
				}
			}
		
		}
		return RTN;
	}
}