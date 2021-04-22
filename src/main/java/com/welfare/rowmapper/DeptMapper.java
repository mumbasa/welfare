package com.welfare.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.Department;

public class DeptMapper implements RowMapper<Department>{

	@Override
	public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Department v = new Department();
		v.setId(rs.getLong(1));
		v.setDepartment(rs.getString(2));
		return v;
	}
	

}
