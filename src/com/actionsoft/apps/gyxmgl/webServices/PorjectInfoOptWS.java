package com.actionsoft.apps.gyxmgl.webServices;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.actionsoft.apps.gyxmgl.event.BizForGyFabgFXToMD;
import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.server.bind.annotation.Param;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSDataAccessException;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.service.response.StringResponse;
import com.actionsoft.bpms.server.bind.annotation.HandlerType;

import net.sf.json.JSONObject;

/**
 * 5平台项目信息同步至6平台，6平台提供接口，5平台进行调用
 * @author Administrator
 *
 */
@Controller(type = HandlerType.OPENAPI, apiName = "ProjectInfoOpt API", desc = "项目信息同步API")
@WebService(serviceName = "ProjectInfoAPI")
public class PorjectInfoOptWS {

	/**
	 * 更新项目信息
	 * @author Administrator
	 * 参数模型{"XMMC","111"}
	 */
	@Mapping(value = "updateProjectInfo")
	public StringResponse  updateProjectInfo(@Param(value = "projectInfo",desc="项目信息JSON",required=true) String projectInfo){
		StringResponse r = new StringResponse();
		String result ="";	//返回结果
		String xmid ="";	//6平台项目ID
		String id ="";	//6平台项目信息表数据ID
		String xmjdid ="";	//项目阶段ID
		String xmjydsid="";	//项目交易对手ID
		String createUser = "";	//创建人
		try {
			if(!projectInfo.equals("")){
				JSONObject proInfo = JSONObject.fromObject(projectInfo);
				//5平台项目ID
				String yxmid = proInfo.getString("XMID");
				String yjdid = proInfo.getString("XMJDID");
				String yjydsid = proInfo.getString("JYDSID");
				createUser = proInfo.getString("CREATEUSER");
				if(yxmid.equals("")){
					result = getResultJson("error","项目ID不存在，请检查项目数据","");
					r.setData(result);
					return r;
				}
				//根据5平台项目ID查询6平台项目ID及数据ID
				//项目ID转换为6平台
				xmid = DBSql.getString("SELECT XMID FROM "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" WHERE YXMID = '"+yxmid+"'","XMID");
				xmjdid = DBSql.getString("SELECT ID FROM "+GlobleParams.BO_ACT_PM_XMJDB_TABLE+" WHERE YJDID = '"+yjdid+"'","ID");
				xmjydsid = DBSql.getString("SELECT ID FROM "+GlobleParams.BO_ACT_PM_KHXXB_TABLE+" WHERE KHXXID = '"+yjydsid+"'","ID");
				BO proInfoBO = (BO) JSONObject.toBean(proInfo, BO.class);
				proInfoBO.set("XMJDID", xmjdid);
				proInfoBO.set("JYDSID", xmjydsid);
				proInfoBO.set("YXMID", yxmid);
				proInfoBO.set("YJYDSID", yjydsid);
				proInfoBO.set("YJDID", yjdid);
				//如果XMID为空，则新建项目信息，不为空则更新
				if(xmid.equals("")){
					String bindId  = SDK.getProcessAPI().createBOProcessInstance(GlobleParams.GYXMXX_UUID, createUser, "项目信息同步5TO6").getId();
					String newID = DBSql.getString("SELECT ID FROM "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" WHERE BINDID = '"+bindId+"'","ID");
					proInfoBO.set("XMID", newID);
					SDK.getBOAPI().create(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, proInfoBO, bindId, createUser);
				}else{
					proInfoBO.set("XMID", xmid);
					id = DBSql.getString("SELECT ID FROM "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" WHERE YXMID = '"+yxmid+"'","ID");
					proInfoBO.set("ID", id);
					SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, proInfoBO);
				}
				//更新项目组成员信息
				String xmjl = proInfoBO.getString("XMJL");//项目经理MMM<MMM>
				String xmzcy = proInfoBO.getString("XMZCY");//项目组成员
				BizForGyFabgFXToMD biz = new BizForGyFabgFXToMD();
				biz.GxrToMD(yxmid, xmjl, xmzcy, createUser);
			}
			result = getResultJson("success","更新成功","");
			r.setData(result);
			return r;
		} catch (AWSDataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 更新项目状态信息
	 * 参数模型{"XMID":"123456","XMZT","归档中"}
	 */
	@Mapping(value = "updateProjectInfoState")
	public StringResponse updateProjectInfoState(@Param(value = "xmid",desc="项目ID",required=true) String xmid,@Param(value = "xmzt",desc="项目状态",required=true) String xmzt){
		StringResponse r = new StringResponse();
		String result ="";
		try {
			if(xmid != "" && !"".equals(xmid)){
				DBSql.update("update " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " set xmzt = '"+GlobleParams.gyxmlxspStatusIng + "' where yxmid = '"+xmid+"'");
			}
			result = getResultJson("success","更新成功","");
		} catch (AWSDataAccessException e) {
			result = getResultJson("error",e.getMessage(),"");
			e.printStackTrace();
		}
		r.setData(result);
		return r;
	}
	
	/**
	 * 输出结果组装
	 * @param result
	 * @param msg
	 * @param data
	 * @return
	 */
	public static String getResultJson(String result,String msg,String data){
		JSONObject jb = new JSONObject();
		jb.put("RESULT", result);
		jb.put("MSG", msg);
		jb.put("DATA", data);
		return jb.toString();
	}
}
