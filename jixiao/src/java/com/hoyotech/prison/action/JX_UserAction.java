package com.hoyotech.prison.action;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import com.hoyotech.prison.service.impl.JX_UserService;
import com.hoyotech.prison.bean.JX_Department;
import com.hoyotech.prison.bean.JX_Role;
import com.hoyotech.prison.bean.JX_User;
import com.hoyotech.prison.bean.JX_People;
import com.hoyotech.prison.bean.JX_UserRole;
import com.hoyotech.prison.bean.PostTable;
import com.isa.pims.basic.ServletRequestUtils;

public class JX_UserAction {
	
	HttpServletRequest request=ServletActionContext.getRequest();
	private JX_UserService jx_UserService;
	private JX_User jx_User;
	private JX_People jx_People;
	private String userId;
	private String peopleId;	
	private String userState;
	private String deptId;
	private int pageNum;
	private int limit; 
	private int totalPage;
	private int totalNum;
	List<JX_Department> dept_list;
	List<PostTable> posts;
	private List<JX_Role> jx_Roles;
	private List<JX_User> jx_Users;
	private String[] roleIds;
	private String checkstring;
	private int errorType=2;

	public JX_UserAction(){
		
	}
	
	/*
	 * 刘泉2015.01.26
	 * 进入用户添加页面
	 * */
	public String bs_gotoAddUser(){
		String dept_id=request.getSession().getAttribute("deptId").toString();		
		dept_list=jx_UserService.getDepts(dept_id);
		posts=jx_UserService.getPosts();
		return "gotoAddUser";
	}
	
	/*
	 * 刘泉2015.01.27
	 * 添加用户函数
	 * */
	public String bs_SaveAddUser(){
		String userName = jx_User.getUsername();
		JX_User jx_user1 = new JX_User();
		jx_user1 = jx_UserService.queryUser(userName);
		if(jx_user1!=null){
			errorType = 0;
			return bs_gotoAddUser();
		}
		else{
			errorType = 1;
			jx_People.setAdd_date(new Date());
			jx_People.setId(jx_UserService.bs_SaveAddPeo(jx_People));
			jx_User.setJx_people(jx_People);
			jx_User.setOnline_state(0);
			jx_User.setPassword("E10ADC3949BA59ABBE56E057F20F883E");
			jx_User.setAdd_date(new Date());
			userId=jx_UserService.bs_SaveAdd(jx_User);
			jx_User=jx_UserService.detail(userId);
			return "AddSuccess";
		}		
	}
	
	/*
	 * 刘泉2015.01.30
	 * 进入用户角色界面
	 * */
	public String bs_gotoUserRole(){
		jx_User=jx_UserService.detail(userId);
		String dept_id=request.getSession().getAttribute("deptId").toString();
		jx_Roles=jx_UserService.jx_roleList(dept_id);
		checkstring="";
		for(JX_UserRole i:jx_User.getRoles()){
			checkstring=checkstring+i.getJx_role().getId()+"," ;
		}
		return "gotoUserRole";
	}
	
	/*
	 * 刘泉2015.01.30
	 * 保存用户角色联系 
	 * */
	public String updateRole() {
		jx_UserService.updateRole(jx_User.getId(),roleIds);	
		jx_User=jx_UserService.detail(jx_User.getId());
		return "UpdateSuccess"; 
	}
	
	/*
	 * 刘泉2015.01.31
	 * 进入用户列表界面
	 * */
	public String bs_gotoUserList(){		
		bs_selectList();
		return "gotoUserList";
	}
	
	/*
	 * 刘泉2015.02.06
	 * 用户列表翻页
	 * */
	public String bs_gotoUserListPage(){
		bs_selectList();
		return "gotoUserList";
	}
	
