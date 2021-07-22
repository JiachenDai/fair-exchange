package edu.fair_exchange.controller;

import edu.fair_exchange.domain.*;
import edu.fair_exchange.mapper.UserMapper;
import edu.fair_exchange.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@RestController
public class RegisterController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/register")
    public Result register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email){
        RegisterVO registerVO = new RegisterVO();
        registerVO.setUsername(username);
        registerVO.setPassword(password);
        registerVO.setEmail(email);
        Result result = new Result();
        if (registerVO.getUsername() == null || registerVO.getPassword() == null || registerVO.getEmail() == null){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("lack of information, please check."));
            return result;
        }
        List<User> users = userMapper.getByUsernameAndEmail(username, email);
        if (users.size() > 0){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("This username or email has Already been registered. Please change the username or email."));
            return result;
        }
        //generate key
        Map<String, Object> keyPairs = null;
        try {
            keyPairs = RSAUtil.generateKeyPairs();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("generate key pairs failed."));
            return result;
        }
        registerVO.setPrivateKey((String) keyPairs.get("privateKey"));
        registerVO.setPublicKey((String) keyPairs.get("publicKey"));
        //insert into database;
        try {
            userMapper.addUser(registerVO);
        }catch (Exception e) {
            e.printStackTrace();
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("register failed."));
            return result;
        }
        result.setCode(ErrorCode.OK);
        return result;
    }
}
