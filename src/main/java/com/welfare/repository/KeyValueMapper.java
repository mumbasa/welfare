package com.welfare.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.welfare.controller.KeyValue;

public class KeyValueMapper implements RowMapper<KeyValue>{

	@Override
	public KeyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		KeyValue kv = new KeyValue();
		kv.setKey(rs.getInt(1));
		kv.setValue(rs.getString(2));
		return kv;
	}

}
