package edu.fair_exchange.domain;

import com.alibaba.fastjson.JSON;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import edu.fair_exchange.mapper.UserMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MQListener {

    @Autowired
    private SenderReceiverMapper senderReceiverMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserMapper userMapper;

    private String from = "daijiachen97@163.com";

    @RabbitListener(queues = "fair-exchange")
    public void getTimerTask(String message){
        //parse parameter
        SenderReceiverRelation senderReceiverRelation = JSON.parseObject(message, SenderReceiverRelation.class);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //判断是否状态置为1
        senderReceiverRelation = senderReceiverMapper.getByTransactionId(senderReceiverRelation.getId());
        if (senderReceiverRelation.getStatus() == 1){
            return;
        }
        //提醒Alice
        User user = userMapper.getById(Integer.parseInt(senderReceiverRelation.getSenderId()));
        String email = user.getEmail();
        String subject = "Your receiver does not accept the file reminder";
        String text = "Your receiver does not accept the file and what will you do next?";
        this.sendSimpleMail(email, subject, text);
    }

    public void sendSimpleMail(String to, String subject, String contnet){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(contnet);
        message.setFrom(from);

        try {
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
