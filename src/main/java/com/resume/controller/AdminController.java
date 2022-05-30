package com.resume.controller;

import com.resume.entity.User;
import com.resume.service.UserService;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

	//TODO add authentication for admin
	@Autowired
	UserService userService;

	@GetMapping("/users")
	public String showUsers(Model model) {

		List<User> users = userService.findAllUsers();

		model.addAttribute("errorMessage", null);
		model.addAttribute("users", users);

		return "users";
	}
}
