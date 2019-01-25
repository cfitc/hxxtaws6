package com.actionsoft.apps.gyxmgl.util;


import java.util.List;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSDataAccessException;

/**
 * 初始化bindid工具类
 * @author Administrator
 *
 */
public class DataInitTool {
	public String DateInit() {
		String result = "success";
		try {
			String sql = "select * from BO_ACT_CSHZCB where isover =?";
			List<RowMap> list = DBSql.getMaps(sql, "否");
			if(list.size()<1){
				return result;
			}
			for(RowMap m : list){
				String zbmc = m.getString("ZBMC");
				String zbmc1 = m.getString("ZBMC1");
				String zbmc2 = m.getString("ZBMC2");
				String zbmc3 = m.getString("ZBMC3");
				String defiID = m.getString("GLBINDID");
				String zzglid = m.getString("GLID");
				//业务表主子关联ID
				String ywbID = "";
				//主表名称不为空，则根据关联流程定义ID获取
				StringBuffer sb = new StringBuffer();
				if(zbmc != "" && !"".equals(zbmc)){
					
					StringBuffer sqlBuf = new StringBuffer();
					sqlBuf.append("select id,bindid");
					if(zzglid != ""){
						sqlBuf.append(","+zzglid);
					}
					sqlBuf.append(" from "+zbmc+" where 1=?");
					System.out.println(sqlBuf.toString());
					List<RowMap> listyw = DBSql.getMaps(sqlBuf.toString(), "1");
					for(int i=0;i<listyw.size();i++){
						RowMap m2 = listyw.get(i);
						if(zzglid != ""){
							ywbID = m2.getString(zzglid);
						}
						String zbYwId = m2.getString("ID");
						String xBindid = WorkFlowUtil.GetBoProcessInstance(defiID, "admin", zbmc);
						//DBSql.update(" update "+zbmc+" set bindid='"+xBindid+"' where id='"+zbYwId+"'");
						sb.append(" update "+zbmc+" set bindid='"+xBindid+"' where id='"+zbYwId+"',");
						if(zbmc1 != ""){
							//DBSql.update(" update "+zbmc1+" set bindid = '"+xBindid+"' where "+zzglid+"='"+ywbID+"'");
							sb.append(" update "+zbmc1+" set bindid = '"+xBindid+"' where "+zzglid+"='"+ywbID+"',");
						}
						if(zbmc2 != ""){
							//DBSql.update(" update "+zbmc2+" set bindid = '"+xBindid+"' where "+zzglid+"='"+ywbID+"'");
							sb.append(" update "+zbmc2+" set bindid = '"+xBindid+"' where "+zzglid+"='"+ywbID+"',");
						}
						if(zbmc3 != ""){
							//DBSql.update(" update "+zbmc3+" set bindid = '"+xBindid+"' where "+zzglid+"='"+ywbID+"'");
							sb.append(" update "+zbmc3+" set bindid = '"+xBindid+"' where "+zzglid+"='"+ywbID+"',");
						}
						if(i%500== 0){
							String[] strs = sb.toString().split(",");
							DBSql.batch(strs);
							System.out.println("--------已更新表"+zbmc+"数据条数："+i);
							sb = new StringBuffer();
						}
					}
					//DBSql.update("update BO_ACT_CSHZCB set ISOVER = '是' where zbmc='"+zbmc+"'");
					sb.append("update BO_ACT_CSHZCB set ISOVER = '是' where zbmc='"+zbmc+"'");
					String[] strs = sb.toString().split(",");
					DBSql.batch(strs);
				}
			}
		} catch (AWSDataAccessException e) {
			result = "error";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
