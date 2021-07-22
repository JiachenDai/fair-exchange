package edu.fair_exchange.mapper;

import edu.fair_exchange.domain.EmailSignatureObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailSignatureMapper {

    Integer addEmailSignature(EmailSignatureObject emailSignatureObject);

    void update(EmailSignatureObject emailSignatureObject);

    EmailSignatureObject getById(@Param("id") String id);

    void updateResponseSig(EmailSignatureObject emailSignatureObject);
}
