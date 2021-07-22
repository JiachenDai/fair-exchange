package edu.fair_exchange.mapper;

import edu.fair_exchange.domain.UploadFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMapper {

    Integer addFile(UploadFile file);

    UploadFile getById(@Param("id") String id);

    void update(UploadFile file);

    UploadFile getByFileSequenceNumber(@Param("fileSequenceNumber") String fileSequenceNumber);

    void updateAbortSignatureByFileId(@Param("fileId") Integer fileId, @Param("abortSignature") String abortSignature);
}
