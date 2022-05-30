package com.resume.repository;

import com.resume.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
	List<User> findByEmail(String email);
	User getByUsername(String username);

}
