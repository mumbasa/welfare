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

import com.welfare.data.Expense;
import com.welfare.data.Loan;
import com.welfare.data.LoanData;
import com.welfare.data.LoanType;
import com.welfare.data.Payments;
import com.welfare.rowmapper.LoanTypeMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

@Repository
public class LoanRepository {

	@Autowired
	JdbcTemplate template;

	@Autowired
	StaffRepository staffRepo;

	@Autowired
	ShareRepository shareRepo;

	@Autowired
	MessageRepository messageRepository;

	public List<LoanType> getLoanTypes() {
		String sql = "SELECT * FROM loan_type";
		return template.query(sql, new LoanTypeMapper());

	}

	public LoanType getLoanType(long id) {
		String sql = "SELECT * FROM loan_type where loan_id=?";
		return template.queryForObject(sql, new LoanTypeMapper(), id);

	}

	public List<Loan> getLoans() {
		String sql = "SELECT * FROM loan_application";
		List<Loan> loans = template.query(sql, new LoanMapper());
		for (Loan l : loans) {
			l.setStaff(staffRepo.getStaff(l.getStaffId()));

		}
		return loans;
	}

	public List<Loan> getLoans(long staffId) {
		String sql = "SELECT * FROM loan_application where staff_number=?";
		List<Loan> loans = template.query(sql, new LoanMapper(), staffId);
		for (Loan l : loans) {
			l.setStaff(staffRepo.getStaff(l.getStaffId()));

		}
		return loans;
	}

	public List<Loan> getStaffAprrovedLoans(long id) {
		String sql = "SELECT * FROM loan_application l join staff s  on l.staff_number=s.staff_number where is_approved='Y' and s.id=?";
		List<Loan> loans = new ArrayList<Loan>();

		SqlRowSet rs = template.queryForRowSet(sql, id);
		while (rs.next()) {
			Loan loan = new Loan();
			loan.setId(rs.getLong(1));
			loan.setAmount(rs.getDouble(4));
			loan.setApprovedAmount(rs.getDouble(14));
			loan.setDateApplied(rs.getString(13));
			loan.setTotalToPay(rs.getDouble(16));
			loan.setCollectionDate(rs.getString(11));
			loan.setDateApproved(rs.getString(7));
			loan.setArrears(getStaffIndebtness(rs.getLong(3)));
			loan.setLoanTypeId(rs.getInt(2));
			loan.setType(getLoanType(rs.getInt(2)));
			loan.setShares(getStaffShares(rs.getInt(3)));
			loan.setStaffId(rs.getLong(3));
			loan.setStaffName(rs.getString(20) + " " + rs.getString(18));
			loans.add(loan);
		}
		return loans;
	}

	public List<Loan> getLoansPending() {
		String sql = "SELECT * FROM loan_application l join staff s  on l.staff_number=s.staff_number where is_approved='P'";
		List<Loan> loans = new ArrayList<Loan>();

		SqlRowSet rs = template.queryForRowSet(sql);
		while (rs.next()) {
			Loan loan = new Loan();
			loan.setId(rs.getLong(1));
			loan.setAmount(rs.getDouble(4));
			loan.setApprovedAmount(rs.getDouble(14));
			loan.setDateApplied(rs.getString(13));
			loan.setTotalToPay(rs.getDouble(16));
			loan.setCollectionDate(rs.getString(11));
			loan.setDateApproved(rs.getString(7));
			loan.setArrears(getStaffIndebtness(rs.getLong(3)));
			loan.setLoanTypeId(rs.getInt(2));
			loan.setType(getLoanType(rs.getInt(2)));
			loan.setShares(getStaffShares(rs.getInt(3)));
			loan.setStaffId(rs.getLong(3));
			loan.setStaffName(rs.getString(20) + " " + rs.getString(18));
			loans.add(loan);
		}
		return loans;
	}

