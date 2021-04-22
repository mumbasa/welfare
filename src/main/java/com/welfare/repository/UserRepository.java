package com.welfare.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.welfare.data.Role;
import com.welfare.data.User;
import com.welfare.rowmapper.RoleMapper;
import com.welfare.rowmapper.UserMapper;

@Repository
public class UserRepository {

	@Autowired
	JdbcTemplate template;

	@Autowired
	BCryptPasswordEncoder encoder;

	public int addUser(String username, String name, String password, int role) {
		String sql = "INSERT INTO `company`.`user`(name,`email`,`password`,role)VALUES(?,?,?,?)";
		return template.update(sql, name, username, encoder.encode(password), role);
	}

	public int deleteUser(long id) {
		String sql = "UPDATE user set active='N' where id=?";
		return template.update(sql, id);
	}

	public int changePass(long id,String password) {
		String sql = "UPDATE user set password=? where id=?";
		return template.update(sql, encoder.encode(password),id);
	}

	
	public List<Role> getRoles() {
		String sql = "SELECT * FROm roles";
		return template.query(sql, new RoleMapper());
	}

	public Role getRole(int id) {
		String sql = "SELECT * FROm roles where role_id=?";
		return template.queryForObject(sql, new RoleMapper(),id);
	}

	public List<User> getUsers() {
		String sql = "SELECT * FROM user where active is null";
		List<User> users= template.query(sql, new UserMapper());
		for(User u : users) {
			u.setRole(getRole(u.getRoleId()));
			
		}
		return users;
			}
	
	
	
}
