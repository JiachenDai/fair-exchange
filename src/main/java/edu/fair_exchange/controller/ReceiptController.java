package edu.fair_exchange.controller;

import com.amazonaws.regions.Regions;
import edu.fair_exchange.domain.*;
import edu.fair_exchange.mapper.EmailSignatureMapper;
import edu.fair_exchange.mapper.FileMapper;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import edu.fair_exchange.mapper.UserMapper;
import edu.fair_exchange.util.RSAUtil;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

@RestController
public class ReceiptController {

    @Autowired
    private SenderReceiverMapper senderReceiverMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailSignatureMapper emailSignatureMapper;

    private String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCQdceYSZm3qcSEiPY6IuH+USJuFQJ6dzfZBISFZDvAuJtu6KOOJGym7T+xPCMrH2CGRWAOGWRyKKc6ZGJntJg60uINoIKQb3XuKUeO1OVLnC0qXhWU3Bu4AjQiaGUO7xGbbDzHSK7Ol9P/DPzlDV26/0JrCcbM0spwrhQEpxKAsX/s/POIeL2ingnkvq4wl38KPGZsvKwtNZSN+5PasglgX/I5ZPryzZUcJHGXsXRnHIHkr1+WRo0jrIqU1NtkRVACEcWhnMhQ0EE6rux/38hDR8d2WxO1EXEIHbgaugpcqMLQK0rjgdlgQXw8DReievCSVPn8+MrkJYYEm10/O53LAgMBAAECggEBAI1vAu+Sf7FVwVWRUqEwrdp0TSVB5J9KQZ9z0NtYezgAhg1cL2VnPAlils1Ld5MssTBEk/q52aH7M170EXQ/WBufhDqbP7lxpyB5wdmRjr0sNvwYCUEpDuplHe3iD69DWRW2LvtdqegStjgr2x3WrEH80GZDuCOo7RLNHERB3tenb8timmcdZUaybGqpgz1pcfTgN1JhAuBT8Ml3YLis8rgpjTRFi1JgKxUTQo1v8+ldHVe1bE/16YmYIq940a963o3hbhoSVCHdVyTBdpjPcqDyBWevZdl/v4O9jCthR3ZVrPMYowo9D/86WgXiOyvf94CkdpRgnb3ZqSd4OZmuquECgYEA7h3SZrTA3l9S+U99LPiRFktyaE3kZ8QUpCD8jYYWuOvU7dttjPVCSBBPUqCA1Ck+Tvb+oWoxyp0aK1y5GLOZEyqY/C/vRq+CePjQXb0SsMzz6Yt77RVnHChl+A7UD6XK+vB+iX/EWCzhnji6Yvq0iFeANGki6R5JiqBitREzRu8CgYEAm09CqDznz8xRvT2BSSSRcojvMUp92pixB4t+Tko17bIk46jaBMW4oozWKOfDcb9fO8Q7y2F8wmZkO6nyW6QYRc7GMElY7ia3UNfJahgDvOSJ4Qnkmk7DJNbK2oTYvOf/98/SM/LURPhI1VB35vM3l3NLajVQGrBtuXnTDpkFduUCgYA5qSMQF+k0cCTplmQbhb2shmyX8XTD4+mTrSE7LCNIeGNBjgdDQQjh5oEu4wsPVUVvXcRfVhXkmJKDuZpg7uy8hW4yc3EfztRrcuUSLfzer2LJRSunR62GVgoLPZwAhgdXKPGbXkMvjv67j8Uivs8EzRuQlX0MsnbgiOIeMqIWJQKBgH0tdxZOcLClh8Q1THz9glWEVPWvipyjsRvZrKXBXpIEoYL01zQ9gMFTLlGt63NEwadUVLVqD7pH+MW2BmcibYXmUQseMxVQEzSPmAFw9aJLuW2uIZjVnqeUj89sf5xSiZbc5BmGwNwOGfYEc3+rzMS+4qvp6HR6exWhQSFk6GiZAoGBALPU1i097VJ3cnNOHV20Nfym70RamXFFxFHWuUfOYwf2ByuXooxeXaCLGioRX+ONvg5RpP9pkMNxhkc4vl7HvY2cBKhOlwDHBljRXr8kSUrdmYlLWAMfIlIKv0dKBrLNTG0kzA9Jw7suRLUjIkJw/9cKIfv/fNxWiu2cFzTHVwfM";


