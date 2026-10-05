package com.hamdy.jobPortal.contact.controller;

import com.hamdy.jobPortal.constants.ApplicationConstants;
import com.hamdy.jobPortal.contact.service.impl.ContactServiceImpl;
import com.hamdy.jobPortal.dto.ContactRequestDto;
import com.hamdy.jobPortal.dto.ContactResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactServiceImpl contactService;

    @PostMapping(path = "/public", version = "1.0")
    public ResponseEntity<String> saveContactMsg(@RequestBody @Valid ContactRequestDto contactRequestDto) {
        boolean isSaved = contactService.saveContact(contactRequestDto);
        if (isSaved) {
            return ResponseEntity.status(HttpStatus.CREATED)
            .body("Request processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Request processing failed");
        }
    }
//    @GetMapping
//    public ResponseEntity<String> fetchOpenContacts(@RequestParam @Validated @NotBlank(message = "Status cannot be blank") String status) {
//        return ResponseEntity.ok().body("These are the open contacts with the status: " + status);
//    }

    @GetMapping("/admin")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactMsgs() {
        List<ContactResponseDto> contactResponseDtos = contactService.fetchNewContactMsgs();
        return ResponseEntity.ok().body(contactResponseDtos);
    }

    @GetMapping("/sort/admin")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactMsgsWithSort(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        List<ContactResponseDto> contactResponseDtos = contactService.fetchNewContactMsgsWithSort(sortBy , sortDir);
        return ResponseEntity.ok().body(contactResponseDtos);
    }

    @GetMapping("/page/admin")
    public ResponseEntity<Page<ContactResponseDto>> fetchOpenContactMsgsWithPaginationAndSort(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<ContactResponseDto> contactResponseDto = contactService.
                fetchNewContactMsgsWithPaginationAndSort(pageNumber, pageSize, sortBy, sortDir);

        return ResponseEntity.ok().body(contactResponseDto);
    }

    @PatchMapping("/{id}/status/admin")
    public ResponseEntity<String> closeContactMsg(@PathVariable String id) {
        boolean isUpdated = contactService.closeContactMsg(Long.valueOf(id),
                ApplicationConstants.CLOSED_MESSAGE);
        if (isUpdated) {
            return ResponseEntity.ok().body("Contact message updated successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update contact message");
    }
}
