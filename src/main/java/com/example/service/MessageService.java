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

    @Autowired
    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
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

    public void updateMessage(Message updatedMessage) throws ResourceNotFoundException {
        Message message = messageRepository.findById(updatedMessage.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException(updatedMessage.getMessageId() + " was not found. Please check the ID and try again."));
        messageRepository.save(message);
    }

    // public List<Message> getAllMessageByUserId(Integer accountId) throws ResourceNotFoundException {
    //     return messageRepository.findAllMessageByAccountId(accountId);
    // }
}