	public Loan getLoan(long id) {
		String sql = "SELECT * FROM loan_application l left join staff s  on l.staff_number=s.staff_number left join loan_type as t on t.loan_id=l.loan_type_id where loan_application_id=?";
		Loan loan = null;

		SqlRowSet rs = template.queryForRowSet(sql, id);
		while (rs.next()) {
			loan = new Loan();
			loan.setId(rs.getLong(1));
			loan.setAmount(rs.getDouble(4));
			loan.setApprovedAmount(rs.getDouble(14));
			loan.setDateApplied(rs.getString(13));
			loan.setTotalToPay(rs.getDouble(16));
			loan.setCollectionDate(rs.getString(11));
			loan.setDateApproved(rs.getString(7));
			loan.setArrears(getStaffIndebtness(rs.getLong(3)));
			loan.setLoanTypeId(rs.getInt(2));
			loan.setType(getLoanType(rs.getInt(2)));
			loan.setShares(getStaffShares(rs.getInt(3)));
			loan.setStaffId(rs.getLong(3));
			loan.setYear(rs.getDouble(32));
			loan.setInterestRate(rs.getDouble(30));
			loan.setStaffName(rs.getString(20) + " " + rs.getString(18));
		}
		return loan;
	}

	public List<Loan> getLoansApprovedNotCollected() {
		String sql = "SELECT * FROM loan_application l join staff s  on l.staff_number=s.staff_number left join loan_type as lt on l.loan_type_id=lt.loan_id where is_approved='Y' and collection_date is null;";
		List<Loan> loans = new ArrayList<Loan>();

		SqlRowSet rs = template.queryForRowSet(sql);
		while (rs.next()) {
			Loan loan = new Loan();
			loan.setId(rs.getLong(1));
			loan.setAmount(rs.getDouble(4));
			loan.setApprovedAmount(rs.getDouble(14));
			loan.setDateApplied(rs.getString(5));
			loan.setTotalToPay(rs.getDouble(16));
			loan.setCollectionDate(rs.getString(11));
			loan.setDateApproved(rs.getString(7));
			loan.setLoanTypeId(rs.getInt(2));
			loan.setType(getLoanType(rs.getInt(2)));
			loan.setStaffId(rs.getLong(3));
			loan.setStaffName(rs.getString(20) + " " + rs.getString(18));
			loans.add(loan);
		}
		return loans;
	}

	public List<Loan> getStaffLoans(String id) {
		String sql = "SELECT * FROM loan_application l join staff s  on l.staff_number=s.staff_number left join loan_type as lt on l.loan_type_id=lt.loan_id where s.staff_number=?";
		List<Loan> loans = new ArrayList<Loan>();

		SqlRowSet rs = template.queryForRowSet(sql, id);
		while (rs.next()) {
			Loan loan = new Loan();
			loan.setId(rs.getLong(1));
			loan.setAmount(rs.getDouble(4));
			loan.setApprovedAmount(rs.getDouble(14));
			loan.setDateApplied(rs.getString(5));
			loan.setTotalToPay(rs.getDouble(16));
			loan.setCollectionDate(rs.getString(11));
			loan.setDateApproved(rs.getString(7));
			loan.setLoanTypeId(rs.getInt(2));
			loan.setType(getLoanType(rs.getInt(2)));
			loan.setStaffId(rs.getLong(3));
			loan.setStaffName(rs.getString(20) + " " + rs.getString(18));
			loans.add(loan);
		}
		return loans;
	}

	public int deletePayment(long id) {
		System.err.println(id);

		String sql = "DELETE FROM loan_payment where loan_payment_id=?";
		return template.update(sql, id);
	}

	public int editPayment(long id, double amount) {
		System.err.println(id);
		String sql = "UPDATE loan_payment set amount=? where loan_payment_id=?";
		return template.update(sql, amount, id);
	}

	public int collectCheck(long id) {
		Loan loan = getLoan(id);
		String sql = "UPDATE loan_application set collection_date=now() where loan_application_id=?";
		String message = "Hello " + loan.getStaffName() + " You have collected your cheque of GH₵ "
				+ loan.getApprovedAmount() + ". Your account has been debited";
		int status = template.update(sql, id);
		shareRepo.sendMessageStaffId(loan.getStaffId(), message);
		messageRepository.sendSms(message, staffRepo.getStaff(loan.getStaffId()).getTelephone());
		return status;
	}

