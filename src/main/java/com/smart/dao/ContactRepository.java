package com.smart.dao;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	
//	pageable have current page and contact per page
	
	@Query("select c from Contact c where c.user.id =:userId ")
	public Page<Contact> findContactByUserId(@Param("userId")int userId,Pageable pageable);

}
