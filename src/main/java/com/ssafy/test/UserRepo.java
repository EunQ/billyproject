package com.ssafy.test;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
	List<User> findAll();
	Optional<User> findById(long id);
	Optional<User> findByEmail(String email);
}
