package com.realtech.socialsurvey.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.realtech.socialsurvey.auth.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u JOIN FETCH u.userProfiles up JOIN FETCH up.profilesMaster WHERE u.loginName = ?1")
	User findByEmailWithRoles(String email);
}