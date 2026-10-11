package com.hamdy.jobPortal.repository;

import com.hamdy.jobPortal.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findContactByStatus(String status);

    List<Contact> findContactByStatusOrderByCreatedAtAsc(String status);

    List<Contact> findContactByStatus(String status, Sort sort);

    Page<Contact> findContactByStatus(String status, Pageable pageable);

    @Modifying
    int updateStatusById(@Param("status") String status, @Param("id") Long id, @Param("updatedBy") String updatedBy);
}