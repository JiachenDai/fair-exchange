package edu.fair_exchange.controller;

import edu.fair_exchange.domain.ErrorCode;
import edu.fair_exchange.domain.Result;
import edu.fair_exchange.domain.SenderReceiverRelation;
import edu.fair_exchange.domain.UploadFile;
import edu.fair_exchange.mapper.FileMapper;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FileCheckController {
    @Autowired
    private SenderReceiverMapper mapper;

    @Autowired
    private FileMapper fileMapper;

    @RequestMapping("/check/{receiverId}")
    public List<SenderReceiverRelation> upload(@PathVariable("receiverId") String receiverId){
        //check the receiverId

        //query from db
        List<SenderReceiverRelation> list = mapper.checkReceive(receiverId);
        return list;
    }

//    @RequestMapping("/getSignature/{transactionId}")
//    public String getSignature(@PathVariable String transactionId){
//        Integer id = Integer.parseInt(transactionId);
//        SenderReceiverRelation relation = mapper.getByTransactionId(id);
//        String fileId = relation.getFileId();
//        UploadFile file = fileMapper.getById(fileId);
//        return file.getSignature();
//    }

    @RequestMapping("/viewSignature")
    public Result getSignature(@RequestParam("fileSequenceNumber") String fileSequenceNumber){
        Result result = new Result();
        UploadFile file = fileMapper.getByFileSequenceNumber(fileSequenceNumber);
        result.setCode(ErrorCode.OK);
        result.setData(file);
        return result;
    }
}
