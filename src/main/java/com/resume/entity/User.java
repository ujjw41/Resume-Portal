package com.resume.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@NotBlank(message = "Name is required")
	@Column(unique = true)
	String username;

	@Size(min = 4, max = 15, message = "mobile must have more than 3 and less than 15 digits")
	String mobile;

	@Size(min = 6, message = "password must contain at least 6 characters")
	String password;

	@Email(message = "Invalid Email")
	@NotBlank(message = "email is required")
	@Column(unique = true, length = 50)
	String email;

	String resume_link;

	@Column(columnDefinition = "varchar(255) default 'USER'")
	String role = "USER";

	@Column(columnDefinition = "boolean default true")
	Boolean enabled = true;
}
