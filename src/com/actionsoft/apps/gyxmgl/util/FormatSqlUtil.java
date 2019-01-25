package com.actionsoft.apps.gyxmgl.util;

import com.actionsoft.bpms.util.DBSql;

public class FormatSqlUtil {
	public boolean checkUserInfo(String uid) {
		boolean bl = false;
		String sql = "select userid   from orguser a  where ext2='是' and closed='0' and userid='"+uid+"'";
		String getStrValue = DBSql.getString(sql, "userid");
		if(getStrValue.equals(uid)|| uid.equals("admin")){
			bl = true;
		}
		return bl;
	}
	
	public boolean checkUserInfoForSFk(String uid) {
		boolean bl = false;
		String sql = "select userid   from orguser a  where ext4='是' and closed='0' and userid='"+uid+"'";
		String getStrValue = DBSql.getString(sql, "userid");
		if(getStrValue.equals(uid)|| uid.equals("admin")){
			bl = true;
		}
		return bl;
	}
}