	public LoanData computeLoan(int id, double amount, Principal principal) {
		LoanType type = getLoanType(id);
		double compountAmount = compountInterest(amount, type.getPercent(), type.getYears());
		double interest = compountAmount - amount;
		double debt = getStaffIndebtness(principal) + interest;
		double montlyInstallation = compountAmount / (12 * type.getYears());
		LoanData data = new LoanData();
		data.setAmountToPay(compountAmount);
		data.setInterest(interest);
		data.setDebts(debt);
		data.setMonthlyInstallment(montlyInstallation);
		return data;
	}

	public int rejectLoan(long id) {
		Loan loan = getLoan(id);
		String sql = "UPDATE loan_application set is_approved='N',decision_date=now() where loan_application_id=?";
		int stat = template.update(sql, id);
		String message = "Hello " + loan.getStaffName() + " the loan " + loan.getAmount() + " you applied on "
				+ loan.getDateApplied()
				+ " has been rejected. You can try again next time or increase your loan repayment and share fund. Thank you";

		shareRepo.sendMessageStaffId(loan.getStaffId(), message);
		messageRepository.sendSms(message, staffRepo.getStaff(loan.getStaffId()).getTelephone());
		return stat;
	}

	public int approveLoan(long id, double amount) {
		Loan loan = getLoan(id);
		loan.setApprovedAmount(amount);
		loan.setTotalToPay(compountInterest(loan));
		String sql = "UPDATE loan_application set is_approved='Y',decision_date=now(),approved_amount=?,total_to_pay=? where loan_application_id=?";
		int stat = template.update(sql, amount, loan.getTotalToPay(), id);
		String message = "Hello " + loan.getStaffName() + " the loan GH₵ " + loan.getAmount() + " you applied on "
				+ loan.getDateApplied() + " has been approved. Your approved amount is GH₵" + loan.getApprovedAmount()
				+ " and you have to pay " + loan.getTotalToPay() + ". You will pay "
				+ Math.round(loan.getTotalToPay() / (loan.getYear() * 12))
				+ " every month. You can take the cheque from the welfare office Thank you";
		shareRepo.sendMessageStaffId(loan.getStaffId(), message);
		messageRepository.sendSms(message, staffRepo.getStaff(loan.getStaffId()).getTelephone());
		return stat;
	}

	public double simpleInterest(Loan loan) {
		return loan.getApprovedAmount() + ((loan.getApprovedAmount() * loan.getYear() * loan.getInterestRate()) / 100);

	}

	public double compountInterest(Loan loan) {
		return loan.getApprovedAmount() * (Math.pow(1 + (loan.getInterestRate() / 100), (loan.getYear())));
	}

	public double compountInterest(double amount, double interest, int years) {
		return amount * (Math.pow(1 + (interest / 100), years));
	}

	public List<Payments> getLoanPayments() {
		String sql = "SELECT * FROM loan_payment w left join staff s on w.staff_number=s.staff_number left join payments_type t on t.payment_id=w.payment_type_id";
		List<Payments> pays = new ArrayList<Payments>();
		SqlRowSet set = template.queryForRowSet(sql);
		while (set.next()) {
			Payments p = new Payments();
			p.setId(set.getLong(1));
			p.setAmount(set.getDouble(4));
			p.setDate(set.getString(5));
			p.setReceipt(set.getString(6));
			p.setType(set.getString(22));
			p.setStaffName(set.getString(12) + " " + set.getString(10));
			pays.add(p);
		}
		return pays;
	}

	public List<Payments> getStaffLoanPayments(long id) {
		String sql = "SELECT * FROM loan_payment w left join staff s on w.staff_number=s.staff_number left join payments_type t on t.payment_id=w.payment_type_id where s.id=?";
		List<Payments> pays = new ArrayList<Payments>();
		SqlRowSet set = template.queryForRowSet(sql, id);
		while (set.next()) {
			Payments p = new Payments();
			p.setId(set.getLong(1));
			p.setAmount(set.getDouble(4));
			p.setDate(set.getString(5));
			p.setReceipt(set.getString(6));
			p.setType(set.getString(22));
			p.setStaffName(set.getString(12) + " " + set.getString(10));
			pays.add(p);
		}
		return pays;
	}

