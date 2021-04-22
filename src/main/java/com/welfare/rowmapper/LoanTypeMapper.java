package com.welfare.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.LoanType;

public class LoanTypeMapper implements RowMapper<LoanType>{

	@Override
	public LoanType mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		LoanType type = new LoanType();
		type.setId(rs.getLong(1));
		type.setType(rs.getString(2));
		type.setPercent(rs.getDouble(3));
		type.setYears(rs.getInt(5));
		return type;
	}
	

}
