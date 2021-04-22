package com.welfare.repository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.welfare.controller.KeyValue;
import com.welfare.data.Beneficiary;
import com.welfare.data.Department;
import com.welfare.data.Staff;
import com.welfare.rowmapper.DeptMapper;
import com.welfare.rowmapper.StaffMapper;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

@Repository
public class StaffRepository {

	@Autowired
	JdbcTemplate template;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MessageRepository messageRepository;

	public List<Staff> getStaff() {
		String sql = "SELECT * FROM staff";
		return template.query(sql, new StaffMapper());

	}

	public List<Staff> getActiveStaff() {
		String sql = "SELECT * FROM staff where active !='N' or active is null";
		return template.query(sql, new StaffMapper());

	}

	public List<Department> getDepartment() {
		String sql = "SELECT * FROM department;";
		return template.query(sql, new DeptMapper());

	}

	public List<KeyValue> getExpenses() {
		String sql = "SELECT * FROM expense;";
		return template.query(sql, new KeyValueMapper());

	}

	public List<Double> staffStatus(long id) {
		Staff staff = getStaffDetail(id);

		List<Double> date = new ArrayList<Double>();
		String sql = "SELECT (SELECT sum(total_to_pay) FROM company.loan_application where staff_number=?),(SELECT sum(amount) FROM company.loan_payment where staff_number=?),(SELECT sum(amount)+(SELECT balance FROM company.welfare_state  ws where ws.staff_number=?) FROM company.welfare_payment where staff_number=?)";
		SqlRowSet set = template.queryForRowSet(sql, staff.getStaffId(),  staff.getStaffId(),  staff.getStaffId(),  staff.getStaffId());
		if (set.next()) {
			date.add(set.getDouble(1));
			date.add(set.getDouble(2));
			date.add(set.getDouble(3));
		}
		return date;
	}
	
	public List<Double> staffStatus(Principal id) {
	

		List<Double> date = new ArrayList<Double>();
		String sql = "SELECT (SELECT sum(total_to_pay) FROM company.loan_application where staff_number=?),(SELECT sum(amount) FROM company.loan_payment where staff_number=?),(SELECT sum(amount)+(SELECT balance FROM company.welfare_state  ws where ws.staff_number=?) FROM company.welfare_payment where staff_number=?)";
		SqlRowSet set = template.queryForRowSet(sql, id.getName(),  id.getName(),   id.getName(),  id.getName());
		if (set.next()) {
			date.add(set.getDouble(1));
			date.add(set.getDouble(2));
			date.add(set.getDouble(3));
		}
		return date;
	}

	public List<Double> staffStatusByTelegram(long id) {
		List<Double> date = new ArrayList<Double>();
		String sql = "SELECT (SELECT sum(total_to_pay) FROM company.loan_application where staff_number=?),(SELECT sum(amount) FROM company.loan_payment where staff_number=?),(SELECT sum(amount) FROM company.welfare_payment where staff_number=?);";
		SqlRowSet set = template.queryForRowSet(sql, id, id, id);
		if (set.next()) {
			date.add(set.getDouble(1));
			date.add(set.getDouble(2));
			date.add(set.getDouble(3));
		}
		return date;
	}

	public Staff getStaff(long id) {
		Staff staff = null;
		String sql = "SELECT * FROM staff where staff_number=?";
		staff = template.queryForObject(sql, new StaffMapper(), id);
		return staff;

	}
	
	public Staff getStaff(Principal principal) {
		Staff staff = null;
		String sql = "SELECT * FROM staff where staff_number=?";
		staff = template.queryForObject(sql, new StaffMapper(), principal.getName());
		return staff;

	}

	
	public List<Beneficiary> getStaffBeneficaries(long id) {
		Staff staff = getStaffDetail(id);
		List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
		String sql = "SELECT * FROM company.beneficiary where staff_number =?";
		SqlRowSet set = template.queryForRowSet(sql, staff.getStaffId());
		while(set.next()) {
			Beneficiary b = new Beneficiary();
			b.setId(set.getLong(1));
			b.setName(set.getString(2));
			b.setContact(set.getString(4));
			b.setEmail(set.getString(3));
			b.setDob(set.getString(6));
			b.setDateApproved(set.getString(10));
			b.setPercentage(set.getDouble(7));
			beneficiaries.add(b);
			
		}
		return beneficiaries;

	}
	
