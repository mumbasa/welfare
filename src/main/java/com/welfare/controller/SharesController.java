package com.welfare.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.welfare.data.Staff;
import com.welfare.repository.ChartValues;
import com.welfare.repository.LoanRepository;
import com.welfare.repository.ShareRepository;
import com.welfare.repository.StaffRepository;

@Controller
public class SharesController {

	@Autowired
	StaffRepository staffRepo;

	@Autowired
	ShareRepository shareRepo;

	@Autowired
	LoanRepository loanRepo;

	@RequestMapping("/home")
	public String home() {

		return "index";
	}

//
	@RequestMapping("/admin/shares/payments")
	public String homes(Model model) {
		model.addAttribute("staff", staffRepo.getStaff());
		return "pages/addshare";
	}

	@RequestMapping("/admin/shares/batch")
	public String shareBatch(Model model) {
		model.addAttribute("staff", staffRepo.getStaff());
		return "admin/sharebatch";
	}

	@RequestMapping("/dashboard")
	public String dash(Model model, Principal principal) {
		Staff staff = null;
		try {
			staff = staffRepo.getStaffDetail(principal);
			ChartValues cv = shareRepo.getYearlyContributuib(principal);
			List<Double> summary = staffRepo.staffStatus(principal);
			model.addAttribute("shares", shareRepo.getSharePayments(principal.getName()));
			model.addAttribute("staff", staff);
			model.addAttribute("beneficiaries", staffRepo.getStaffBeneficaries(principal.getName()).size());
			model.addAttribute("loanstotal", summary.get(0));
			model.addAttribute("loanspaid", summary.get(1));
			model.addAttribute("welfare", summary.get(2));
			model.addAttribute("benportion", 100 - shareRepo.getStaffBeneficiaryTotal(principal));
			model.addAttribute("contyears", cv.getLabels());
			model.addAttribute("conts", cv.getFigures());

			return "pages/dashhboard";

		} catch (Exception e) {
			return "auth-login";
			// TODO: handle exception
		}

	}

	//
	@RequestMapping("/admin/shares/data")
	public String sharesData(Model model) {
		model.addAttribute("payments", shareRepo.getSahrePayments());
		return "pages/payments";
	}

	@RequestMapping("/my/shares/data")
	public String myShareData(Model model, Principal principal) {
		model.addAttribute("payments", shareRepo.getSharePayments(principal.getName()));
		return "my/payments";
	}

	@RequestMapping("/my/beneficiaries/")
	public String myBeneficiaries(Model model, Principal principal) {
		model.addAttribute("beneficiaries", staffRepo.getStaffBeneficaries(principal.getName()));
		model.addAttribute("benportion", shareRepo.getStaffBeneficiaryTotal(principal));

		return "my/beneficiaries";
	}

	@RequestMapping("/my/interests/")
	public String myInterest(Model model, Principal principal) {
		model.addAttribute("interests", shareRepo.getStaffInterests(principal.getName()));
		return "my/interests";
	}

	@RequestMapping("/my/loanpayments/")
	public String myLoanPayments(Model model, Principal principal) {
		model.addAttribute("loanpayment", loanRepo.getStaffLoanPayments(principal.getName()));
		return "my/loanpayments";
	}

	@RequestMapping("/my/loan/applications")
	public String myLoanApplications(Model model, Principal principal) {
		model.addAttribute("loanpayment", loanRepo.getStaffLoans(principal.getName()));
		return "my/applications";
	}

	//
	@RequestMapping("/admin/add/interest")
	public String addInterst(Model model) {
		model.addAttribute("staff", staffRepo.getStaff());

		return "pages/addinterest";
	}

	@RequestMapping("/admin/expenditure/data")
	public String expenditureD(Model model) {
		model.addAttribute("exp", loanRepo.getEXpenditure());

		return "pages/exp";
	}

	@ResponseBody

	@PostMapping("/add/shares/payment")
	public int getStaff(@RequestParam("id") long id, @RequestParam("amount") double amount,
			@RequestParam("receipt") String receipt, @RequestParam("type") String type,
			@RequestParam("date") String date) {
		System.err.println("gaga");

		return shareRepo.savePayment(id, type, receipt, date, amount);
	}

	@ResponseBody

	@RequestMapping("/delete/payment/{id}")
	public int payLoan(@PathVariable("id") long id) {
		return shareRepo.deletePayment(id);
	}

	@ResponseBody
	@PostMapping("/update/share/payment/")
	public int update(@RequestParam long id, @RequestParam double amount) {
		return shareRepo.editPayment(id, amount);
	}

	@ResponseBody
	@PostMapping("/add/beneficiary/")
	public int addBeneficiary(Principal principal, @RequestParam String relation, @RequestParam String dob,
			@RequestParam String name, @RequestParam String contact, @RequestParam String email,
			@RequestParam double share) {
		return shareRepo.addBeneficiary(principal, share, relation, contact, email, name, dob);
	}

	@ResponseBody
	@PostMapping("/update/beneficiary/")
	public int update(@RequestParam long id, @RequestParam String name, @RequestParam String email,
			@RequestParam String contact, @RequestParam String dob, @RequestParam double share) {
		return shareRepo.editBeneficiary(id, name, contact, email, dob, share);
	}

	@ResponseBody
	@GetMapping("/delete/beneficiary/{id}")
	public int deleteBene(@PathVariable long id) {
		return shareRepo.deleteBeneficiary(id);
	}

	@ResponseBody

	@RequestMapping("/generate/file")
	public int payLoan() {
		shareRepo.readFile();
		return 1;
	}

	@ResponseBody
	@PostMapping("/add/expense")
	public int addExpense(@RequestParam("name") String name, @RequestParam("narration") String naration,
			@RequestParam("amount") double amount, @RequestParam("date") String date,
			@RequestParam("type") String payment) {
		return loanRepo.saveExpenditure(payment, naration, date, amount, name);
	}

	@ResponseBody
	@PostMapping("/update/expense/")
	public int addExpenseUpdate(@RequestParam long id, @RequestParam("narration") String naration,
			@RequestParam("amount") double amount) {
		return loanRepo.updateEexpenditure(id, naration, amount);
	}

	@ResponseBody
	@PostMapping("/delete/expense/{id}")
	public int deleteExpense(@PathVariable long id) {
		return loanRepo.deleteExpenditure(id);
	}

	@ResponseBody
	@PostMapping("/add/interest")
	public int addInterest(@RequestParam long id, @RequestParam double amount, @RequestParam String year,
			Principal principal) {
		return shareRepo.addIntersest(id, amount, year, principal);
	}

	@ResponseBody
	@RequestMapping("/edit/share/payment/{id}")
	public int editSharePayment(@PathVariable("id") long id, @RequestParam("amount") double amount) {
		return shareRepo.editPayment(id, amount);
	}

}
