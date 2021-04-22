package com.welfare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.welfare.repository.StaffRepository;
import com.welfare.repository.UserRepository;

@Controller
public class UserContoller {

	@Autowired
	UserRepository userRepo;

	@Autowired
	StaffRepository staffRepo;

	@ResponseBody
	@RequestMapping("/add/user")
	public int addUser(@RequestParam("name") String name, @RequestParam("password") String pass,
			@RequestParam("username") String username, @RequestParam("role") int role) {
		return userRepo.addUser(username, name, pass, role);

	}

	@ResponseBody
	@RequestMapping("/delete/user/{id}")
	public int addUser(@PathVariable("id") long id) {
		return userRepo.deleteUser(id);

	}

	@ResponseBody
	@RequestMapping("/edit/password/{id}/{pass}")
	public int addUser(@PathVariable("id") long id, @PathVariable("pass") String pass) {
		return userRepo.changePass(id, pass);

	}

	@RequestMapping("/admin/user/data")
	public String addUser(Model model) {
		model.addAttribute("users", userRepo.getUsers());
		return "admin/users";

	}

	
	//
	@RequestMapping("/admin/add/payments")
	public String a(Model model) {
		model.addAttribute("exp", staffRepo.getExpenses());
		return "pages/addpayment";

	}

}
