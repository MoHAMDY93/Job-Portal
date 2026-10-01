package com.hamdy.jobPortal.contact.service;

import com.hamdy.jobPortal.dto.ContactRequestDto;

public interface IContactService {
    boolean saveContact(ContactRequestDto contactRequestDto);
}
