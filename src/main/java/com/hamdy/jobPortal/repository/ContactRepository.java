package com.hamdy.jobPortal.repository;

import com.hamdy.jobPortal.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}