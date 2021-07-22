package edu.fair_exchange.mapper;

import edu.fair_exchange.domain.SenderReceiverRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SenderReceiverMapper {

    void addSenderReceiverRelation(@Param("senderId") String senderId,@Param("receiverId") String receiverId, @Param("fileId") String fileId);

    List<SenderReceiverRelation> checkReceive(@Param("receiverId") String receiverId);

    SenderReceiverRelation getByTransactionId(@Param("transactionId") Integer transactionId);

    Integer addSenderReceiverRelationByObject(SenderReceiverRelation senderReceiverRelation);

    void updateStatus(@Param("transactionId") String transactionId);

    List<SenderReceiverRelation> listSent(@Param("userId") Integer userId);

    List<SenderReceiverRelation> listReceived(@Param("userId") Integer userId);

    SenderReceiverRelation getByFileId(@Param("fileId") Integer fileId);

    void updateStatusById(@Param("id") Integer id, @Param("status") Integer status);
}
