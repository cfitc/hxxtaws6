package com.actionsoft.apps.gyxmgl.service;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.util.DBSql;

public class pmTaskDoCheckUtil {
	public boolean pmTaskDoCheck(String xmId, String lcId, String jdId) {
		boolean bl = false;
        String sqlForNum = " select ywc.yxfqcs from "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" ywc  where ywc.lcid='"+lcId+"' and ywc.jdid='"+jdId+"'";
        String numForFull = DBSql.getString(sqlForNum,"yxfqcs"); 
        if(numForFull == ""){
        	numForFull = "-1";
        }
        int fullNum = Integer.parseInt(numForFull);
        if(fullNum==-1){
        	bl=true;
        }
        
        String sqlForNow = " select count(*) numNow from "+GlobleParams.BO_ACT_PM_WF_TJXX_TABLE+" ywc  where ywc.lcid='"+lcId+"' and ywc.xmjdid='"+jdId+"'  and ywc.xmid='"+xmId+"'";
        int nowNum = DBSql.getInt(sqlForNow, "numNow");
        if(fullNum>nowNum){
        	bl = true;
        }
		return bl;
	}
}
