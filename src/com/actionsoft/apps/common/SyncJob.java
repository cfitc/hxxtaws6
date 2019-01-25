package com.actionsoft.apps.common;

import com.actionsoft.bpms.org.event.OrganizationSyncListener;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.RoleModel;
import com.actionsoft.bpms.org.model.TeamModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.internal.cc.HttpAPIImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SyncJob implements OrganizationSyncListener {
	public String profileUUID = "afc37a377fe5e143743c0c71d3a23188";//5平台策略ID
	public String ht = "http://192.168.0.134/services/rs/org/";//5平台REST API调用地址
	public String cc = "e0ee6447-f01b-4079-b3a3-89150eae6f64";//5平台的CC连接
	public void afterOperationCompany(CompanyModel arg0, CompanyModel arg1, int arg2) {
	}

	public void afterOperationDepartment(DepartmentModel arg0, DepartmentModel arg1, int arg2) {
		StringBuffer url = new StringBuffer();
		url.append(this.ht);
		url.append(this.profileUUID);
		String departmentname = SDK.getORGAPI().getDepartmentById(arg1.getParentDepartmentId()).getName();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>departmentname:" + departmentname);
		Connection conn = SDK.getCCAPI().getDBAPI(this.cc).open();
		Map map = new HashMap();
		if (arg2 == 1) {
			map.put("departmentname", departmentname);
			int parentId = DBSql.getInt(conn, "SELECT ID FROM orgDepartment WHERE departmentname=:departmentname", "ID",
					map);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>parentId:" + parentId);
			url.append("/createDepartment/");
			url.append("1");
			url.append("/" + arg1.getName());
			url.append("/" + parentId);
			url.append("?departmentNo=" + (arg1.getNo().equals("") ? "''" : arg1.getNo()));
			url.append("&&departmentZone=" + (arg1.getZone().equals("''") ? "" : arg1.getZone()));
			url.append("&extend1=" + (arg1.getExt1().equals("") ? "''" : arg1.getExt1()));
			url.append("&extend2=" + (arg1.getExt2().equals("") ? "''" : arg1.getExt2()));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				System.out.println(new HttpAPIImpl().get(url.toString()));
			} catch (IOException e) {
				System.err.println("新增部门同步到5平台出错！");
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if (arg2 == 2) {
			map.put("departmentname", arg0.getName());
			int parentId = DBSql.getInt(conn, "SELECT ID FROM orgDepartment WHERE departmentname=:departmentname", "ID",
					map);
			url.append("/updateDepartment/");
			url.append(parentId);
			url.append("?departmentName=" + arg1.getName());
			url.append("&departmentNo=" + (arg1.getNo().equals("''") ? "" : arg1.getNo()));
			url.append("&&departmentZone=" + (arg1.getZone().equals("''") ? "" : arg1.getZone()));
			url.append("&extend1=" + (arg1.getExt1().equals("") ? "''" : arg1.getExt1()));
			url.append("&extend2=" + (arg1.getExt2().equals("") ? "''" : arg1.getExt2()));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				System.out.println(new HttpAPIImpl().get(url.toString()));
			} catch (IOException e) {
				System.err.println("更新部门同步到5平台出错！");
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if (arg2 == 3) {
			map.put("departmentname", arg1.getName());
			int parentId = DBSql.getInt(conn, "SELECT ID FROM orgDepartment WHERE departmentname=:departmentname", "ID",
					map);
			url.append("/removeDepartment/");
			url.append(parentId);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				System.out.println(new HttpAPIImpl().get(url.toString()));
			} catch (IOException e) {
				System.err.println("删除部门同步到5平台出错！");
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("111111");
	}

	public void afterOperationRole(RoleModel arg0, RoleModel arg1, int arg2) {
		StringBuffer url = new StringBuffer();
		url.append(this.ht);
		url.append(this.profileUUID);
		if (arg2 == 1) {
			url.append("/createRole/");
			url.append(arg1.getName());
			url.append("/" + arg1.getCategoryName());
			try {
				System.out.println(new HttpAPIImpl().get(url.toString()));
			} catch (IOException e) {
				System.err.println("新增角色同步到5平台出错！");
				e.printStackTrace();
			}
		}
		if (arg2 == 2) {
			url.append("/getRoleId/");
			url.append(arg0.getName());
			url.append("/" + arg0.getCategoryName());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				String roleId = new HttpAPIImpl().get(url.toString());
				System.out.println(">>>>>>>>>>>>>>>roleId:" + roleId);
				while (("".equals(roleId)) || (roleId == null)) {
					System.out.println(">>>>>>>>>>>>eeeeeee");
					Thread.sleep(100L);
				}
				System.out.println(">>>>>>>>>>>>>>>roleId:" + roleId);
				StringBuffer url1 = new StringBuffer();
				url1.append(this.ht + this.profileUUID);
				url1.append("/updateRole/");
				url1.append(roleId);
				url1.append("/" + arg1.getName());
				url1.append("/" + arg1.getCategoryName());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>url1:" + url1);
				System.out.println(new HttpAPIImpl().get(url1.toString()));
			} catch (IOException e) {
				System.err.println("更新角色同步到5平台出错！");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (arg2 == 3) {
			url.append("/getRoleId/");
			url.append(arg1.getName());
			url.append("/" + arg1.getCategoryName());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				String roleId = new HttpAPIImpl().get(url.toString());
				System.out.println(">>>>>>>>>>>>>>>roleId:" + roleId);
				while (("".equals(roleId)) || (roleId == null)) {
					System.out.println(">>>>>>>>>>>>eeeeeee");
					Thread.sleep(100L);
				}
				System.out.println(">>>>>>>>>>>>>>>roleId:" + roleId);
				StringBuffer url1 = new StringBuffer();
				url1.append(this.ht + this.profileUUID);
				url1.append("/removeRole/");
				url1.append(roleId);
				System.out.println(">>>>>>>>>>>>>>>>>>>>>url1:" + url1);
				System.out.println(new HttpAPIImpl().get(url1.toString()));
			} catch (IOException e) {
				System.err.println("删除角色同步到5平台出错！");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("111111");
	}

	public void afterOperationTeam(TeamModel arg0, TeamModel arg1, int arg2) {
		System.out.println("111111");
	}

	public void afterOperationUser(UserModel arg0, UserModel arg1, int arg2) {
		StringBuffer url = new StringBuffer();
		url.append(this.ht);
		url.append(this.profileUUID);
		Connection conn = SDK.getCCAPI().getDBAPI(cc).open();
		String categoryName = SDK.getORGAPI().getRoleById(arg1.getRoleId()).getCategoryName();
		String roleName = SDK.getORGAPI().getRoleById(arg1.getRoleId()).getName();
		Map map = new HashMap();
		String departmentname = SDK.getORGAPI().getDepartmentById(arg1.getDepartmentId()).getName();
		map.put("departmentname", departmentname);
		int departmentnameId = DBSql.getInt(conn, "SELECT ID FROM orgDepartment WHERE departmentname=:departmentname",
				"ID", map);
//		int departmentnameId=SDK.getCCAPI().getRDSAPI(cc).getInt("SELECT ID FROM orgDepartment WHERE departmentname="+departmentname, "departmentnameId");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>departmentnameId:" + departmentnameId);
		if (arg2 == 1) {
			//5平台同步开始
			url.append("/getRoleId/");
			url.append(roleName);
			url.append("/" + categoryName);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				@SuppressWarnings("resource")
				String roleId = new HttpAPIImpl().get(url.toString());
				System.out.println(">>>>>>>>>>>>>>>roleId:" + roleId);
				StringBuffer url1 = new StringBuffer();
				url1.append(this.ht + this.profileUUID);
				url1.append("/createUser/");
				url1.append(departmentnameId);
				url1.append("/" + arg1.getUID());
				url1.append("/" + arg1.getUserName());
				url1.append("/" + roleId);
				url1.append("/123456");
				url1.append("/" + arg1.isManager() + "?");
				url1.append("userNo=" + arg1.getUserNo());
				url1.append("&email=" + arg1.getEmail());
				url1.append("&mobileNo=" + arg1.getMobile());
				url1.append("&extend1=" + arg1.getExt1());
				url1.append("&extend2=" + arg1.getExt2());
				url1.append("&extend3=" + arg1.getExt3());
				url1.append("&extend4=" + arg1.getExt4());
				url1.append("&extend5=" + arg1.getExt5());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>url1:" + url1);
				System.out.println(new HttpAPIImpl().get(url1.toString()));
			} catch (IOException e) {
				System.err.println("新增用户同步到5平台出错！");

				e.printStackTrace();
			}
		}

		if (arg2 == 2) {
			url.append("/updateUser/");
			url.append(arg1.getUID() + "?");
			url.append("userName=" + arg1.getUserName());
			url.append("&userNo=" + arg1.getUserNo());
			url.append("&email=" + arg1.getEmail());
			url.append("&mobileNo=" + arg1.getMobile());
			url.append("&extend1=" + arg1.getExt1());
			url.append("&extend2=" + arg1.getExt2());
			url.append("&extend3=" + arg1.getExt3());
			url.append("&extend4=" + arg1.getExt4());
			url.append("&extend5=" + arg1.getExt5());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				System.out.println(new HttpAPIImpl().get(url.toString()));
			} catch (IOException e) {
				System.err.println("更新用户同步到5平台出错！");
				e.printStackTrace();
			}
		}
		if (arg2 == 3) {
			//5平台同步开始
			url.append("/removeUser/");
			url.append(arg1.getUID());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>url:" + url);
			try {
				System.out.println(new HttpAPIImpl().get(url.toString()));
			} catch (IOException e) {
				System.err.println("删除用户同步到5平台出错！");
				e.printStackTrace();
			}
		}
		System.out.println("111111");
	}

	public void beforeOperationCompany(CompanyModel arg0, int arg1) {
	}

	public void beforeOperationDepartment(DepartmentModel arg0, int arg1) {
	}

	public void beforeOperationRole(RoleModel arg0, int arg1) {
	}

	public void beforeOperationTeam(TeamModel arg0, int arg1) {
	}

	public void beforeOperationUser(UserModel arg0, int arg1) {
	}
}