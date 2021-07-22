package edu.fair_exchange.domain;

import com.alibaba.fastjson.JSON;
import edu.fair_exchange.mapper.EmailSignatureMapper;
import edu.fair_exchange.mapper.FileMapper;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import edu.fair_exchange.mapper.UserMapper;
import edu.fair_exchange.util.RSAUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class FileSentNotificationer {

    @Autowired
    private JavaMailSender mailSender;

    private String from = "daijiachen97@163.com";

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SenderReceiverMapper senderReceiverMapper;

    @Autowired
    private EmailSignatureMapper emailSignatureMapper;

    private String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCQdceYSZm3qcSEiPY6IuH+USJuFQJ6dzfZBISFZDvAuJtu6KOOJGym7T+xPCMrH2CGRWAOGWRyKKc6ZGJntJg60uINoIKQb3XuKUeO1OVLnC0qXhWU3Bu4AjQiaGUO7xGbbDzHSK7Ol9P/DPzlDV26/0JrCcbM0spwrhQEpxKAsX/s/POIeL2ingnkvq4wl38KPGZsvKwtNZSN+5PasglgX/I5ZPryzZUcJHGXsXRnHIHkr1+WRo0jrIqU1NtkRVACEcWhnMhQ0EE6rux/38hDR8d2WxO1EXEIHbgaugpcqMLQK0rjgdlgQXw8DReievCSVPn8+MrkJYYEm10/O53LAgMBAAECggEBAI1vAu+Sf7FVwVWRUqEwrdp0TSVB5J9KQZ9z0NtYezgAhg1cL2VnPAlils1Ld5MssTBEk/q52aH7M170EXQ/WBufhDqbP7lxpyB5wdmRjr0sNvwYCUEpDuplHe3iD69DWRW2LvtdqegStjgr2x3WrEH80GZDuCOo7RLNHERB3tenb8timmcdZUaybGqpgz1pcfTgN1JhAuBT8Ml3YLis8rgpjTRFi1JgKxUTQo1v8+ldHVe1bE/16YmYIq940a963o3hbhoSVCHdVyTBdpjPcqDyBWevZdl/v4O9jCthR3ZVrPMYowo9D/86WgXiOyvf94CkdpRgnb3ZqSd4OZmuquECgYEA7h3SZrTA3l9S+U99LPiRFktyaE3kZ8QUpCD8jYYWuOvU7dttjPVCSBBPUqCA1Ck+Tvb+oWoxyp0aK1y5GLOZEyqY/C/vRq+CePjQXb0SsMzz6Yt77RVnHChl+A7UD6XK+vB+iX/EWCzhnji6Yvq0iFeANGki6R5JiqBitREzRu8CgYEAm09CqDznz8xRvT2BSSSRcojvMUp92pixB4t+Tko17bIk46jaBMW4oozWKOfDcb9fO8Q7y2F8wmZkO6nyW6QYRc7GMElY7ia3UNfJahgDvOSJ4Qnkmk7DJNbK2oTYvOf/98/SM/LURPhI1VB35vM3l3NLajVQGrBtuXnTDpkFduUCgYA5qSMQF+k0cCTplmQbhb2shmyX8XTD4+mTrSE7LCNIeGNBjgdDQQjh5oEu4wsPVUVvXcRfVhXkmJKDuZpg7uy8hW4yc3EfztRrcuUSLfzer2LJRSunR62GVgoLPZwAhgdXKPGbXkMvjv67j8Uivs8EzRuQlX0MsnbgiOIeMqIWJQKBgH0tdxZOcLClh8Q1THz9glWEVPWvipyjsRvZrKXBXpIEoYL01zQ9gMFTLlGt63NEwadUVLVqD7pH+MW2BmcibYXmUQseMxVQEzSPmAFw9aJLuW2uIZjVnqeUj89sf5xSiZbc5BmGwNwOGfYEc3+rzMS+4qvp6HR6exWhQSFk6GiZAoGBALPU1i097VJ3cnNOHV20Nfym70RamXFFxFHWuUfOYwf2ByuXooxeXaCLGioRX+ONvg5RpP9pkMNxhkc4vl7HvY2cBKhOlwDHBljRXr8kSUrdmYlLWAMfIlIKv0dKBrLNTG0kzA9Jw7suRLUjIkJw/9cKIfv/fNxWiu2cFzTHVwfM";

    @RabbitListener(queues = "send-file-notification")
    public void getTimerTask(String message){
        //parse parameter
        UploadFile file = fileMapper.getByFileSequenceNumber(message);
        SenderReceiverRelation relation = senderReceiverMapper.getByFileId(file.getId());
        User receiver = userMapper.getById(Integer.parseInt(relation.getReceiverId()));
        User sender = userMapper.getById(Integer.parseInt(relation.getSenderId()));

        //提醒Alice
        String email = receiver.getEmail();
        EmailSignatureObject emailSignatureObject = new EmailSignatureObject();
        emailSignatureObject.setFileSequenceNumber(message);
        emailSignatureMapper.addEmailSignature(emailSignatureObject);
        String link = "http://localhost:8080/acceptByEmailLink?fileSequenceNumber=" + message + "&mailSignatureId=" + emailSignatureObject.getId();
        String subject = "File received Notification";
        String text = "Dear, User: " + receiver.getUsername() + "\nYou have received a file from User: " + sender.getUsername() + ".\nIf you want to accept this file, then click the link below:\n\n" + link;

        String mailSignature = RSAUtil.getSignature(privateKey, text);
        emailSignatureObject.setEmailSignature(mailSignature);
        emailSignatureMapper.update(emailSignatureObject);

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
