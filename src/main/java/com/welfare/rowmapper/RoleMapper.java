package com.welfare.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.Role;

public class RoleMapper implements RowMapper<Role>{

	@Override
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Role role = new Role();
		role.setId(rs.getLong(1));
		role.setRole(rs.getString(2));
		return role;
	}
	
	

}
