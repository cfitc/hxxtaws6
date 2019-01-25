package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.FormatSqlUtil;
import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatSQLEventInterface;
import com.actionsoft.bpms.dw.exec.component.DataView;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;

public class FormatSqlForPmTask implements DataWindowFormatSQLEventInterface{
	public String formatSQL(UserContext me, DataView view, String sql) {
		String formatSql = null;
		String uid = me.getUID();
		String username = DBSql.getString("select username from orguser where userid='"+uid+"'", "username");
		String departmentid = DBSql.getString("select departmentid from orguser where userid='"+uid+"'", "departmentid");
		String departmentname = DBSql.getString("select departmentname from orgdepartment where id='"+departmentid+"'", "departmentname");

		FormatSqlUtil fsu = new FormatSqlUtil();
		boolean bl = fsu.checkUserInfo(uid);
		if (bl){
			   formatSql = sql;
		    } else {
			   formatSql = sql.replace("1=1", " (xmid in (select gxr.ssxmid from "+GlobleParams.BO_ACT_PM_GYXMGXRB_TABLE+" gxr join orguser ouser on gxr.gxrid = ouser.id and ouser.userid='"+uid+"' union select gxr.xmid as ssxmid from "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" gxr where (createuser='"+uid+"' or xmjl='"+uid+"<"+username+">' or xmzcy like '%"+username+"%')))");
			   System.out.println(formatSql);
		    }
		return formatSql;
	}
		
}