	public List<Payments> getStaffLoanPayments(String id) {
		String sql = "SELECT * FROM loan_payment w left join staff s on w.staff_number=s.staff_number left join payments_type t on t.payment_id=w.payment_type_id where s.staff_number=?";
		List<Payments> pays = new ArrayList<Payments>();
		SqlRowSet set = template.queryForRowSet(sql, id);
		while (set.next()) {
			Payments p = new Payments();
			p.setId(set.getLong(1));
			p.setAmount(set.getDouble(4));
			p.setDate(set.getString(5));
			p.setReceipt(set.getString(6));
			p.setType(set.getString(22));
			p.setStaffName(set.getString(12) + " " + set.getString(10));
			pays.add(p);
		}
		return pays;
	}

	public double getStaffIndebtness(long id) {
		double data = 0;
		String sql = "SELECT (SELECT sum(total_to_pay)  FROM company.loan_application where staff_number=?) -(SELECT sum(amount)  FROM company.loan_payment where staff_number=?);";
		SqlRowSet set = template.queryForRowSet(sql, id, id);
		if (set.next()) {
			data = set.getDouble(1);
		}
		return data;
	}

	public double getStaffIndebtness(Principal principal) {
		double debt = 0;
		String sql = "SELECT (SELECT sum(total_to_pay)  FROM company.loan_application where staff_number=?) -(SELECT sum(amount)  FROM company.loan_payment where staff_number=?);";
		SqlRowSet set = template.queryForRowSet(sql, principal.getName(), principal.getName());
		if (set.next()) {
			debt = set.getDouble(1);
		}
		return debt;
	}

	public double getStaffShares(long id) {
		System.err.println(id + "00000");
		String sql = "SELECT sum(amount) FROM company.welfare_payment where staff_number=?";
		return template.queryForObject(sql, double.class, id);

	}

	public File getLoanstatment(long id) {
		List<Payments> getSahrePayments = getStaffLoanPayments(id);
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
		} catch (IOException e) { // TODO
			e.printStackTrace();
		}

