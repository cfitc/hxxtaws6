package com.actionsoft.apps.gyxmgl.service;

import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSException;
import com.actionsoft.sdk.local.SDK;

public class GyxmTaskInfoService {

	public long totalRecordsSchema = 0L;
	/**
	 * 待办
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfo(int curPage, int rowsPerPage,String xmid,String keyword) {
		if (curPage <= 0) {
			throw new AWSException("参数错误：" + curPage);
		}
		if (rowsPerPage <= 0) {
			throw new AWSException("参数错误：" + rowsPerPage);
		}
		int firstRow = (curPage - 1) * rowsPerPage+1;
		int endRow = curPage* rowsPerPage;
        String baseSql =   "SELECT aa.* , ROWNUM RN FROM (SELECT jdgl.lcmc, tj.lcbt,wf.processinstid, wf.owner,wf.id taskId, wf.target, wf.begintime "+
        " FROM "+GlobleParams.BO_ACT_PM_WF_TJXX_TABLE+" tj"+
        " LEFT JOIN "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" jdgl ON tj.xmjdid = jdgl.jdid and tj.lcid = jdgl.lcuuid "+
        " AND jdgl.lcuuid = tj.lcid"+
        " LEFT JOIN "+GlobleParams.WFC_TASK_TABLE+" wf ON tj.lcbindid = wf.processinstid"+
        " WHERE tj.xmid = '"+xmid+"' AND wf.taskstate = '1' ORDER BY tj.CREATEDATE) aa";
        if(keyword != "" && !keyword.equals("")){
        	baseSql += " where aa.lcbt like '%"+keyword+"%' ";
		}
        
		String sql = "SELECT * FROM ("+baseSql+ ")"+" WHERE RN BETWEEN " + firstRow + " AND "+endRow;
		String sqlCount = "SELECT count(1) FROM ("+baseSql+ ")";
		List<RowMap> list = DBSql.getMaps(sql);
		String amount = DBSql.getString(sqlCount);
		this.totalRecordsSchema += Long.parseLong(amount);
		return list;
	}
	/**
	 * 已办
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfoHavetodo(int curPage, int rowsPerPage,String xmid) {
		if (curPage <= 0) {
			throw new AWSException("参数错误：" + curPage);
		}
		if (rowsPerPage <= 0) {
			throw new AWSException("参数错误：" + rowsPerPage);
		}
		int firstRow = (curPage - 1) * rowsPerPage+1;
		int endRow = curPage* rowsPerPage;
		String baseSql = "select aa.*, rownum rn from (select tj.lcbt, jd.xmjdmc, '自营项目' as lcfl, jdgl.lcmc,wp.starttime,wp.endtime,wp.id processinstid,bb.taskid "+
				"from "+GlobleParams.BO_ACT_PM_WF_TJXX_TABLE+" tj right join "+GlobleParams.WFC_PROCESS_TABLE+" wp on to_char(tj.lcbindid) = wp.id and wp.isend = '1' "+
				"left join "+GlobleParams.BO_ACT_PM_XMJDB_TABLE+" jd on tj.xmjdid = jd.id "+
				"left join "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" jdgl on jd.id = jdgl.jdid and tj.lcid = jdgl.lcuuid "+
				" left join (select wf.processinstid,max(wf.id) taskid,max(wf.endtime) from wfh_task wf  group by wf.processinstid) bb on wp.id = bb.processinstid "+
				"where tj.xmid = '"+xmid+"') aa";
		//已办数据查询语句
		String sql = "select * from ( " +baseSql+") WHERE RN BETWEEN " + firstRow + " AND "+endRow;
		//已办数量查询语句
		String sqlCount = "select count(1) from ("+baseSql+")";
		List<RowMap> list = DBSql.getMaps(sql);
		String amount = DBSql.getString(sqlCount);
		this.totalRecordsSchema = Long.parseLong(amount);
		return list;
	}
	
	/**
	 * 项目文档
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfoProjectFile(int curPage, int rowsPerPage,String xmid) {
		if (curPage <= 0) {
			throw new AWSException("参数错误：" + curPage);
		}
		if (rowsPerPage <= 0) {
			throw new AWSException("参数错误：" + rowsPerPage);
		}
		int firstRow = (curPage - 1) * rowsPerPage+1;
		int endRow = curPage* rowsPerPage;
		String sql = "SELECT * FROM (SELECT aa.* , ROWNUM RN FROM (SELECT BAA.ID,BAA.XMID,BAA.XMJD, BAPX.XMJDMC, BAPWT.LCBT AS LCMC,BAA.SCR,BAA.SCSJ,BAA.WJBH,BAA.WJMC,BAA.WDLB, BAA.DZWJ, BAA.WJZT "+
					 " FROM BO_ACT_ATTACH BAA LEFT JOIN BO_ACT_PM_WF_TJXX BAPWT ON BAA.BINDID = BAPWT.LCBINDID "+
					 " LEFT JOIN BO_ACT_PM_XMJDB BAPX ON BAPWT.XMJDID = BAPX.ID"+
					 " WHERE BAPWT.XMID = '"+xmid+"' AND BAPX.SFQY = '是' ORDER BY BAA.CREATEDATE) aa)"+
                     " WHERE RN BETWEEN " + firstRow + " AND "+endRow;
		String sqlCount = "SELECT count(1) "+
					 " FROM BO_ACT_ATTACH BAA LEFT JOIN BO_ACT_PM_WF_TJXX BAPWT ON BAA.BINDID = BAPWT.LCBINDID "+
					 " LEFT JOIN BO_ACT_PM_XMJDB BAPX ON BAPWT.XMJDID = BAPX.ID"+
					 " WHERE BAPWT.XMID = '"+xmid+"' AND BAPX.SFQY = '是'";
		List<RowMap> list = DBSql.getMaps(sql);
		String amount = DBSql.getString(sqlCount);
		this.totalRecordsSchema = Long.parseLong(amount);
		return list;
	}
	
	/**
	 * 项目团队
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfoProjectTeam(int curPage, int rowsPerPage,String xmid) {
		if (curPage <= 0) {
			throw new AWSException("参数错误：" + curPage);
		}
		if (rowsPerPage <= 0) {
			throw new AWSException("参数错误：" + rowsPerPage);
		}
		int firstRow = (curPage - 1) * rowsPerPage+1;
		int endRow = curPage* rowsPerPage;

		String sql = "SELECT * FROM (SELECT aa.* , ROWNUM RN FROM (select pmperson.gxrjs, pmperson.gxrdw,pmperson.gxrmz, pmperson.gxrdh,pmperson.gxrsj, pmperson.gxryx, pmperson.updatedate from BO_ACT_PM_GYXMGXRB pmperson where pmperson.ssxmid = '"+xmid+"' order by pmperson.updatedate) aa)"+
        " WHERE RN BETWEEN " + firstRow + " AND "+endRow;
		String sqlCount = "select count(1) from BO_ACT_PM_GYXMGXRB t where t.ssxmid='"+xmid+"'";
		List<RowMap> list = DBSql.getMaps(sql);
		String amount = DBSql.getString(sqlCount);
		this.totalRecordsSchema = Long.parseLong(amount);
		return list;
	}
	/**
	 * 5平台待办
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfo_five(String xmid,String keyword) {
    	String waitInfo_Sql = "select aa.* from(select xmid, xmzt,xmlbmc,xmjdmc,lcuuid,lcid,lcz,lcmc,lcfl,"
    			+ "lcbt,title, begintime, bind_id,wfid,owner,target from (select e.xmid, e.xmzt,a.xmlbmc,b.xmjdmc,"
    			+ "c.lcuuid, c.lcid,c.lcz, c.lcmc,c.lcfl,e.lcbt,d.title, d.begintime, d.bind_id, d.wfid,"
    			+ " d.owner,d.target from bo_bcf_bd_xmflb a join bo_bcf_bd_xmjdb b on a.bindid = b.bindid and b.sfqy='是' join bo_bcf_bd_xmjdlcgxb c "
    			+ "on b.id = c.jdid join WF_TASK d on c.lcid = d.wfid and d.status='1' join bo_bcf_pm_wf_tjxx e on d.wfid = e.lcid and c.jdid = e.xmjdid "
    			+ "and e.xmid = '"+xmid+"' and d.bind_id = e.lcbindid order by a.pxh, b.pxh,c.lcfl, c.lcz, "
    			+ "d.begintime desc) group by xmid,xmzt, xmlbmc, xmjdmc,lcuuid,lcid,lcz,lcmc,lcfl,lcbt,title, begintime, bind_id,wfid, owner,target) aa";
    	if(keyword != "" && !keyword.equals("")){
    		waitInfo_Sql += " where aa.lcbt like '%"+keyword+"%' ";
 		}
    	String sqlcount="select count(1) from ("+waitInfo_Sql+")";
    	List<RowMap> list=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getMaps(waitInfo_Sql);
    	String amount=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getString(sqlcount);
		this.totalRecordsSchema += Long.parseLong(amount);
		return list;
	}
	/**
	 * 5平台已办
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfoHavetodo_five(String xmid) {
    	String doneInfo_Sql = "select xmid,xmzt,'自营项目' xmlbmc,xmjdmc,lcuuid,lcid,lcz,lcmc,'自营项目' lcfl,lcbt,min(begintime) begintime,max(endtime) endtime,bind_id,wfid,apxh,bpxh from (select "
    			+ "wftj.xmid,wftj.xmzt,xmjd.xmjdmc,lcuuid lcuuid,wftj.lcid,lcmc lcmc,wftj.lcz,wftj.lcbt,wfl.id,wf_id,apxh,xmjd.pxh bpxh,wf_end wf_end from (select sf.uuid lcuuid,sf.flowname lcmc,"
    			+ "wf.id id,wf.wf_id wf_id,0 as apxh,wf.wf_end wf_end from wf_messagedata wf join sysflow sf on wf.wf_id = sf.id where wf.wf_end = '1') wfl join bo_bcf_pm_wf_tjxx wftj on wfl.wf_id = wftj.lcid and wftj.xmid = '"
						+ xmid
						+ "' and wfl.id = wftj.lcbindid join bo_bcf_bd_xmjdb xmjd on wftj.xmjdid = xmjd.id) a join wf_task_log b on "
						+ "a.id = b.bind_id and a.wf_id = b.wfid and b.status = '1' group by xmid,xmzt,xmjdmc,lcuuid,lcid,lcz,lcmc,lcbt,bind_id,wfid,apxh,bpxh order by begintime, endtime, apxh, bpxh asc";
    	String sqlcount="select count(1) from ("+doneInfo_Sql+")";
    	List<RowMap> list=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getMaps(doneInfo_Sql);
    	String amount=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getString(sqlcount);
		this.totalRecordsSchema += Long.parseLong(amount);
		return list;
	}
	/**
	 * 5平台项目文档
	 * @param curPage
	 * @param rowsPerPage
	 * @param xmid
	 * @return
	 */
	public List<RowMap> queryGyxmTaskInfoProjectFile_five(int curPage, int rowsPerPage,String xmid) {
    	String doneInfo_Sql = "select doc.id,doc.xmid,doc.xmjd,doc.bindid,jd.xmjdmc,msg.title as lcmc, doc.scr,doc.scsj,doc.wjbh,doc.wjm,doc.wdlb,doc.dzwj,doc.wjzt from BO_BCF_PM_ATTACH doc left join wf_messagedata msg on (doc.bindid = msg.id) join bo_bcf_bd_xmjdb jd on doc.xmjd = jd.id where jd.sfqy = '是' and doc.xmid = '"+xmid+"' order by jd.pxh asc, doc.scsj asc";
    	String sqlcount="select count(1) from ("+doneInfo_Sql+")";
    	List<RowMap> list=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getMaps(doneInfo_Sql);
    	String amount=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getString(sqlcount);
		this.totalRecordsSchema += Long.parseLong(amount);
		return list;
	}
}
