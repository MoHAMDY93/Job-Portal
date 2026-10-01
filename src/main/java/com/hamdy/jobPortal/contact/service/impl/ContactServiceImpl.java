package com.hamdy.jobPortal.contact.service.impl;

import com.hamdy.jobPortal.contact.service.IContactService;
import com.hamdy.jobPortal.dto.ContactRequestDto;
import com.hamdy.jobPortal.entity.Contact;
import com.hamdy.jobPortal.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements IContactService {

    private final ContactRepository contactRepository;

    @Override
    public boolean saveContact(ContactRequestDto contactRequestDto) {
        Contact contact= contactRepository.save(transformToEntity(contactRequestDto));
        return (contact.getId() != null);
    }

    private Contact transformToEntity(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactRequestDto , contact);
//        contact.setCreatedAt(Instant.now());
//        contact.setCreatedBy("System");
        contact.setStatus("NEW");
        return contact;
    }
}
