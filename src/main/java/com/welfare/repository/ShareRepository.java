package com.welfare.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.welfare.data.Interest;
import com.welfare.data.Payments;
import com.welfare.data.Staff;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

@Repository
public class ShareRepository {

	@Autowired
	JdbcTemplate template;

	@Autowired
	StaffRepository repo;

	@Autowired
	MessageRepository messageRepository;

	public int savePayment(long id, String paymentType, String receipt, String date, double amount) {
		String sql = "INSERT INTO `welfare_payment`(`staff_number`, `amount`, `date`, `receipt_number`, `time_added`, `payment_type_id`,user) VALUES(?,?,?,?,now(),?,120)";
		int status = template.update(sql, id, amount, date, receipt, paymentType);
		String smsmessage = "You share fund has been credited with GH₵ " + amount + ". Your receipt number is "
				+ receipt + ".Thank you";
		String message = "You share fund has been credited with <b>GH₵ " + amount + "</b>\nYour receipt number is <b>"
				+ receipt + "</b>\nThank you";
		messageRepository.sendSms(smsmessage, repo.getStaff(id).getTelephone());
		if (status == 1) {
			if (repo.getStaffTelegram(id) > 0) {
				sendAlert(repo.getStaffTelegram(id), message);
			}
		}
		return status;
	}

	public int savePayment(long id, String paymentType, String date, double amount) {
		String sql = "INSERT INTO `welfare_payment`(`staff_number`, `amount`, `date`,  `time_added`, `payment_type_id`,user) VALUES(?,?,?,now(),?,120)";
		int status = template.update(sql, id, amount, date, paymentType);
		String message = "You share fund has been credited with <b>GH₵ " + amount + "</b>\nYour receipt number is <b>"
				+ "auto generated From Bot" + "</b>\nThank you";
		if (status == 1) {
			if (repo.getStaffTelegram(id) > 0) {
				sendAlert(repo.getStaffTelegram(id), message);
			}
		}
		return status;
	}

