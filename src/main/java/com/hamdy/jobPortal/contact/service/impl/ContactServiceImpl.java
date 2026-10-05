package com.hamdy.jobPortal.contact.service.impl;

import com.hamdy.jobPortal.constants.ApplicationConstants;
import com.hamdy.jobPortal.contact.service.IContactService;
import com.hamdy.jobPortal.dto.ContactRequestDto;
import com.hamdy.jobPortal.dto.ContactResponseDto;
import com.hamdy.jobPortal.entity.Contact;
import com.hamdy.jobPortal.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        contact.setStatus(ApplicationConstants.NEW_MESSAGE);
        return contact;
    }

    private ContactResponseDto transformToDto(Contact contact) {
        return new ContactResponseDto(contact.getId(),
                contact.getName(), contact.getEmail(), contact.getUserType(),
                contact.getSubject(), contact.getMessage(), contact.getStatus(),
                contact.getCreatedAt());
    }

    @Override
    public List<ContactResponseDto> fetchNewContactMsgs() {
        List<Contact> contacts = contactRepository.findContactByStatusOrderByCreatedAtAsc(ApplicationConstants.NEW_MESSAGE);
        List<ContactResponseDto> responseDtos = contacts.stream()
                .map(this::transformToDto)
                .collect(Collectors.toList());

        return responseDtos;
    }

    @Override
    public List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        List<Contact> contacts = contactRepository.findContactByStatus(ApplicationConstants.NEW_MESSAGE , sort);
        List<ContactResponseDto> responseDtos = contacts.stream()
                .map(this::transformToDto)
                .collect(Collectors.toList());

        return responseDtos;
    }

    @Override
    public Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Contact> contactPage = contactRepository.findContactByStatus(
                ApplicationConstants.NEW_MESSAGE , pageable
        );

        Page<ContactResponseDto> responseDtoPage = contactPage.map(this::transformToDto);
        return responseDtoPage;
    }

    @Override
    public boolean closeContactMsg(Long id, String status) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact == null) {
            return false;
        } else {
            contact.setStatus(status);
            contactRepository.save(contact);
        }
        return true;
    }
}
