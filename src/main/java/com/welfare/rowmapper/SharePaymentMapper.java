package com.welfare.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.Payments;

public class SharePaymentMapper implements RowMapper<Payments>{

	@Override
	public Payments mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Payments pay = new Payments();
		pay.setAmount(rs.getDouble("amount"));
		pay.setStaffId(rs.getLong("staff_number"));
		pay.setId(rs.getLong(1));
		pay.setDate(rs.getString("date"));
		pay.setReceipt(rs.getString("receipt_number"));
		pay.setType(rs.getString(8));
		return pay;
	}

	
}
