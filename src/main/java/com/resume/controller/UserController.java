package com.resume.controller;

import com.resume.entity.ErrorMessage;
import com.resume.entity.User;
import com.resume.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/register")
	public String register(Model model, ErrorMessage errorMessage) {
		model.addAttribute("applicationName", "Resume upload portal");
		model.addAttribute("error", errorMessage.getErrorMessage());

		return "register";
	}

	@PostMapping("/submit")
	public String submit(@Valid User user, BindingResult binding, Model model) {
		if (binding.hasErrors()) {
			List<ObjectError> allErrors = binding.getAllErrors();

			String errorMessage = null;

			for (ObjectError firstError : allErrors) {
				errorMessage = firstError.getDefaultMessage();
				System.out.println(firstError.getDefaultMessage());
			}

			return "redirect:/register?errorMessage=" + errorMessage;
		}
		userService.saveUser(user);
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login(HttpSession httpSession) {
		//Logged-in user cannot log in again
		if (httpSession.getAttribute("userLoggedIn") != null && httpSession.getAttribute("userLoggedIn").equals("yes")) {
			return "redirect:/upload";
		}
		return "login";
	}

	@PostMapping("/verifyuser")
	public String verifyUser(Model model, @RequestParam("email") String email, @RequestParam("password") String password, HttpSession httpSession) {

		String response = userService.verifyUser(email, password, httpSession);

		if (response.equals("success")) {
			return "redirect:/upload";
		}

		model.addAttribute("response", response);
		return "login";

	}

	@GetMapping("/upload")
	public String upload(Model model, HttpSession httpSession) {
		model.addAttribute("applicationName", "Resume upload portal");

		//Only logged-in user can access upload page
		if (httpSession.getAttribute("userLoggedIn") != null && httpSession.getAttribute("userLoggedIn").equals("yes")) {
			return "/upload";
		}
		model.addAttribute("errorMessage", null);
		model.addAttribute("empty", new ArrayList());
		return "redirect:/register";
	}

	@PostMapping("/save")
	public String save(@RequestParam("myresume") MultipartFile resume, HttpSession httpSession, User user) throws IOException {

		String fileLocation = System.getProperty("user.dir") + "/src/main/resources/static/";
		String fileName = resume.getOriginalFilename();

		File mySavedFile = new File(fileLocation + fileName);

		InputStream inputStream = resume.getInputStream();

		OutputStream outputStream = new FileOutputStream(mySavedFile);

		int read = 0;
		byte[] bytes = new byte[10240];

		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		//Get user from session and save the file location
		user = (User) httpSession.getAttribute("user");
		user.setResume_link("http://localhost:8080/" + fileName);
		userService.saveUser(user);

		return "redirect:/success";
	}

	@GetMapping("/success")
	public String success() {
		return "success";
	}

	@GetMapping("/logout")
	public String logout(Model model, HttpSession httpSession) {
		httpSession.invalidate();
		model.addAttribute("applicationName", "Resume upload portal");
		return "redirect:/";
	}

}
