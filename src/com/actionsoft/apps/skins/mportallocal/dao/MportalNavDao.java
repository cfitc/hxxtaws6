package com.actionsoft.apps.skins.mportallocal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.actionsoft.apps.skins.mportallocal.model.MportalNavModel;
import com.actionsoft.apps.skins.mportallocal.model.MportalNavUsedAlModel;
import com.actionsoft.bpms.commons.database.BatchPreparedStatementSetter;
import com.actionsoft.bpms.commons.database.RowMapper;
import com.actionsoft.bpms.commons.pagination.SQLPagination;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UUIDGener;
import com.actionsoft.bpms.util.UtilString;

import net.sf.json.JSONObject;

public class MportalNavDao {

	/**
	 * 创建主题风格导航
	 *
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public String create(MportalNavModel model) throws SQLException {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		model.setId(UUIDGener.getUUID());
		paramsMap.put(MportalNavModel.FIELD_ID, model.getId());
		paramsMap.put(MportalNavModel.FIELD_FUNCTION_ID, model.getFunctionId());
		paramsMap.put(MportalNavModel.FIELD_ORDER_NO, model.getOrderNo());
		paramsMap.put(MportalNavModel.FIELD_CREATE_USER, model.getCreateUser());
		paramsMap.put(MportalNavModel.FIELD_CREATE_TIME, model.getCreateTime());
		int result = DBSql.update(DBSql.getInsertStatement(MportalNavModel.DATABASE_ENTITY, paramsMap), paramsMap);
		return (result > 0) ? model.getId() : null;
	}

	public void batchCreate(final List<MportalNavModel> list, String curUserId) throws SQLException {
		Connection conn = null;
		try {
			conn = DBSql.open();
			// 关闭事务自动提交
			conn.setAutoCommit(false);
			StringBuffer sql = new StringBuffer();
			// 删除
			sql.append(" delete from ").append(MportalNavModel.DATABASE_ENTITY).append(" where createUser = ? ");
			DBSql.update(conn, sql.toString(), new Object[] { curUserId });
			sql = new StringBuffer();
			// 新增
			sql.append("insert into ").append(MportalNavModel.DATABASE_ENTITY).append("(id, functionId, orderNo, createUser, createTime) values (?, ?, ?, ?, ?)");

			// 执行批量更新
			DBSql.batch(conn, sql.toString(), new BatchPreparedStatementSetter() {

				public void setValues(PreparedStatement pst, int paramInt) throws SQLException {
					MportalNavModel model = list.get(paramInt);
					pst.setString(1, UUIDGener.getUUID());
					pst.setString(2, model.getFunctionId());
					pst.setInt(3, model.getOrderNo());
					pst.setString(4, model.getCreateUser());
					pst.setTimestamp(5, model.getCreateTime());
				}

				public int getBatchSize() {
					return list.size();
				}
			});
			// 语句执行完毕，提交本事务
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			// 关闭连接
			DBSql.close(conn);
		}
	}

	public List<MportalNavModel> getMetroNav(String userId) throws SQLException {
		List<MportalNavModel> list = new LinkedList<MportalNavModel>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ").append(MportalNavModel.DATABASE_ENTITY).append(" where createUser = ? order by orderNo ");
			list = DBSql.query(sql.toString(), new RowMapper<MportalNavModel>() {

				public MportalNavModel mapRow(ResultSet rs, int arg1) throws SQLException {
					MportalNavModel model = new MportalNavModel();
					model.setId(rs.getString(MportalNavModel.FIELD_ID));
					model.setFunctionId(rs.getString(MportalNavModel.FIELD_FUNCTION_ID));
					model.setOrderNo(rs.getInt(MportalNavModel.FIELD_ORDER_NO));
					model.setCreateUser(rs.getString(MportalNavModel.FIELD_CREATE_USER));
					model.setCreateTime(rs.getTimestamp(MportalNavModel.FIELD_CREATE_TIME));
					return model;
				}

			}, new Object[] { userId });
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return list;
	}

	public long getPrevLoginTime(String userId) {
		long prevLoginTime = 0;
		String sql = " select * from SYS_SESSION s where s.userid = '" + userId + "' order by s.STARTTIME desc ";
		List<Long> list = DBSql.query(SQLPagination.getPaginitionSQL(sql, 0, 2), new RowMapper<Long>() {
			public Long mapRow(ResultSet rs, int paramInt) throws SQLException {
				return rs.getLong("starttime");
			}
		});
		if (list != null && list.size() > 0) {
			prevLoginTime = list.get(list.size() - 1);
		}
		return prevLoginTime;
	}

	// 查询常用菜单项
	public List getNavUsedAlways(UserContext me) throws SQLException {
		List<MportalNavUsedAlModel> list = new LinkedList<MportalNavUsedAlModel>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ").append(MportalNavUsedAlModel.DATABASE_ENTITY_USED).append(" where USERID=? order by NAME ");
			list = DBSql.query(sql.toString(), new RowMapper<MportalNavUsedAlModel>() {
				public MportalNavUsedAlModel mapRow(ResultSet rs, int arg1) throws SQLException {
					MportalNavUsedAlModel model = new MportalNavUsedAlModel();
					model.setAppId(rs.getString("appId"));
					model.setIcon64(rs.getString("icon64"));
					model.setName(rs.getString("name"));
					model.setNavId(rs.getString("navId"));
					model.setTarget(rs.getString("target"));
					model.setUrl(rs.getString("url"));
					model.setId(rs.getString("id"));
					model.setFunnavid(rs.getString("funnavid"));
					return model;
				}
			}, me.getUID());
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return list;
	}

	public int addNavUsed(String navInfo, UserContext me) throws DataAccessException {
		// TODO Auto-generated method stub
		JSONObject json = JSONObject.fromObject(navInfo);
		String sql = "INSERT INTO " + MportalNavUsedAlModel.DATABASE_ENTITY_USED + "(ID,NAME,URL,TARGET,ICON64,APPID,USERID,FUNNAVID)VALUES(:ID,:NAME,:URL,:TARGET,:ICON64,:APPID,:USERID,:FUNNAVID)";
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("ID", UUIDGener.getUUID());
		paraMap.put("NAME", json.get("name"));
		paraMap.put("URL", json.get("url"));
		paraMap.put("TARGET", json.get("target"));
		paraMap.put("APPID", json.get("appId"));
		paraMap.put("ICON64", json.get("icon64"));
		paraMap.put("USERID", me.getUID());
		paraMap.put("FUNNAVID", json.get("id"));
		return DBSql.update(sql, paraMap);
	}

	public int delNavUsed(UserContext me, String FUNNAVID) {
		String sql = " delete from  " + MportalNavUsedAlModel.DATABASE_ENTITY_USED + " where USERID = '" + me.getUID() + "'";
		if (!UtilString.isEmpty(FUNNAVID)) {
			sql += " AND FUNNAVID='" + FUNNAVID + "'";
		}
		int rs = DBSql.update(sql);
		return rs;
	}

}
