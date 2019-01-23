package com.actionsoft.apps.delegation;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.actionsoft.bpms.commons.security.delegation.model.DelegationModel;
import com.actionsoft.bpms.commons.security.delegation.model.DelegationScopeModel;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
@Controller
public class DelegationController {
	@Mapping(value="com.actionsoft.apps.delegation_sync.createDelegation",session=false,noSessionEvaluate="无",noSessionReason="测试")
	public void createDelegation(String wfRange,String applicantUser,String delegateUser,String dateFrom,String dateTo,String memo,String commisionType,String status){		
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long beginTime=0L;
			long endTime=0L;
			try {
				beginTime = sdf.parse(dateFrom).getTime();
				endTime = sdf.parse(dateTo).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String[] wfList = wfRange.split(" ");
			DelegationModel model=new DelegationModel();
			model.setScopeType("1");
			model.setDelegateUser(delegateUser);
			model.setApplicantUser(applicantUser);
			model.setBeginTime(new Timestamp(beginTime));
			model.setEndTime(new Timestamp(endTime));
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			model.setDelegateReason(memo);
			List<DelegationScopeModel> list=new ArrayList<DelegationScopeModel>();
			for(int i=0;i<wfList.length;i++){
				String listx=wfList[i];
				if(listx==null&&"".equals(listx)){
					continue;
				}
				DelegationScopeModel scopeModel=new DelegationScopeModel();
				scopeModel.setResourceType("0");
				scopeModel.setResourceId(listx.trim());
				list.add(scopeModel);
			}
			model.setScope(list);
			SDK.getDelegationAPI().create(model);
	}

	@Mapping(value="com.actionsoft.apps.delegation_sync.createupdateuser",session=false,noSessionEvaluate="无",noSessionReason="创建或修改用户")
	public void createorupdateuser(String depname,String uname,String uid,String email,String mobile,String tel,String extend1,String extend2,String extend3,String extend4,String extend5,String pwd){		
	    int num=DBSql.getInt("select count(*) c from ORGUSER where userid='"+uid+"'", "c");
		if(num==0) {
			depname=UrlUtil.getURLDecoderString(depname);
			uname=UrlUtil.getURLDecoderString(uname);
			extend1=UrlUtil.getURLDecoderString(extend1);
			extend2=UrlUtil.getURLDecoderString(extend2);
			extend3=UrlUtil.getURLDecoderString(extend3);
			extend4=UrlUtil.getURLDecoderString(extend4);
			extend5=UrlUtil.getURLDecoderString(extend5);
			String departmentId=DBSql.getString("select ID from orgdepartment  where departmentname='"+depname+"'","ID");
			String userno="";
			String roleid=DBSql.getString("select ID from orgrole  where rolename='普通员工'","ID");
			SDK.getORGAPI().createUser(departmentId,uid,uname,roleid,userno,pwd,false,email,mobile,extend1,extend2,extend3,extend4,extend5);
		}
	    
	
	}
}
