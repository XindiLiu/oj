package com.example.oj.rabbit;

import com.example.oj.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @Description 发送单点评测结果
     **/
    public void sendOneJudgeResult(MessageDTO messageDTO) {
        send("oj.test", "test", messageDTO);
    }

    private void send(String exchange, String routingKey, Object o) {
        for (int i = 0; i < 5; i++) {
            try {
                this.rabbitTemplate.convertAndSend(exchange, routingKey, o);
                break;
            } catch (AmqpException e) {
                log.warn("sendOneJudgeResult", e);
                try {
                    Thread.sleep(i * 2000L);
                } catch (Throwable ignore) {
                }
            } catch (Exception e) {
                log.error("sendOneJudgeResult", e);
                break;
            }
        }
    }
}