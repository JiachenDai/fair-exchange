package edu.fair_exchange.controller;

import edu.fair_exchange.domain.*;
import edu.fair_exchange.mapper.FileMapper;
import edu.fair_exchange.mapper.SenderReceiverMapper;
import edu.fair_exchange.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FileListController {

    @Autowired
    private SenderReceiverMapper senderReceiverMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;


    @RequestMapping("/listSent")
    public Result listSent(@RequestParam("userId") Integer userId){
        Result result = new Result();
        List<SenderReceiverRelation> sents = senderReceiverMapper.listSent(userId);
        List<ListSentElement> list = new ArrayList<>();
        for (SenderReceiverRelation sent : sents) {
            ListSentElement element = new ListSentElement();
            String receiverId = sent.getReceiverId();
            //根据id获取人名
            User user = userMapper.getById(Integer.parseInt(receiverId));
            element.setRecipient(user.getUsername());
            //根据status，转换成对应状态
            int status = sent.getStatus();
            if (status == 0){
                element.setStatus("unaccepted");
            }else if (status == 1){
                element.setStatus("accepted");
            }else if (status == 2){
                element.setStatus("abort");
            }
            String fileId = sent.getFileId();
            //根据fileId查询文件名
            UploadFile file = fileMapper.getById(fileId);
            element.setFileName(file.getFilename());
            element.setFileSequenceNumber(file.getFileSequenceNumber());
            list.add(element);
        }
        result.setCode(ErrorCode.OK);
        result.setData(list);
        return result;
    }
    @RequestMapping("/listReceived")
    public Result listReceived(@RequestParam("userId") Integer userId){
        Result result = new Result();
        List<SenderReceiverRelation> receive = senderReceiverMapper.listReceived(userId);
        List<ListReceivedElement> list = new ArrayList<>();
        for (SenderReceiverRelation r : receive) {
            ListReceivedElement element = new ListReceivedElement();
            String senderId = r.getSenderId();
            //根据id获取人名
            User user = userMapper.getById(Integer.parseInt(senderId));
            element.setSender(user.getUsername());
            //根据status，转换成对应状态
            int status = r.getStatus();
            if (status == 0){
                element.setStatus("unaccepted");
            }else if (status == 1){
                element.setStatus("accepted");
            }else if (status == 2){
                element.setStatus("abort");
            }
            String fileId = r.getFileId();
            //根据fileId查询文件名
            UploadFile file = fileMapper.getById(fileId);
            element.setFileName(file.getFilename());
            element.setFileSequenceNumber(file.getFileSequenceNumber());
            list.add(element);
        }
        result.setCode(ErrorCode.OK);
        result.setData(list);
        return result;
    }

}
