package com.actionsoft.apps.cfgl.util;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

public class GETmanagementID extends AbstExpression{

	public GETmanagementID(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		//取参数，数字代表第几个参数
        String boname = getParameter(expression, 1);
        String fieldname = getParameter(expression, 2);
        String bindid=getParameter(expression, 3);
        String userid = "";
        String depname=DBSql.getString("select "+fieldname+" from "+boname+" where bindid='"+bindid+"'");
        if (!depname.equals("")&&!depname.equals(null)) {
            String depid=DBSql.getString("select id from orgdepartment where departmentname='"+depname+"'");
            userid=DBSql.getString("select userid from orguser where departmentid='"+depid+"' and ismanager=1");
		}
		return userid;
	}

}
