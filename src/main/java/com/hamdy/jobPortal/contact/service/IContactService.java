package com.hamdy.jobPortal.contact.service;

import com.hamdy.jobPortal.dto.ContactRequestDto;
import com.hamdy.jobPortal.dto.ContactResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IContactService {
    boolean saveContact(ContactRequestDto contactRequestDto);

    List<ContactResponseDto> fetchNewContactMsgs();

    List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir);

    Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(int pageNumber , int pageSize, String sortBy, String sortDir);

    boolean closeContactMsg(Long id , String status);
}
