package com.actionsoft.apps.gyxmgl.plugins;

import java.util.List;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

public class DepartmentManagerExpressionImpl extends AbstExpression {
	
	/**
	 * 获取部门负责人的@命令(@departmentManager('@departmentName'))
	 * @param atContext
	 * @param expressionValue
	 */
	public DepartmentManagerExpressionImpl(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		// 第一个参数 部门 @
		String deptName = getParameter(expression, 1).trim();
		String UIDS="";
		try {
			List<RowMap> list=DBSql.getMaps("select * from orguser where ismanager=1 and  DEPARTMENTID in"
									+ "(select id from orgdepartment where departmentname=?)", deptName);
			for (RowMap rowMap : list) {
				String uid=rowMap.getString("USERID");
				UIDS+=uid+" ";
			}
//			String userName = DBSql
//					.getString(
//							"select username from orguser where ismanager=1 and  DEPARTMENTID in"
//									+ "(select id from orgdepartment where departmentname='"
//									+ deptName + "')", "USERNAME");
			return UIDS;
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

}
