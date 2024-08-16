package com.example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.MessageRepository;


@Service
public class MessageService {

    private MessageRepository messageRepository;
   // private AccountService accountService;

    @Autowired
    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
       // this.accountService = accountService;
    }

    public void addNewMessage(Message newMessage){
        messageRepository.save(newMessage);
    }

    public List<Message> getAllMessage(){
        return (List<Message>) messageRepository.findAll();
    }

    public Message getMessageById(Integer messageId){
        return messageRepository.findById(messageId)
                .orElse(null);
    }

    public void deleteMessageById(Integer messageId){
        messageRepository.deleteById(messageId);
    }

    public void updateMessage(String messageText, Integer messageId) throws ResourceNotFoundException {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(messageId + " was not found. Please check the ID and try again."));

        if (messageText == null || messageText.trim().isEmpty() || messageText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text: it must not be empty and should be within 255 characters.");
        }

        message.setMessageText(messageText);
        messageRepository.save(message);
    }

    public List<Message> getAllMessageByPostedby(Integer postedBy) throws ResourceNotFoundException {
        return messageRepository.findAllMessageByPostedBy(postedBy);
    }
}
