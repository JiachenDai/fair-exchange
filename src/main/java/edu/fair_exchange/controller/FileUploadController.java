package edu.fair_exchange.controller;

import com.alibaba.fastjson.JSON;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import edu.fair_exchange.domain.*;
import edu.fair_exchange.mapper.FileMapper;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import edu.fair_exchange.mapper.UserMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Random;

@RestController
public class FileUploadController {

    @Autowired
    private SenderReceiverMapper mapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/upload/{senderId}/{receiverEmail}")
    public Result upload(@PathVariable("senderId") String senderId, @PathVariable("receiverEmail") String receiverEmail, HttpServletRequest request){
        Result result = new Result();
        //check senderId and receiverId
        User receiver = userMapper.getByEmail(receiverEmail);
        if (receiver == null){
            result.setCode(ErrorCode.Unauthorized);
            result.setData(new ErrorMessage("There are no user with this email"));
            return result;
        }
        User sender = userMapper.getById(Integer.parseInt(senderId));
        //insert into db
        //考虑怎么直接把文件上传到AWS并且获得id
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        String originalFilename = file.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        File file3 = null;
        try {
            file3 = File.createTempFile(filename[0], filename[1]);
            file.transferTo(file3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();
        String uuid = request.getParameter("uuid");
        String text = senderId + receiverEmail + uuid;
        String sig = request.getParameter("signature");

        //signature verify
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(sender.getPublicKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(publicKey);
            signature.update(text.getBytes());
            boolean bool = signature.verify(Hex.decodeHex(sig));
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


        String uploadFileName = filename[0] + uuid.toString() + "." + filename[1];
        UploadFile uploadFile = null;
        try {
            s3.putObject("fair-exchange-online", uploadFileName, file3);
            uploadFile = new UploadFile(originalFilename, sig, uploadFileName, "fair-exchange-online");
            //生成序列号
            String time = FastDateFormat.getInstance("yyyyMMddHHmmssSSS").format(new Date());
            String random = generateRandom();
            String sequenceNumber = time + random;
            uploadFile.setFileSequenceNumber(sequenceNumber);
            fileMapper.addFile(uploadFile);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage(e.getMessage()));
            return result;
        }
        //mapper.addSenderReceiverRelation(senderId, receiverId, String.valueOf(uploadFile.getId()));
        SenderReceiverRelation relation = new SenderReceiverRelation(0, senderId, String.valueOf(receiver.getId()), String.valueOf(uploadFile.getId()), 0);
        mapper.addSenderReceiverRelationByObject(relation);

        //to rabbitmq
//        String rela = JSON.toJSONString(relation);
//        rabbitTemplate.convertAndSend("fair-exchange", rela);
        rabbitTemplate.convertAndSend("send-file-notification", uploadFile.getFileSequenceNumber());
        result.setCode(ErrorCode.OK);
        result.setData(new ErrorMessage("ok"));
        return result;
    }

    private String generateRandom(){
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }


}
