package com.actionsoft.apps.gyxmgl.plugins;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

/**
 * 项目序号编号获取方式
 * @author Administrator
 *
 */
public class GyXmpxhXHExpressionImpl extends AbstExpression {

	public GyXmpxhXHExpressionImpl(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute(String arg0) throws AWSExpressionException {
		System.out.println("******************************");
		String pxh = "1";
		try {
			if(Integer.valueOf(pxh)<10)
				pxh="00"+pxh;
			if(10<=Integer.valueOf(pxh) && Integer.valueOf(pxh)<100)
				pxh="0"+pxh;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(System.err);
		}
		System.out.println("******************************");
		
		Date date = new Date();
		String year = new SimpleDateFormat("yyyy").format(date);
		
		//判断当前序号是否已经使用
		boolean isRepeat = judgeXHRepeat(year,pxh);
		if(isRepeat){
			List<String> allList = getAllPXH(); //获得所有的
			List<String> usedList = getUsedPXH(year); //获得已经存在 
			pxh = getFreeXH(allList, usedList); //从空闲列表中获得一个，赋给pxh
			
		}
		return pxh;
	}
	
	/**
	 * 判断新序号是否存在
	 * @param pxh
	 * @return
	 */
	public boolean judgeXHRepeat(String year,String pxh){
		try {
			
			String xpxh = year+pxh+"号";
			String sql ="select count(*) c from bo_act_pm_gyxmxxb where xmbh like '%"+xpxh+"%'";
			int count = DBSql.getInt(sql, "c");
			if(count > 0){
				return true;
			}else{
				return false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
	
	/**
	 * 若当前新序号已经使用，则用该方法获得1-999的
	 * @return
	 */
	public String getFreeXH(List<String> allList,List<String> usedList){
		List<String> stuList3 = new ArrayList<String>();
		stuList3.addAll(allList);
		stuList3.addAll(usedList);//把两个集合合成一个
		allList.retainAll(usedList);//找出两个集合中重复的数据
		stuList3.removeAll(allList);//然后移除
		System.out.println("=======获得空闲号码："+stuList3.toString()+"================");
		System.out.println("=======获得空闲号码中的第一个："+stuList3.get(0).toString()+"================");
		return stuList3.get(0).toString();
	}
	
	/**
	 * 获得1-999的所有序号
	 */
	public List<String> getAllPXH(){
		List<String> list = new ArrayList<String>();
		for(int i=1;i<=999;i++){
			String str = "";
			if(i<10)
				str="00"+i;
			if(10<=i && i<100)
				str ="0"+i;
			if(100<=i && i<1000)
				str = i+"";
			
			list.add(str);
		}
		return list;
	}
	
	/**
	 * 获得
	 * @param year
	 * @return
	 */
	public List<String> getUsedPXH(String year){
		List<String> list = new ArrayList<String>();
		String sql = "select XMBH from bo_act_pm_gyxmxxb where xmbh like '%营字"+year+"%' and 1=? order by xmbh asc";
		try {
			List<RowMap> mapList  = DBSql.getMaps(sql, 1);
			for(RowMap m: mapList){
				String xmbh = m.getString("XMBH");
				String xh = xmbh.substring(xmbh.indexOf("营字"+year+"")+6, xmbh.length()-1);
				list.add(xh);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
}
