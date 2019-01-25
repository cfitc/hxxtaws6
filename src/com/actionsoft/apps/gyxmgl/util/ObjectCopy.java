package com.actionsoft.apps.gyxmgl.util;

import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

/**
 * BO对象COPY类
 * 根据字段匹配值
 * 把BO对象的值复制给另外一个BO，拷贝字段名称相同的值
 * 如果是MySql数据库，需要修改查询系统表的语法
 * MySql:select COLUMN_NAME from information_schema.columns where TABLE_NAME =?
 * Oracle:select column_name from user_tab_columns c where c.TABLE_NAME = ?
 * @author chenyu
 *
 */
public class ObjectCopy {
	
	public static String ptzd = "ID,ORGID,BINDID,CREATEDATE,CREATEUSER,UPDATEDATE,UPDATEUSER,PROCESSDEFID,ISEND";
	
	
	/**
	 * BO对象COPY类
	 * 根据字段匹配值
	 * 把BO对象的值复制给另外一个BO，拷贝字段名称相同的值
	 * @param FromObject
	 * @param ToBoName
	 * @return
	 */
	public static BO ObjectCopyMtd(BO FromObject,String ToBoName){
		try {
			/**
			 * mysql表结构所属表
			 */
			//String ColumnsTable = "information_schema.columns";
			/**
			 * oracle 表结构所属表
			 */
			String ColumnsTable = "user_tab_columns";
			List<RowMap> listItem = DBSql.getMaps("select COLUMN_NAME from "+ColumnsTable+"  where TABLE_NAME = ? ",ToBoName);
			Map<String, Object>  FromMapObj = FromObject.asMap();
			BO boObj = new BO();
			for(String key : FromMapObj.keySet()){
				if(ptzd.contains(key)){
					continue;
				}
				String fromKey = key;
				for(RowMap rowMap:listItem){
					String toKey = rowMap.getString("COLUMN_NAME");
					if(fromKey.equals(toKey)){
						boObj.set(fromKey, FromMapObj.get(fromKey));
					}
				}
			}
			return boObj;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * copyMap对象
	 * @param mapObject
	 * @param ToBoName
	 * @return
	 */
	public static BO ObjectCopyMtd(Map<String,Object> mapObject,String ToBoName){
		try {
			/**
			 * mysql表结构所属表
			 */
			//String ColumnsTable = "information_schema.columns";
			/**
			 * oracle 表结构所属表
			 */
			String ColumnsTable = "user_tab_columns";
			List<RowMap> listItem = DBSql.getMaps("select COLUMN_NAME from "+ColumnsTable+"  where TABLE_NAME = ? ",ToBoName);
			
			BO boObj = new BO();
			for(String key : mapObject.keySet()){
				if(ptzd.contains(key)){
					continue;
				}
				String fromKey = key;
				for(RowMap rowMap:listItem){
					String toKey = rowMap.getString("COLUMN_NAME");
					if(fromKey.equals(toKey)){
						boObj.set(fromKey, mapObject.get(fromKey));
					}
				}
			}
			return boObj;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param FromObject 元数据
	 * @param ToBoName  目标表
	 * @param exception  例外字段
	 * @return
	 */
	public static BO ObjectCopyMtd(BO FromObject,String ToBoName,String exception){
		try {
			/**
			 * mysql表结构所属表
			 */
			//String ColumnsTable = "information_schema.columns";
			/**
			 * oracle 表结构所属表
			 */
			String ColumnsTable = "user_tab_columns";
			List<RowMap> listItem = DBSql.getMaps("select COLUMN_NAME from "+ColumnsTable+"  where TABLE_NAME = ? ",ToBoName);
			Map<String, Object>  FromMapObj = FromObject.asMap();
			BO boObj = new BO();
			for(String key : FromMapObj.keySet()){
				if(ptzd.contains(key)){
					continue;
				}
				String fromKey = key;
				for(RowMap rowMap:listItem){
					String toKey = rowMap.getString("COLUMN_NAME");
					if(toKey == "BINDID" || toKey.equals("BINDID")){
						System.out.print(toKey);
					}
					if(fromKey.equals(toKey) && (exception == null ? true: !exception.contains(toKey))){
						boObj.set(fromKey, FromMapObj.get(fromKey));
					}
				}
			}
			return boObj;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param FromObject 元数据
	 * @param ToBoName  目标表
	 * @param exception  例外字段用，隔开
	 * @return
	 */
	public static BO ObjectCopyMtd(Map<String,Object> mapObject,String ToBoName,String exception){
		try {
			/**
			 * mysql表结构所属表
			 */
			//String ColumnsTable = "information_schema.columns";
			/**
			 * oracle 表结构所属表
			 */
			String ColumnsTable = "user_tab_columns";
			List<RowMap> listItem = DBSql.getMaps("select COLUMN_NAME from "+ColumnsTable+"  where TABLE_NAME = ? ",ToBoName);
			
			BO boObj = new BO();
			for(String key : mapObject.keySet()){
				String fromKey = key;
				for(RowMap rowMap:listItem){
					String toKey = rowMap.getString("COLUMN_NAME");
					if(fromKey.equals(toKey) && exception == null ? true: !exception.contains(toKey)){
					boObj.set(fromKey, mapObject.get(fromKey));
					}
				}
			}
			return boObj;
		} catch (Exception e) {
			return null;
		}
		
	}
}