	public void readFile() {
		String[] months = { "2019-01-20", "2019-02-20", "2019-03-20", "2019-04-20", "2019-05-20", "2019-06-20",
				"2019-07-20", "2019-08-20", "2019-08-20", "2019-09-20", "2019-10-20", "2019-11-20", "2019-12-20" };
		Path path = Paths.get("/home/bryan/Downloads/Welfare Shares Andoh COMPOSITE DATA 2019.csv");

		try {

			List<String> lines = Files.readAllLines(path);
			for (int i = 2; i < 102; i++) {
				String[] data = (lines.get(i)).split(",");
				if (data[1] != null) {
					System.out.println();
					long id = Long.parseLong(data[1]);

					for (int a = 0; a < 12; a++) {

						try {
							double amount = data[7 + a].equals("") | data[7 + a].equals("-") | data[7 + a] == null ? 0
									: Double.parseDouble(data[7 + a]);
							if (amount > 0) {
								savePayment(id, "1", months[a], amount);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							// TODO: handle exception
						}
					}
					System.out.println();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int deletePayment(long id) {
		String sql = "DELETE FROM welfare_payment where transaction_id=?";
		return template.update(sql, id);
	}

	public int editPayment(long id, double amount) {
		String sql = "UPDATE welfare_payment set amount=? where transaction_id=?";
		return template.update(sql, amount, id);
	}
	
	
	public int editBeneficiary(long id,String name,String contact,String email,String dob, double portion) {
		String sql = "UPDATE beneficiary set name=?,contact=?,email=?,dob=?,percentage=? where id=?";
		return template.update(sql, name,contact,email,dob,portion,id);
	}

	public List<Payments> getSahrePayments() {
		List<Payments> pays = new ArrayList<Payments>();
		String sql = "SELECT * FROM welfare_payment w left join staff s on w.staff_number=s.staff_number  left join payments_type t on t.payment_id=w.payment_type_id;";
		SqlRowSet set = template.queryForRowSet(sql);
		while (set.next()) {
			Payments p = new Payments();
			p.setId(set.getLong(1));
			p.setAmount(set.getDouble(3));
			p.setDate(set.getString(4));
			p.setReceipt(set.getString(5));
			p.setType(set.getString(21));
			p.setStaffName(set.getString(12) + " " + set.getString(10));
			pays.add(p);
		}

		return pays;
	}

	public int addIntersest(long id, double amount, String year, Principal principal) {

		String sql = "INSERT INTO `company`.`interest` (`staff_number`, `amount`, `year`, `date_paid`, `user`, percent) VALUES (?,?,?,now(),?,?)";
		double percent = (amount / getAmountContributed(id)) * 100;

		int status = template.update(sql, id, amount, year, principal.getName(), percent);
		
		String message = "Your share fund has been credited with GH₵ " + amount + " as a "+percent+"% increment for the year" +year+" contribution Thank you";
		messageRepository.sendSms(message, repo.getStaff(id).getTelephone());
		if (status == 1) {
			if (repo.getStaffTelegram(id) > 0) {
				sendAlert(repo.getStaffTelegram(id), message);
			}
		}
		
		return status;

	}
	

	public int addBeneficiary(Principal principal, double portion, String relationship,String contact,String email,String name,String dob) {

		String sql = "INSERT INTO `company`.`beneficiary` (`name`, `email`, `contact`, `relationship`, `dob`, `percentage`, `staff_number`, `date_approved`) VALUES (?,?,?,?,?,?,?,now())";
		int status = template.update(sql, name, email, contact, relationship,dob,portion,principal.getName());
		return status;

	}
	
	
	public ChartValues getYearlyContributuib(Principal principal) {
		ChartValues cv = new ChartValues();
		
		String sql = "SELECT year(date),sum(amount) FROM company.welfare_payment  where staff_number=? group by year(date);";
		SqlRowSet set =  template.queryForRowSet(sql, principal.getName());
		while (set.next()) {
			cv.getLabels().add(set.getString(1));
			cv.getFigures().add(set.getDouble(2));
		}
		return cv;
	}

	
	public int deleteBeneficiary(long id) {
		String sql = " DELETE FROM beneficiary  where id=?";
		return template.update(sql, id);
	}
	public double getStaffBeneficiaryTotal(Principal principal) {
		double share =0;
		System.err.println(principal.getName());
		String sql ="SELECT sum(percentage) FROM company.beneficiary where staff_number=?";
		SqlRowSet set = template.queryForRowSet(sql,principal.getName());
		if(set.next()) {
			share = set.getDouble(1);
		}
		return share;
		
		
	}
	
	public List<Interest> getStaffInterests(long id) {
		Staff staff = repo.getStaffDetail(id);
		System.err.println(staff.getStaffId() + "-------id");
		List<Interest> beneficiaries = new ArrayList<Interest>();
		String sql = "SELECT * FROM company.interest where staff_number=?";
		SqlRowSet set = template.queryForRowSet(sql, staff.getStaffId());
		while (set.next()) {
			Interest b = new Interest();
			b.setId(set.getLong(1));
			b.setAmount(set.getDouble(3));
			b.setYear(set.getString(4));
			b.setDateApproved(set.getString(5));
			b.setPercent(set.getDouble(7));
			beneficiaries.add(b);

		}
		return beneficiaries;

	}

	public List<Interest> getStaffInterests(String id) {

		List<Interest> beneficiaries = new ArrayList<Interest>();
		String sql = "SELECT * FROM company.interest where staff_number=?";
		SqlRowSet set = template.queryForRowSet(sql, id);
		while (set.next()) {
			Interest b = new Interest();
			b.setId(set.getLong(1));
			b.setAmount(set.getDouble(3));
			b.setYear(set.getString(4));
			b.setDateApproved(set.getString(5));
			b.setPercent(set.getDouble(7));
			beneficiaries.add(b);

		}
		return beneficiaries;

	}

	public List<Payments> getSharePayments(String id) {
		System.err.println(id + " long");
		List<Payments> pays = new ArrayList<Payments>();
		String sql = "SELECT * FROM welfare_payment w left join staff s on w.staff_number=s.staff_number  left join payments_type t on t.payment_id=w.payment_type_id where s.staff_number=?";
		SqlRowSet set = template.queryForRowSet(sql, id);
		while (set.next()) {
			Payments p = new Payments();
			p.setId(set.getLong(1));
			p.setAmount(set.getDouble(3));
			p.setDate(set.getString(4));
			p.setReceipt(set.getString(5));
			p.setType(set.getString(21));
			p.setStaffName(set.getString(12) + " " + set.getString(10));
			pays.add(p);
		}

		return pays;
	}

	public List<Payments> getSharePayments(long id) {
		System.err.println(id + " long");
		List<Payments> pays = new ArrayList<Payments>();
		String sql = "SELECT * FROM welfare_payment w left join staff s on w.staff_number=s.staff_number  left join payments_type t on t.payment_id=w.payment_type_id where s.id=?";
		SqlRowSet set = template.queryForRowSet(sql, id);
		while (set.next()) {
			Payments p = new Payments();
			p.setId(set.getLong(1));
			p.setAmount(set.getDouble(3));
			p.setDate(set.getString(4));
			p.setReceipt(set.getString(5));
			p.setType(set.getString(21));
			p.setStaffName(set.getString(12) + " " + set.getString(10));
			pays.add(p);
		}

		return pays;
	}

	public File getShareStatment(long id) {
		List<Payments> getSahrePayments = getSharePayments(id);
		File file = new File("/home/bryan/Documents/" + id + "-Share.csv");
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write("Date,Amount,Type,ReceiptNumber\n");
			for (Payments p : getSahrePayments) {
				writer.append(p.getDate() + "," + p.getAmount() + "," + p.getType() + "," + p.getReceipt() + "\n");

			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;

	}

	public double getAmountContributed(long id) {
		double amount = 0;
		String sql = "SELECT (SELECT ifnull(sum(amount),0) from welfare_payment where staff_number=?) +(SELECT  ifnull(sum(amount),0) FROM company.interest where staff_number=?);";
		SqlRowSet set = template.queryForRowSet(sql, id, id);
		if (set.next()) {
			amount = set.getDouble(1);
		}
		return amount;
	}

	public static void sendAlert(long id, String message) {
		System.err.println(message);
		HttpResponse<JsonNode> d = Unirest
				.get("https://api.telegram.org/bot853503019:AAEe5h9KLkInNP5yLemDQ6Zd_QMbVs7oexY/sendMessage")
				.queryString("chat_id", id).queryString("text", message).queryString("parse_mode", "html").asJson();
		System.err.println(d.getStatus() + "ttt");

	}

	public int sendMessageStaffId(long id, String message) {
		System.err.println(message);
		HttpResponse<JsonNode> d = Unirest
				.get("https://api.telegram.org/bot853503019:AAEe5h9KLkInNP5yLemDQ6Zd_QMbVs7oexY/sendMessage")
				.queryString("chat_id", repo.getStaff(id).getTelegramId()).queryString("text", message)
				.queryString("parse_mode", "html").asJson();
		return (d.getStatus());

	}

}
