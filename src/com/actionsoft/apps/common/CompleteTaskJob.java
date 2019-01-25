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

public class CompleteTaskJob implements IJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String ccid="e0ee6447-f01b-4079-b3a3-89150eae6f64";//cc连接
		Connection conn = SDK.getCCAPI().getDBAPI(ccid).open();
		String countSql="select count(1) from wf_task_log";//查询数据条数
		int count=1;
		int sumCount = DBSql.getInt(conn, countSql);
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		for(int i=0;i<=sumCount/1000;i++){//一次查1000条进行导入
			String sql="select top 1000 * from wf_task_log where id not in(select top "+i*1000+" id from wf_task_log)";
			List<RowMap> maps = DBSql.getMaps(conn, sql, new HashMap<Object, Object>());
			for (RowMap rowMap : maps) {
				System.err.println(">>>>>>>>>count:"+count++);
				String taskId = rowMap.getString("ID");
			    String createUid = rowMap.getString("owner");
			    String beginTime = rowMap.getString("BEGINTIME");
			    String endTime = rowMap.getString("ENDTIME");
			    //String createUid = "admin";
			    String targetUid = rowMap.getString("target");
			    Map map=new HashMap<Object, Object>();
			    map.put("USERID", targetUid);
			    String flag=DBSql.getString(conn, "select disenable from orguser where USERID=:USERID", "disenable", map);
			    if(!"0".equals(flag) || flag==null){
			    	System.err.println(">>>>>targetUid"+targetUid);
			    	continue;
			    }
			    //String targetUid = "admin";
			    String title = rowMap.getString("title");
			    if(title.length()>250){
			    	title=title.substring(0, 250);
			    }
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
			    //生成待办
				EAITaskInstance eaiTaskInstance = SDK.getTaskAPI().createEAITaskInstance("AWS5.2-Worklist", taskId, createUid, targetUid, title, json.toString(), state);
				Connection conn1 = null;
			    conn1 = DBSql.open();
			    Map paraMap = new HashMap();
			    paraMap.put("state", state);
			    paraMap.put("ownerDepartmentId", ownerDepartmentId);
			    paraMap.put("id", eaiTaskInstance.getId());
			    paraMap.put("BEGINTIME", beginTime);
			    try {
			      //修改类型、时间、已读
			      String sql1 = "update WFC_TASK set TASKSTATE=:state,OWNERDEPTID=:ownerDepartmentId,BEGINTIME=:BEGINTIME,READSTATE=1 where ID=:id";
			      DBSql.update(conn1, sql1, paraMap);
			    } catch (Exception localException) {
			    	localException.printStackTrace();
			    	try {
						conn.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    } finally {
			    	 try {
			    		 Map paraMap2 = new HashMap();
			    		 paraMap2.put("ENDTIME", endTime);
			    		 paraMap2.put("id", eaiTaskInstance.getId());
			    		 //完成该代办
			    		 SDK.getTaskAPI().completeEAITask(eaiTaskInstance.getId());
			    		 String sql2="update WFH_TASK set ENDTIME=:ENDTIME where ID=:id";
			    		 DBSql.update(conn1, sql2, paraMap2);
			    		 if(conn1!=null){
			    			 conn1.close();
			    		 }
					} catch (SQLException e) {
						try {
							conn.rollback();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}
		}
		try {
			if(conn!=null){
				if(!conn.getAutoCommit()){
					conn.commit();
				}
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
