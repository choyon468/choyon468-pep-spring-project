package com.example.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.ResourceNotFoundException;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@RequestMapping
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {
            // Validate that the username is not blank
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be blank");
            }

            // Validate that the password is at least 4 characters long
            if (account.getPassword() == null || account.getPassword().length() < 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 4 characters long");
            }

            // Check if the username already exists
            if (accountService.usernameExists(account.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }

            // If all validations pass, register the new account
            Account newAccount = accountService.register(account);
            return ResponseEntity.status(HttpStatus.OK).body(newAccount);

        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed due to an unexpected error");
        }
    }

    @PostMapping("login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
    try {
            // Attempt to log in the user with the provided username and password
            Account authenticatedAccount = accountService.login(account.getUsername(), account.getPassword());

            // If authentication is successful, return the account with a status of 200 OK
            return ResponseEntity.ok(authenticatedAccount);
        } catch (AuthenticationException e) {
            // If authentication fails (e.g., incorrect username or password), return a 401 Unauthorized status
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message newMessage){
        try {
            if(newMessage.getMessageText() != null && !newMessage.getMessageText().isEmpty() && newMessage.getMessageText().length() <= 255 && newMessage.getPostedBy() != null){
                messageService.addNewMessage(newMessage);
                return ResponseEntity.status(HttpStatus.OK).body(newMessage);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newMessage);
            }
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newMessage);
        }
        
    }

    @GetMapping("messages")
    public ResponseEntity<List<Message>> retrieveAllMessage(){
        List<Message> message = messageService.getAllMessage();
        try {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @GetMapping("messages/{messageId}")
    public ResponseEntity<Message> retrieveMessageById(@PathVariable Integer messageId){
        //return new ResponseEntity<>(messageService.getMessageById(messageId), HttpStatus.OK);
        try {
            Message message = messageService.getMessageById(messageId);
            if(message == null){
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
        
    }

    @DeleteMapping("messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable Integer messageId) {
        try {
            // Check if the message exists
            if (messageService.getMessageById(messageId) != null) {
                messageService.deleteMessageById(messageId);
                // Return 1 to indicate that one row (message) was deleted
                return ResponseEntity.ok(1);
            } else {
                return ResponseEntity.ok(null);
            }
        } catch (ResourceNotFoundException e) {
            // If message doesn't exist, we still return 200 with empty body
            return ResponseEntity.ok(0);
        }
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

}
