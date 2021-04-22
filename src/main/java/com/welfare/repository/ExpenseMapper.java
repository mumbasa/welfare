package com.welfare.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.Expense;

public class ExpenseMapper implements RowMapper<Expense>{

	@Override
	public Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Expense exp = new Expense();
		exp.setAmount(rs.getDouble(5));
		exp.setId(rs.getLong(1));
		exp.setDate(rs.getString(3));
		exp.setReceiver(rs.getString(2));
		exp.setExpense(rs.getString(6));
		exp.setNaration(rs.getString(7));
		exp.setMode(rs.getString(9));
		return exp;
	}

	
}