		return file;

	}

	public void readFile() {
		String[] months = { "2019-01-20", "2019-02-20", "2019-03-20", "2019-04-20", "2019-05-20", "2019-06-20",
				"2019-07-20", "2019-08-20", "2019-08-20", "2019-09-20", "2019-10-20", "2019-11-20", "2019-12-20" };
		Path path = Paths.get("/home/bryan/Downloads/loans.csv");

		try {

			List<String> lines = Files.readAllLines(path);
			for (int i = 1; i < 102; i++) {
				String[] data = (lines.get(i)).replaceAll(",", "").replaceAll("\\(", "-").replaceAll("\\)", "")
						.split(";");
				if (data[1] != null) {
					System.out.println();
					long id = Long.parseLong(data[1]);
					double grantedLoan = data[5].equals("") ? 0 : Double.parseDouble(data[5]);
					if (grantedLoan > 0) {
						grantedLoan(id, grantedLoan, 3);
					}

					for (int a = 0; a < 12; a++) {

						try {

							double amount = data[6 + a].equals("") | data[6 + a].equals("-") | data[6 + a] == null ? 0
									: Double.parseDouble(data[6 + a]);

							if (amount > 0) {
								saveLoan(id, "1", "", months[a], amount);
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

	public void readFile2() {

		Path path = Paths.get("/home/bryan/Downloads/taken.csv");

		try {

			List<String> lines = Files.readAllLines(path);
			for (int i = 1; i < 101; i++) {
				String[] data = (lines.get(i)).replaceAll(",", "").replaceAll("\\(", "-").replaceAll("'", "-")
						.replaceAll("\\)", "").split(";");
				if (data[0] != null) {
					System.out.println(lines.get(i));
					long id = Long.parseLong(data[4]);

					for (int a = 0; a < 12; a++) {

						try {
							double amount = data[5 + a].equals("") | data[5 + a].equals("-") | data[5 + a] == null ? 0
									: Double.parseDouble(data[5 + a]);

							if (amount > 0) {
								grantedLoan(id, amount, 1);
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

	public File getLoansFile(long id) {
		List<Loan> loans = getStaffAprrovedLoans(id);
		File file = new File("/home/bryan/Documents/" + id + "-Loans.csv");
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write("Date,Amount Approved,Amount To Pay,Interest\n");
			for (Loan p : loans) {
				writer.append(p.getDateApproved() + "," + p.getApprovedAmount() + "," + p.getTotalToPay() + ","
						+ ((p.getTotalToPay()) - p.getApprovedAmount()) + "\n");

			}
			writer.flush();
			writer.close();
		} catch (IOException e) { // TODO
			e.printStackTrace();
		}

		return file;

	}

	public List<Double> getLoansummary() {
		List<Double> t = new ArrayList<>();
		String sql = "SELECT (SELECT sum(amount) FROM company.loan_payment),(SELECT sum(approved_amount) FROM company.loan_application),(SELECT sum(total_to_pay) FROM company.loan_application),(SELECT sum(total_to_pay) FROM company.loan_application)-(SELECT sum(amount) FROM company.loan_payment),(SELECT sum(total_to_pay) FROM company.loan_application)-(SELECT sum(approved_amount) FROM company.loan_application);";
		SqlRowSet set = template.queryForRowSet(sql);
		if (set.next()) {
			t.add(set.getDouble(1));
			t.add(set.getDouble(2));
			t.add(set.getDouble(3));
			t.add(set.getDouble(4));
			t.add(set.getDouble(5));
		}
		return t;
	}

	public List<Expense> getEXpenditure() {
		String sql = "SELECT * FROM company.expenditure e left join payments_type t on e.type= t.payment_id";
		List<Expense> pays = template.query(sql, new ExpenseMapper());
		return pays;
	}

	public int saveLoan(long id, String paymentType, String receipt, String date, double amount) {

		String sql = "INSERT INTO `loan_payment`(`staff_number`, `amount`, `date`, `receipt_number`, `time_added`, `payment_type_id`,user) VALUES(?,?,?,?,now(),?,120)";
		int status = template.update(sql, id, amount, date, receipt, paymentType);

		String message = "You loan payments has been credited with <b>GH₵ " + amount
				+ "</b>\nYour receipt number is <b>" + receipt + "</b>\nThank you";

		messageRepository.sendSms(message, staffRepo.getStaff(id).getTelephone());
		if (status == 1) {
			if (staffRepo.getStaffTelegram(id) > 0) {
				sendAlert(staffRepo.getStaffTelegram(id), message);
			}
		}
		return status;
	}

	public int saveExpenditure(String paymentType, String naration, String date, double amount, String name) {
		String sql = "INSERT INTO `company`.`expenditure`(`name`,`date_paid`,`type`,`amunt`,naration)VALUES(?,?,?,?,?);";
		return template.update(sql, name, date, paymentType, amount, naration);
	}

	public int updateEexpenditure(long id, String naration, double amount) {
		String sql = " UPDATE expenditure set `amunt`=?,naration=? where id=?";
		return template.update(sql, amount, naration, id);
	}

	public int deleteExpenditure(long id) {
		String sql = " DELETE FROM expenditure  where id=?";
		return template.update(sql, id);
	}

	public int grantedLoan(long id, double amount, int loanid) {
		String sql = "INSERT INTO `company`.`loan_application` (`loan_type_id`, `staff_number`, `amount`, `is_approved`, `entry_date`, total_to_pay) VALUES(?,?,?,'Y',curdate(),?)";
		return template.update(sql, loanid, id, amount, amount);

	}

	public int saveApplication(Principal id, double amount, int loanid) {
		String sql = "INSERT INTO `company`.`loan_application` (`loan_type_id`, `staff_number`, `amount`, `is_approved`, `entry_date`) VALUES(?,?,?,'P',curdate())";
		return template.update(sql, loanid, id.getName(), amount);

	}

	public static void sendAlert(long id, String message) {
		System.err.println(message);
		HttpResponse<JsonNode> d = Unirest
				.get("https://api.telegram.org/bot853503019:AAEe5h9KLkInNP5yLemDQ6Zd_QMbVs7oexY/sendMessage")
				.queryString("chat_id", id).queryString("text", message).queryString("parse_mode", "html").asJson();
		System.err.println(d.getStatus() + "ttt");

	}
}
