package com.resume.controller;

import com.resume.entity.User;
import com.resume.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.*;
import java.util.ArrayList;

@Controller
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("errorMessage", null);
		model.addAttribute("empty", new ArrayList());
		model.addAttribute("applicationName", "Resume upload portal");

		return "register";
	}

	@PostMapping("/submit")
	public String submit(@Valid User user, BindingResult binding, Model model) {
		if (binding.hasErrors()) {
			model.addAttribute("errorMessage", binding);
			model.addAttribute("applicationName", "Resume upload portal");
			return "register";
		}
		userService.saveUser(user);
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/upload")
	public String upload(Model model, HttpSession httpSession) {
		model.addAttribute("applicationName", "Resume upload portal");
		if (httpSession.getAttribute("userLoggedIn") != null && httpSession.getAttribute("userLoggedIn").equals("yes")) {
			return "/upload";
		}
		model.addAttribute("errorMessage", null);
		model.addAttribute("empty", new ArrayList());
		return "redirect:/register";
	}

	@PostMapping("/save")
	public String save(@RequestParam("myresume") MultipartFile resume, HttpSession httpSession) throws IOException {

		String mylocation = System.getProperty("user.dir") + "/src/main/resources/static/";
		String filename = resume.getOriginalFilename();

		File mySavedFile = new File(mylocation + filename);

		InputStream inputStream = resume.getInputStream();

		OutputStream outputStream = new FileOutputStream(mySavedFile);

		int read = 0;
		byte[] bytes = new byte[10240];

		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		User user = (User) httpSession.getAttribute("user");
		user.setResume_link("http://localhost:8080/" + filename);
		userService.saveUser(user);

		return "redirect:/success";
	}

	@GetMapping("/success")
	public String success() {
		return "success";
	}

	@PostMapping("/verify")
	public String verify(Model model, @RequestParam("email") String email, @RequestParam("password") String password, HttpSession httpSession) {

		String response = userService.verifyUser(email, password, httpSession);

		if (response.equals("success")) {
			return "redirect:/upload";
		}

		model.addAttribute("response", response);
		return "login";

	}

	@GetMapping("/logout")
	public String logout(Model model, HttpSession httpSession) {
		httpSession.invalidate();
		model.addAttribute("applicationName", "Resume upload portal");
		return "redirect:/";
	}

}
