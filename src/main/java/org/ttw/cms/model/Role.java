package org.ttw.cms.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 角色对象，用来对应可以访问的功能。系统中为了简单值定义了管理员，发布人员，审核人员
 * @author Administrator
 *
 */
@Entity
@Table(name="t_role")
public class Role {
	private int id;
	private String name;
	/**
	 * 角色编号，枚举类型
	 */
	private RoleType roleType;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Enumerated(EnumType.ORDINAL)
	public RoleType getRoleType() {
		return roleType;
	}
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}
	
	
}