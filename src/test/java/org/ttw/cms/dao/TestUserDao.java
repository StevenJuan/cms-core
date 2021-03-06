package org.ttw.cms.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.ttw.basic.model.Pager;
import org.ttw.basic.model.SystemContext;
import org.ttw.basic.test.util.AbstractDbUnitTestCase;
import org.ttw.basic.test.util.EntitiesHelper;

import org.ttw.cms.model.User;
import org.ttw.cms.model.Group;
import org.ttw.cms.model.Role;
import org.ttw.cms.model.RoleType;
import org.ttw.cms.model.UserGroup;
import org.ttw.cms.model.UserRole;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
@TestExecutionListeners({
	DbUnitTestExecutionListener.class,
	DependencyInjectionTestExecutionListener.class
	})
public class TestUserDao extends AbstractDbUnitTestCase{
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private IUserDao userDao;
	@Inject
	private IRoleDao roleDao;
	@Inject
	private IGroupDao groupDao;
	
	@Before
	public void setUp() throws SQLException, IOException, DatabaseUnitException{
		Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory,new SessionHolder(session));
		this.backupAllTable();
		IDataSet dSet = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, dSet);
	}
	
	@Test
	public void testListUserRoles() throws DatabaseUnitException, SQLException {
		
		List<Role> actuals = Arrays.asList(new Role(2,"文章发布人员",RoleType.ROLE_PUBLISH),new Role(3,"文章审核人员",RoleType.ROLE_AUDIT));
		List<Role> roles = userDao.listUserRoles(2);
		EntitiesHelper.assertRoles(roles,actuals);
	}
	@Test
	public void testListUserRoleIds() throws Exception {
		List<Integer> actuals = Arrays.asList(2,3);
		List<Integer> expected = userDao.listUserRoleIds(2);
		EntitiesHelper.assertObjects(expected, actuals);
	}
	@Test
	public void testListUserGroups() throws DatabaseUnitException, SQLException {
		List<Group> actuals = Arrays.asList(new Group(1,"财务处"),new Group(3,"宣传部"));
		List<Group> roles = userDao.listUserGroups(3);
		EntitiesHelper.assertGroups(roles, actuals);
	}
	
	@Test
	public void testListUserGroupIds() throws DatabaseUnitException, SQLException {
		List<Integer> actuals = Arrays.asList(1,3);
		List<Integer> expected = userDao.listUserGroupIds(3);
		EntitiesHelper.assertObjects(expected, actuals);
	}
	
	@Test
	public void testLoadUserRole() throws DatabaseUnitException, SQLException {
		int uid = 1;
		int rid = 1;
		UserRole ur = userDao.loadUserRole(uid, rid);
		User au = new User(1,"admin1","123","admin1","admin1@admin.com","110",1);
		Role ar = new Role(1,"管理员",RoleType.ROLE_ADMIN);
		EntitiesHelper.assertUser(ur.getUser(), au);
		EntitiesHelper.assertRole(ur.getRole(), ar);
	}
	
	@Test
	public void testLoadUserGroup() throws DatabaseUnitException, SQLException {
		
		int uid = 2;
		int gid = 1;
		UserGroup ug = userDao.loadUserGroup(uid, gid);
		User au = new User(2,"admin2","123","admin1","admin1@admin.com","110",1);
		Group ag = new Group(1,"财务处");
		EntitiesHelper.assertUser(ug.getUser(), au);
		EntitiesHelper.assertGroup(ug.getGroup(), ag);
	}
	
	@Test
	public void testLoadUserName() throws DatabaseUnitException, SQLException {
		User au = EntitiesHelper.getBaseUser();
		String username = "admin1";
		User eu = userDao.loadByUsername(username);
		EntitiesHelper.assertUser(eu, au);
	}
	
	@Test
	public void testListRoleUsers() throws DatabaseUnitException, SQLException {
		int rid = 2;
		List<User> aus = Arrays.asList(new User(2,"admin2","123","admin1","admin1@admin.com","110",1),
									   new User(3,"admin3","123","admin1","admin1@admin.com","110",1));
		List<User> eus = userDao.listRoleUsers(rid);
		EntitiesHelper.assertUsers(eus, aus);
	}
	
	@Test
	public void testListRoleUsersByRoleType() throws DatabaseUnitException, SQLException {
		List<User> aus = Arrays.asList(new User(2,"admin2","123","admin1","admin1@admin.com","110",1),
									   new User(3,"admin3","123","admin1","admin1@admin.com","110",1));
		List<User> eus = userDao.listRoleUsers(RoleType.ROLE_PUBLISH);
		EntitiesHelper.assertUsers(eus, aus);
	}
	
	@Test
	public void testAddUserGroup() throws DatabaseUnitException, SQLException {
		Group group = groupDao.load(1);
		User user = userDao.load(1); 
		userDao.addUserGroup(user,group);
		UserGroup ug = userDao.loadUserGroup(1,1);
		assertNotNull(ug);
		assertEquals(ug.getGroup().getId(),1);
		assertEquals(ug.getUser().getId(),1);
		
	}
	
	@Test
	public void testDUserRole() throws DatabaseUnitException, SQLException {
		Role role = roleDao.load(1);
		User user = userDao.load(1); 
		userDao.addUserRole(user,role);
		UserRole ur = userDao.loadUserRole(1,1);
		assertNotNull(ur);
		assertEquals(ur.getRole().getId(),1);
		assertEquals(ur.getUser().getId(),1);
	}
	
	@Test
	public void testDeleteUserRoles() throws DatabaseUnitException, SQLException {
		int uid=2;
		userDao.deleteUserRoles(uid);
		List<Role> urs = userDao.listUserRoles(uid);
		assertTrue(urs.size()<=0);
	}
	@Test
	public void testDeleteUserGroups() throws DatabaseUnitException, SQLException {
		int uid=2;
		userDao.deleteUserGroups(uid);
		List<Group> ugs = userDao.listUserGroups(uid);
		assertTrue(ugs.size()<=0);
	}
	
	@Test
	public void testDeleteUserRole() throws DatabaseUnitException, SQLException {
		int uid=1;
		int rid=1;
		userDao.deleteUserRole(uid,rid);
		assertNull(userDao.loadUserRole(uid,rid));
	}
	@Test
	public void testDeleteUserGroup() throws DatabaseUnitException, SQLException {
		int uid=1;
		int gid=2;
		userDao.deleteUserGroup(uid,gid);
		assertNull(userDao.loadUserGroup(uid,gid));
	}
	
	@Test
	public void testFindUser(){
		SystemContext.setPageOffset(0);
		SystemContext.setPageSize(15);
		List<User> actuals = Arrays.asList(new User(1,"admin1","123","admin1","admin1@admin.com","110",1),
				   new User(2,"admin2","123","admin1","admin1@admin.com","110",1),
				   new User(3,"admin3","123","admin1","admin1@admin.com","110",1));
		Pager<User> pages=userDao.findUser();
		assertNotNull(pages);
		assertEquals(pages.getTotal(),3);
		EntitiesHelper.assertUsers(pages.getDatas(),actuals);
	}
	
	
	
	@After
	public void tearDown() throws FileNotFoundException, DatabaseUnitException, SQLException{
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		Session session = holder.getSession();
		session.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
//		this.resumeTable();
	}
}
