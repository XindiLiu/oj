//package com.example.oj.rabbit;
//
//import com.example.oj.dto.MessageDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//@Slf4j
//@Component
//@RabbitListener(queues = "test")
//public class MessageListener {
//    @RabbitHandler
//    public void receiver(MessageDTO msg) {
//        log.info("Message received: {}", msg);
//    }
//
//    @RabbitHandler
//    public void receiver(Object msg) {
//        log.info("Message received: {}", msg);
//    }
//}
