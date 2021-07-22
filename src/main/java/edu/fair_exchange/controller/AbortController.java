package edu.fair_exchange.controller;

import edu.fair_exchange.domain.*;
import edu.fair_exchange.mapper.FileMapper;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import edu.fair_exchange.mapper.UserMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

@RestController
public class AbortController {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private SenderReceiverMapper senderReceiverMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    private String from = "daijiachen97@163.com";

    @RequestMapping("/abort")
    @Transactional
    public Result abort(@RequestParam("fileSequenceNumber") String fileSequenceNumber, @RequestParam("abortSignature") String abortSignature){
        Result result = new Result();
        UploadFile file = fileMapper.getByFileSequenceNumber(fileSequenceNumber);
        Integer id = file.getId();
        SenderReceiverRelation relation = senderReceiverMapper.getByFileId(id);
        //check if already accepted
        if (relation.getStatus().equals(1)){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("file already accepted"));
            return result;
        }
        if (relation.getStatus().equals(2)){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("file already aborted.you do not need to abort twice."));
            return result;
        }
        //verify the signature
        User sender = null;
        try {
            sender = userMapper.getById(Integer.parseInt(relation.getSenderId()));
            String text = sender.getId() + fileSequenceNumber;
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(sender.getPublicKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(publicKey);
            signature.update(text.getBytes());
            boolean bool = signature.verify(Hex.decodeHex(abortSignature));
            if (!bool){
                result.setCode(ErrorCode.InternalServerError);
                result.setData(new ErrorMessage("unauthorized"));
                return result;
            }
        } catch (Exception e){
            e.printStackTrace();
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage(e.getMessage()));
            return result;
        }
        //store signature
        fileMapper.updateAbortSignatureByFileId(file.getId(), abortSignature);
        senderReceiverMapper.updateStatusById(relation.getId(), 2);
        User receiver = userMapper.getById(Integer.parseInt(relation.getReceiverId()));
        String email = receiver.getEmail();
        String subject = "File Abort";
        String text = sender.getUsername() + "has aborted a transaction with file:" + file.getFilename();
//        this.sendSimpleMail(email, subject, text);
        result.setCode(ErrorCode.OK);
        return result;
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
