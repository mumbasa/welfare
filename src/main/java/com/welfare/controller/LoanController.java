package com.welfare.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.welfare.data.LoanData;
import com.welfare.repository.LoanRepository;
import com.welfare.repository.StaffRepository;

@Controller
public class LoanController {

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	LoanRepository loanRepository;

	@RequestMapping("/admin/loan/apply")
	public String applyLoan(Model model,Principal principal) {
		System.err.println(principal.getName()+"------------");

		model.addAttribute("debt", loanRepository.getStaffIndebtness(principal));
		model.addAttribute("loans", loanRepository.getLoanTypes());
		return "pages/newloan";
	}

	@RequestMapping("/admin/loans/data")
	public String getLoans(Model model) {
		model.addAttribute("loans", loanRepository.getLoans());
		return "admin/loans";
	}

	@RequestMapping("/admin/loans/unapproved")
	public String getLoansUnApproved(Model model) {
		model.addAttribute("loans", loanRepository.getLoansPending());
		return "pages/unapprovedloans";
	}
//
	@RequestMapping("/admin/loans/approved")
	public String getLoansApproved(Model model) {
		model.addAttribute("loans", loanRepository.getLoansApprovedNotCollected());
		return "pages/approvedloans";
	}

	//
	@RequestMapping("/admin/loan/payment")
	public String payLoans(Model model) {
		model.addAttribute("staff", staffRepository.getStaff());
		return "pages/addloan";
	}

//
	@ResponseBody
	@PostMapping("/new/loan/")
	public int grantLoan(@RequestParam("amount") double amount, @RequestParam("type") int loan,
			Principal principal) {
		return loanRepository.saveApplication(principal, amount, loan);

	}

	//
	@RequestMapping("/admin/loan/payment/data")
	public String loanData(Model model) {
		model.addAttribute("payments", loanRepository.getLoanPayments());
		return "pages/loanpayments";
	}

	
	//
	@ResponseBody
	@PostMapping("/add/loan/payment")
	public int payLoan(@RequestParam("id") long id, @RequestParam("amount") double amount,

			@RequestParam("receipt") String receipt, @RequestParam("type") String type,

			@RequestParam("date") String date) {
		return loanRepository.saveLoan(id, type, receipt, date, amount);
	}

	@ResponseBody

	@RequestMapping("/delete/loan/payment/{id}")
	public int payLoan(@PathVariable("id") long id) {
		return loanRepository.deletePayment(id);
	}

	//
	@ResponseBody
	@PostMapping("/reject/loan/{id}")
	public int rejectLoan(@PathVariable("id") long id) {

		return loanRepository.rejectLoan(id);
	}

	@ResponseBody
	@RequestMapping("/generate/loan")
	public int payLoan() {
		loanRepository.readFile();
		return 1;
	}

	@RequestMapping("/generate/loan2")
	public int payLoan2() {
		loanRepository.readFile2();
		return 1;
	}

	@ResponseBody

	@RequestMapping("/collect/loan/{id}")
	public int collectctLoan(@PathVariable("id") long id) {
		return loanRepository.collectCheck(id);
	}
	
	
	@ResponseBody

	@PostMapping("/loan/compute/")
	public LoanData collectctLoan(@RequestParam int type,@RequestParam double amount,Principal principal) {
		return loanRepository.computeLoan(type,amount,principal);
	}

	//
	@ResponseBody
	@RequestMapping("/approve/loan/")
	public int approveLoan(@RequestParam long id,  @RequestParam("amount") double amount) {
		System.err.println(amount);
		
		return loanRepository.approveLoan(id,  amount);
	}

	//
	@ResponseBody
	@RequestMapping("/edit/loan/payment/")
	public int editSharePayment(@RequestParam long id, @RequestParam("amount") double amount) {
		return loanRepository.editPayment(id, amount);
	}

}
