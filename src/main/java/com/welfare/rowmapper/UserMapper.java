package com.welfare.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.User;

public class UserMapper implements RowMapper<User>{

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId(rs.getLong(1));
		user.setUsername(rs.getString(3));
		user.setName(rs.getString(2));
		user.setRoleId(rs.getInt(5));
		return user;
	}

}
