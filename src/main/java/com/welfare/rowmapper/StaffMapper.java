package com.welfare.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.Staff;

public class StaffMapper implements RowMapper<Staff>{

	@Override
	public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Staff staff = new Staff();
		staff.setId(rs.getLong(1));
		staff.setName(rs.getString(2));
		staff.setEmail(rs.getString(3));
		staff.setPicture(rs.getString(6));
		staff.setStaffId(rs.getLong(7));
		staff.setTelegramId(rs.getLong(10));
		staff.setTelephone(rs.getString(9));
		return staff;
	}

}
