package edu.fair_exchange.mapper;

import edu.fair_exchange.domain.RegisterVO;
import edu.fair_exchange.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    User getById(@Param("id") Integer id);

    User getByUsername(@Param("username") String username);

    void addUser(RegisterVO registerVO);

    List<User> getByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    User getByEmail(@Param("email") String email);
}
