package com.example.oj.rabbit;

import com.example.oj.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rabbitmq")
public class MessageController {
    @Autowired
    MessageSender messageSender;
    @PostMapping(value = "/sender")
    public String producer(@RequestBody MessageDTO msg) {
        messageSender.sendOneJudgeResult(msg);
        return "Message sent to the RabbitMQ Queue Successfully";
    }
}