	public void bs_selectList(){
		String dept_id=request.getSession().getAttribute("deptId").toString();
		pageNum = ServletRequestUtils.getInt(request, "page", "1");
		limit = ServletRequestUtils.getInt(request, "limit", "5");
		jx_Users=jx_UserService.getJX_UserList(dept_id,pageNum,limit);	
		dept_list=jx_UserService.getXJdept(dept_id);			
		totalNum=jx_UserService.getJX_UserListNum(dept_id);	
		totalPage = (totalNum - 1) / limit + 1;
	}
	
	/*
	 * 刘泉
	 * 进入用户详细信息界面
	 * */
	public String bs_gotoUserDetail(){
		jx_User=jx_UserService.detail(userId);
		return "gotoUserDetail";
	}
	
	/*
	 * 刘泉
	 * 进入用户信息修改界面
	 * */
	public String bs_gotoUserEdit(){
		String dept_id=request.getSession().getAttribute("deptId").toString();
		jx_User=jx_UserService.detail(userId);
		jx_People = jx_UserService.peodetail(jx_User.getJx_people().getId());
		dept_list=jx_UserService.getDepts(dept_id);
		posts=jx_UserService.getPosts();
		return "gotoUserEdit";
	}
	
	/*
	 *刘泉2015.03.24
	 * 保存修改用户信息
	 * */
	public String bs_SaveUpdateUser(){
		jx_User.setId(userId);
		jx_UserService.bs_SaveUpdate(jx_User,jx_People);
		jx_User=jx_UserService.detail(userId);
		return "UpdateSuccess";
	}
	
	/*
	 * 刘泉2015.03.25
	 * 删除用户信息
	 * */
	public String bs_detele(){
		jx_UserService.bs_deleteUser(userId, peopleId);
		bs_selectList();
		return "DeleteSuccess";
	}
	
//	/**
//	 * 进入角色修改页面
//	 * **/
//	public String roleEdit() {
//		user = userService.detail(id);
//		HttpServletRequest request = ServletActionContext.getRequest();
//		String orgType=StringUtils.$C((String)request.getSession().getAttribute("orgType"),"");
//		String prisonCode=PrisonUtil.getPrisonCode(request);
//		roles=userService.roleList(orgType,prisonCode);
//		checkstring="";
//		for(UserRole i:user.getRoles()){
//			checkstring=checkstring+i.getRoleId().getId()+"," ;
//		}
//		String type=(String)request.getSession().getAttribute("orgType");
//		if("2".equals(type)){
//			return "stroleedit";
//		}else{
//			return "roleedit";
//		}
//		
//	}
	
	
	public List<JX_User> getJx_Users() {
		return jx_Users;
	}
  
	public void setJx_Users(List<JX_User> jxUsers) {
		jx_Users = jxUsers;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}



	public JX_UserService getJx_UserService() {
		return jx_UserService;
	}



	public void setJx_UserService(JX_UserService jxUserService) {
		jx_UserService = jxUserService;
	}



	public List<JX_Department> getDept_list() {
		return dept_list;
	}

	public void setDept_list(List<JX_Department> deptList) {
		dept_list = deptList;
	}

	public JX_User getJx_User() {
		return jx_User;
	}

	public void setJx_User(JX_User jxUser) {
		jx_User = jxUser;
	}
	
	public JX_People getJx_People() {
		return jx_People;
	}

	public void setJx_People(JX_People jxPeople) {
		jx_People = jxPeople;
	}
	
	public List<PostTable> getPosts() {
		return posts;
	}

	public void setPosts(List<PostTable> posts) {
		this.posts = posts;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public List<JX_Role> getJx_Roles() {
		return jx_Roles;
	}

	public void setJx_Roles(List<JX_Role> jxRoles) {
		jx_Roles = jxRoles;
	}

	public String[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}

	public String getCheckstring() {
		return checkstring;
	}

	public void setCheckstring(String checkstring) {
		this.checkstring = checkstring;
	}
	
	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	
	public String getPeopleId() {
		return peopleId;
	}

	public void setPeopleId(String peopleId) {
		this.peopleId = peopleId;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	public int getErrorType() {
		return errorType;
	}

}