	public List<Beneficiary> getStaffBeneficaries(String id) {

		List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
		String sql = "SELECT * FROM company.beneficiary where staff_number =?";
		SqlRowSet set = template.queryForRowSet(sql, id);
		while(set.next()) {
			Beneficiary b = new Beneficiary();
			b.setId(set.getLong(1));
			b.setName(set.getString(2));
			b.setContact(set.getString(4));
			b.setEmail(set.getString(3));
			b.setDob(set.getString(6));
			b.setDateApproved(set.getString(10));
			b.setPercentage(set.getDouble(7));
			beneficiaries.add(b);
			
		}
		return beneficiaries;

	}

	
	public Staff getStaffDetail(long id) {
		Staff staff = null;
		String sql = "SELECT * FROM staff where id=?";
		staff = template.queryForObject(sql, new StaffMapper(), id);
		return staff;

	}

	public Staff getStaffDetail(Principal id) {
		Staff staff = null;
		String sql = "SELECT * FROM staff where staff_number=?";
		staff = template.queryForObject(sql, new StaffMapper(),id.getName());
		return staff;

	}

	
	public long getStaffTelegram(long id) {
		long status = 0;
		String sql = "SELECT telegram FROM staff where staff_number=?";
		SqlRowSet set = template.queryForRowSet(sql, id);
		if (set.next()) {
			status = set.getLong(1);
		}
		return status;

	}

	public Staff getStaffByTelegram(long id) {
		Staff staff = null;
		String sql = "SELECT * FROM staff where telegram=?";
		staff = template.queryForObject(sql, new StaffMapper(), id);
		return staff;

	}

	public Staff getStaffByTelegram(String id) {
		Staff staff = null;
		String sql = "SELECT * FROM staff where staff_number=?";
		staff = template.queryForObject(sql, new StaffMapper(), id);
		return staff;

	}

	public int telegramRegister(long id, long tid) {
		String sql = "UPDATE staff set telegram=? where staff_number=?";
		return template.update(sql, tid, id);
	}

	public int deactivate(long id) {
		String sql = "UPDATE staff set active='N' where staff_number=?";
		return template.update(sql, id);
	}
	public int delete(long id) {
		String sql = "DELETE FROM staff  where id=?";
		return template.update(sql, id);
	}
	
	public int activate(long id) {
		String sql = "UPDATE staff set active='Y' where staff_number=?";
		return template.update(sql, id);
	}

	public int uploadPicture(String picture, long tid) {
		String sql = "UPDATE staff set picture=? where telegram=?";
		return template.update(sql, picture, tid);

	}

	@Transactional
	public int addStaff(String name, String staffId, int rank, String contact) {
		String code = RandomStringUtils.randomAlphanumeric(6);

		String sql = "INSERT INTO `staff`(`last_name`,`staff_number`,`active`,`telephone`) VALUES(?,?,'Y',?)";
		int status = template.update(sql, name, staffId, contact);
		userRepository.addUser(staffId, name, code, rank);
		String message = "You have been granted access to the IIR Welfare Platform on http://104.236.119.236:8081/login Your username is "+staffId+" and your password is "+code+" Thank you";
		messageRepository.sendSms(message, contact);
		return status;

	}

	public int telegramRegister(long id, String telephone, String department) {
		String sql = "UPDATE staff set telephone=?,dept_id=? where staff_number=?";
		return template.update(sql, telephone, department, id);

	}
	
	public int updateStaff(long id,String name, String telephone, String email,String controller) {
		String sql = "UPDATE staff set last_name=?,telephone=?,email=?,staff_number=?,picture=? where id=?";
		return template.update(sql, name,telephone, email,controller, id);

	}
	
	public int updateStaffs(long id,String name, String telephone, String email,String picture) {
		String sql = "UPDATE staff set last_name=?,telephone=?,email=?,picture=? where id=?";
		return template.update(sql, name,telephone, email,picture, id);

	}

	public void download(String fileId, long chatid) {
		HttpResponse<JsonNode> d = Unirest
				.get("https://api.telegram.org/bot853503019:AAEe5h9KLkInNP5yLemDQ6Zd_QMbVs7oexY/getFile")
				.queryString("file_id", fileId).asJson();
		String path = (d.getBody().getObject().getJSONObject("result").getString("file_path"));
		String url = "https://api.telegram.org/file/bot853503019:AAEe5h9KLkInNP5yLemDQ6Zd_QMbVs7oexY/" + path;
		String location = "/var/www/pics/" + chatid + ".jpg";
		try {
			// connectionTimeout, readTimeout = 10 seconds
			FileUtils.copyURLToFile(new URL(url), new File(location), 10000, 10000);

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(url);

	}
}
