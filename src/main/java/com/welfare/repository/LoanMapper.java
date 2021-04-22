package com.welfare.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.data.Loan;

public class LoanMapper implements RowMapper<Loan>{

	@Override
	public Loan mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Loan loan = new Loan();
		loan.setId(rs.getLong(1));
		loan.setAmount(rs.getDouble(4));
		loan.setApprovedAmount(rs.getDouble(14));
		loan.setDateApplied(rs.getString(5));
		loan.setTotalToPay(rs.getDouble(16));
		loan.setCollectionDate(rs.getString(11));
		loan.setDateApproved(rs.getString(7));
		loan.setLoanTypeId(rs.getInt(2));
		loan.setStaffId(rs.getLong(3));
		switch (rs.getString(8)) {
		case "P":
			loan.setStatus("Pending");
			break;

		case "N":
			loan.setStatus("Rejected");
			break;

		case "Y":
			loan.setStatus("Approved");
			break;

		default:
			break;
		}
		return loan;
	}
	

}
