package com.actionsoft.apps.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.EAITaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;

public class TaskJob implements IJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String ccid="e0ee6447-f01b-4079-b3a3-89150eae6f64";//cc连接
		Connection conn = SDK.getCCAPI().getDBAPI(ccid).open();
		String sql="select * from wf_task where status=1 order by begintime desc";
		List<RowMap> maps = DBSql.getMaps(conn, sql, new HashMap());
		int count=1;
		for (RowMap rowMap : maps) {
			System.err.println(">>>>>>>>>count:"+count++);
			String taskId = rowMap.getString("ID");
		    String createUid = rowMap.getString("owner");
		    String beginTime = rowMap.getString("BEGINTIME");
		    //String createUid = "admin";
		    String targetUid = rowMap.getString("target");
		    Map map=new HashMap();
		    map.put("USERID", targetUid);
		    String flag=DBSql.getString(conn, "select disenable from orguser where USERID=:USERID", "disenable", map);
		    if(!"0".equals(flag) || flag==null){
		    	System.err.println(">>>>>targetUid"+targetUid);
		    	continue;
		    }
		    //String targetUid = "admin";
		    String title = rowMap.getString("title");
		    String priority = rowMap.getString("priority");

		    String processInstId = rowMap.getString("BIND_ID");
		    //String processInstName = (String)params.get("processInstName");
		    String processGroupName = rowMap.getString("WF_STYLE");
		    String ownerDepartmentId = rowMap.getString("OWNER_DPT_ID");
		    int state = rowMap.getInt("STATUS");
		    
		    JSONObject json = new JSONObject();
		    json.put("processInstId", processInstId);
		    json.put("processInstName", title);
		    json.put("processGroupName", processGroupName);
		    json.put("ownerDepartmentId",ownerDepartmentId);

		    json.put("taskId", taskId);
		    json.put("state", state);
		    //SDK.getTaskAPI().createEAITaskInstance("AWS5.2-Worklist", "0001", "admin", "admin", "这是百度的首页", "http://www.baidu.com", 1);
		    System.err.println(">>>>>>>>>json:"+json.toString());
			EAITaskInstance eaiTaskInstance = SDK.getTaskAPI().createEAITaskInstance("AWS5.2-Worklist", taskId, createUid, targetUid, title, json.toString(), state);
			Connection conn1 = null;
		    conn1 = DBSql.open();
		    Map paraMap = new HashMap();
		    paraMap.put("state", state);
		    paraMap.put("ownerDepartmentId", ownerDepartmentId);
		    paraMap.put("id", eaiTaskInstance.getId());
		    paraMap.put("beginTime", beginTime);
		    try {
		      String sql1 = "update WFC_TASK set TASKSTATE=:state,OWNERDEPTID=:ownerDepartmentId,BEGINTIME=:beginTime,READSTATE=1 where ID=':id'";
		      //String sql = "update WFC_TASK set TASKSTATE="+state+",OWNERDEPTID=";
		      DBSql.update(conn1, sql1, paraMap);
		     // DBSql.update(sql1);
		    } catch (Exception localException) {
		    	localException.printStackTrace();
		    } finally {
		    	 try {
		    		 if(conn1!=null){
		    			 conn1.close();
		    		 }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		try {
			if(conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
