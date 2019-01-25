package com.actionsoft.apps.gyxmgl.controller;


import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;

public class KhmsgJudgeController {

	@Mapping("com.actionsoft.apps.gyxmgl.KhmsgJudge")
	public String khmsg(String KHLX,String KHFL,String KHMC,String bindid){

		int kh_count = DBSql.getInt("select count(*) num from BO_ACT_PM_KHXXB a where a.khmc ='"+KHMC+"'", "num");
		
		if(kh_count>0){
			String result = "该客户名称已存在，请重新填写！";
			return result;
		}
		
		if(KHLX.equals("1-机构")&&KHFL.contains("交易对手")){
			int khgd_count = DBSql.getInt("select count(*) num2 from BO_ACT_PM_KHGDXX where bindid="+bindid, "num2");
			if(khgd_count==0){
				String result = "客户股东信息不能为空!";
				return result;
			}
		}
		return null;
	
	}
	
}
