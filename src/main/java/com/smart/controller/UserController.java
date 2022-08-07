package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

//	for common purpose
	@ModelAttribute
	public void commonData(Model model, Principal principal) {
		String userName = principal.getName();

		User user = userRepository.getUserByUserName(userName);

		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String index(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";

	}

	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title", " Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

//	handling contact form
	@PostMapping(value = "/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,
			BindingResult result, @RequestParam("profileImage") MultipartFile file,
			Principal principal,Model model
			,HttpSession session) {

		try {
			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			contact.setUser(user);
			
			if (result.hasErrors()) {
				System.out.println("there is somthing worng");
				System.out.println("ERROR :" + result.toString());
				model.addAttribute("contact", contact);
				return "normal/add_contact_form";
			}
			if (file.isEmpty()) {
//			file is empty so that give some message
				contact.setImageUrl("default.png");
			} else {
				contact.setImageUrl(file.getOriginalFilename());
//			Location where you want to save image
				File saveFilePath = new ClassPathResource("/static/img").getFile();
				System.out.println(saveFilePath.getAbsolutePath());

				Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("image uploaded");

			}

			user.getContacts().add(contact);
			userRepository.save(user);
			
			//message for add contact 
			session.setAttribute("message", new Message("Your Contact added successfully!.. Add more", "success"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			//message for some went wrong ! Try again..
			
			session.setAttribute("message", new Message("Something went wrong!.. Try again", "danger"));
			
			
		}

		return "normal/add_contact_form";
	}
	
//	show contact handler
//	per page 5 contacts
//	currentPage = 0
	
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","Show Users Contacts");
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		Pageable pageable = PageRequest.of(page, 3);
		
		
		Page<Contact> contacts = this.contactRepository.findContactByUserId(user.getId(),pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contact";
	}
	
	
	
//	handling per contact profile
	
	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid,Model model) {
		
		System.out.println(cid);
		Optional<Contact> contactOptioal = this.contactRepository.findById(cid);
		
		Contact contact = contactOptioal.get();
		model.addAttribute("title","Show Details");
		
		model.addAttribute(contact);
		
		return "normal/show_contact_details";
	}

}
