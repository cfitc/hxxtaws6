package com.actionsoft.apps.gyxmgl.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StringUtil {
	
	/**
	 * 将xxx<XXX>转成xxx
	 * @param str
	 * @return
	 */
	public static String getUserStr(String str) {
		if (str.indexOf("<") > -1)
			str = str.substring(0, str.indexOf("<"));
		if (str.indexOf("&lg;") > -1)
			str = str.substring(0, str.indexOf("&lt"));
		return str;
	}
	
	/**
	 * 将xxx<XXX> yyy<YYY> zzz<ZZZ>转成xxx,yyy,zzz
	 * @param str
	 * @param sign
	 * @return
	 */
	public static String getUserStr(String strvalue, String oldsign, String newsign) {
		String str[] = strvalue.split(oldsign);
		String newStrValue = "";
		for(int i=0;i<str.length;i++){
			if(i<str.length-1){
				newStrValue = newStrValue + StringUtil.getUserStr(str[i]) + newsign;
			} else{
				newStrValue = newStrValue + StringUtil.getUserStr(str[i]);
			}
		}
		return newStrValue;
	}

	/**
	 * 根据符号对字符串分割成字符串数组
	 * @param value
	 * @param sign
	 * @return
	 */
	public static String[] ConvertString(String value, String sign) {
		String[] filename = value.split(sign);
		return filename;
	}
	
	/**
	 * 对字符串中的某符号转换成新符号
	 * @param value
	 * @return
	 */
	public static String replaceString(String value, String oldStr, String newStr){
		value = value.replaceAll(oldStr, newStr);
		return value;
	}
	
	/**
	 * 将xxx<XXX>转成XXX
	 * @param str
	 * @return
	 */
	public static String getXXXStr(String str) {
		if (str.indexOf(">") > -1)
			str = str.substring(str.indexOf("<")+1, str.indexOf(">"));
		if (str.indexOf("&gt;") > -1)
			str = str.substring(str.indexOf("&lt;")+1, str.indexOf("&gt;"));
		return str;
	}
	
	/**
	 * 将xxx<XXX> yyy<YYY> zzz<ZZZ>转成XXX,YYY,ZZZ
	 * @param str
	 * @param sign
	 * @return
	 */
	public static String getUserNameStr(String strvalue, String oldsign, String newsign) {
		String str[] = strvalue.split(oldsign);
		String newStrValue = "";
		for(int i=0;i<str.length;i++){
			if(i<str.length-1){
				newStrValue = newStrValue + StringUtil.getXXXStr(str[i]) + newsign;
			} else{
				newStrValue = newStrValue + StringUtil.getXXXStr(str[i]);
			}
		}
		return newStrValue;
	}
	
	/**
	 * 日期String串转成yyyy-MM-dd
	 * @param DateLog
	 * @return
	 */
	public static String getYMDDate(String DateLog){
		String dateStr = "";
		try {
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(DateLog);
			dateStr = sdf.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStr;
	}
	
	public static void main(String [] args){
		System.out.println(getUserNameStr("xxx<XXX> yyy<YYY>"," ",","));
	}
}
