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
import org.springframework.web.multipart.MultipartFile;
import com.welfare.data.Payments;
import com.welfare.data.Staff;
import com.welfare.repository.LoanRepository;
import com.welfare.repository.ShareRepository;
import com.welfare.repository.StaffRepository;
import com.welfare.repository.UserRepository;
import com.welfare.sys.FileStorageService;

@Controller
public class StaffController {

	@Autowired
	StaffRepository staffRepo;

	@Autowired
	ShareRepository shareRepo;

	@Autowired
	LoanRepository loanRepo;

	@Autowired
	UserRepository userRepo;
	

	@Autowired
	private FileStorageService fileStorageService;


	@ResponseBody

	@RequestMapping("/get/staff")
	public List<Staff> getStaff() {
		return staffRepo.getStaff();
	}

	@ResponseBody

	@RequestMapping("/send/message")
	public int sendMessage(@RequestParam("id") long id, @RequestParam("message") String message) {
		System.err.println(message);
		return shareRepo.sendMessageStaffId(id, message.replace("<br>", "\n").replace("<strong>", "<b>")
				.replace("</strong>", "</b>").replace("<p>", "").replace("</p>", ""));
	}

	@ResponseBody

	@RequestMapping("/deactivate/{id}")
	public int deactivate(@PathVariable("id") long id) {
		System.err.println(id);
		return staffRepo.deactivate(id);
	}

	@ResponseBody
	@RequestMapping("/activate/{id}")
	public int activate(@PathVariable("id") long id) {
		return staffRepo.deactivate(id);
	}

	@ResponseBody
	@PostMapping("/add/staff")
	public int addStaff(@RequestParam("controller") String staffId, @RequestParam("name") String name,
	@RequestParam String mobile,@RequestParam String email,
			@RequestParam("type") int rank) {
		return staffRepo.addStaff(name, staffId, rank, mobile);
	}

	@ResponseBody
	@PostMapping("/edit/staff/")
	public int editStaff(@RequestParam("name") String name,@RequestParam("controller") String staffId, 
	@RequestParam String contact,@RequestParam String email,@RequestParam long id) {
		return staffRepo.updateStaff(id,name,contact,email,staffId);
	}
	
	
	
	

	@RequestMapping("/update/my/detail/")
	public String editStaff(@RequestParam("name") String name,@RequestParam("file") MultipartFile file,
	@RequestParam String mobile,@RequestParam String email,@RequestParam long id) {
		String fileName = fileStorageService.storeFile(file);

	

		 staffRepo.updateStaffs(id,name,mobile,email,fileName);
	return "my/applications";
	}
	

	
	
	
	@RequestMapping("/admin/staff/data")
	public String staffData(Model model) {
		model.addAttribute("staff", staffRepo.getActiveStaff());
		return "pages/staffs";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		return "auth-login";
	}

	
	@RequestMapping("/my/info")
	public String inof(Model model,Principal principal) {
		model.addAttribute("staff", staffRepo.getStaff(principal));
		return "my/info";
	}

	@RequestMapping("/staff")
	public String login(Model model, Principal principal) {

		Long id = Long.parseLong(principal.getName());
		System.err.println(id + "---------");
		List<Double> summary = staffRepo.staffStatus(id);
		//model.addAttribute("shares", shareRepo.getSahrePayments(id));
		model.addAttribute("staff", staffRepo.getStaff(id));
		model.addAttribute("loans", loanRepo.getStaffAprrovedLoans(id));
		model.addAttribute("loanpayment", loanRepo.getStaffLoanPayments(id));
		model.addAttribute("loanstotal", summary.get(0));
		model.addAttribute("loanspaid", summary.get(1));
		model.addAttribute("welfare", summary.get(2));
		return "pages/profile";
	}

	@RequestMapping("/admin/staff/performance/{id}")
	public String staffData(@PathVariable("id") long id, Model model) {
		List<Double> summary = staffRepo.staffStatus(id);
		model.addAttribute("shares", shareRepo.getSharePayments(id));
		model.addAttribute("staff", staffRepo.getStaffDetail(id));
		model.addAttribute("loans", loanRepo.getStaffAprrovedLoans(id));
		model.addAttribute("beneficiaries", staffRepo.getStaffBeneficaries(id));
		model.addAttribute("interest", shareRepo.getStaffInterests(id));
		model.addAttribute("loanpayment", loanRepo.getStaffLoanPayments(id));
		model.addAttribute("loanstotal", summary.get(0));
		model.addAttribute("loanspaid", summary.get(1));
		model.addAttribute("staffdetail", staffRepo.getStaffDetail(id));
		model.addAttribute("welfare", summary.get(2));
		return "pages/profile";
	}

	//
	@RequestMapping("/admin/add/staff")
	public String addStaff(Model model) {
		model.addAttribute("dept", staffRepo.getDepartment());
		return "pages/newstaff";
	}

	@RequestMapping("/admin/add/user")
	public String addUser(Model model) {
		model.addAttribute("dept", userRepo.getRoles());
		return "admin/adduser";
	}

	@ResponseBody
	@RequestMapping("/get/shares/payments")
	public List<Payments> getStaffPayments() {
		return shareRepo.getSahrePayments();
	}
	
	
	@ResponseBody
	@GetMapping("/delete/staff/{id}")
	public int getStaffPayments(@PathVariable("id") long id) {
		return staffRepo.delete(id);
	}
	
	@RequestMapping("/")
	public String home() {
		return "redirect:/login";
	}

}