    @RequestMapping("/accept")
    public Result sendReceipt(@RequestParam("fileSequenceNumber") String fileSequenceNumber, HttpServletRequest request, @RequestParam("receipt") String receipt){
        //根据transactionId，查询出文件信息，然后校验receipt，最后把receipt落库
        Result result = new Result();
        UploadFile file = fileMapper.getByFileSequenceNumber(fileSequenceNumber);
        Integer id = file.getId();
        SenderReceiverRelation relation = senderReceiverMapper.getByFileId(id);
//        if (relation.getStatus().equals(1)){
//            result.setCode(ErrorCode.OK);
//            result.setData(new ErrorMessage("file already accepted"));
//            return result;
//        }
        if (relation.getStatus().equals(2)){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("file already aborted."));
            return result;
        }
//        String receipt = request.getParameter("receipt");
        //signature verify
        try {
            User receiver = userMapper.getById(Integer.parseInt(relation.getReceiverId()));
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(receiver.getPublicKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(publicKey);
            signature.update(file.getSignature().getBytes());
            boolean bool = signature.verify(Hex.decodeHex(receipt));
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
        //更新receipt
        file.setReceipt(receipt);
        fileMapper.update(file);
        //修改status
        senderReceiverMapper.updateStatusById(relation.getId(), 1);
        result.setCode(ErrorCode.OK);
        AcceptResult acceptResult = new AcceptResult();
        acceptResult.setBucket("fair-exchange-online");
        acceptResult.setKey(file.getFileKey());
        acceptResult.setRegion("eu-west-2");
        result.setData(acceptResult);
        return result;
    }

    @RequestMapping("/acceptByEmailLink")
    public Result acceptByEmailLink(@RequestParam("fileSequenceNumber") String fileSequenceNumber, HttpServletRequest request, @RequestParam("mailSignatureId") String mailSignatureId){
        //根据transactionId，查询出文件信息，然后校验receipt，最后把receipt落库
        Result result = new Result();
        UploadFile file = fileMapper.getByFileSequenceNumber(fileSequenceNumber);
        Integer id = file.getId();
        SenderReceiverRelation relation = senderReceiverMapper.getByFileId(id);
//        if (relation.getStatus().equals(1)){
//            result.setCode(ErrorCode.OK);
//            result.setData(new ErrorMessage("file already accepted"));
//            return result;
//        }
        if (relation.getStatus().equals(2)){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("file already aborted."));
            return result;
        }
        if (relation.getStatus().equals(1)){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("file already been accepted. please login to the client and download the file"));
            return result;
        }
//        String receipt = request.getParameter("receipt");
        EmailSignatureObject emailSignatureObject = emailSignatureMapper.getById(mailSignatureId);
        String emailSignature = emailSignatureObject.getEmailSignature();

        String responseSig = RSAUtil.getSignature(privateKey, emailSignature + "accept");
        emailSignatureObject.setResponsePlusEmailSignature(responseSig);
        //更新签名
        emailSignatureMapper.updateResponseSig(emailSignatureObject);

        //生成receipt
        User receiver = userMapper.getById(Integer.parseInt(relation.getReceiverId()));
        String receipt = RSAUtil.getSignature(receiver.getPrivateKey(), file.getSignature());
        //更新receipt
        file.setReceipt(receipt);
        fileMapper.update(file);
        //修改status
        senderReceiverMapper.updateStatusById(relation.getId(), 1);
        result.setCode(ErrorCode.OK);
        result.setData(new ErrorMessage("Congratulations! You have succeeded to accept the file. Please download the file in client."));
        return result;
    }
}
