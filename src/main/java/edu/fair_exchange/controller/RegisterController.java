package edu.fair_exchange.controller;

import edu.fair_exchange.domain.*;
import edu.fair_exchange.mapper.UserMapper;
import edu.fair_exchange.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping("/storePublicKey")
    public Result register(@RequestParam("userId") String userId, @RequestParam("publicKey") String publicKey){
        Result result = new Result();
        if (userId == null || userId.equals("") || publicKey == null || publicKey.equals("")){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("lack of information, please check."));
            return result;
        }
        User user = userMapper.getById(Integer.parseInt(userId));
        if (user == null){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("No user registered."));
            return result;
        }
        user.setPublicKey(publicKey);
        System.out.println("public key is param: " + publicKey);
        try {
            userMapper.updatePublicKey(Integer.parseInt(userId), publicKey);
        } catch (Exception e){
            e.printStackTrace();
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("store public key failed"));
            return result;
        }
        result.setCode(ErrorCode.OK);
        return result;
    }

    @GetMapping("/generateKeyPair")
    @ResponseBody
    public Result register(@RequestParam("userId") Integer userId){
        Result result = new Result();
        Map<String, Object> keyPairs = null;
        try {
            keyPairs = RSAUtil.generateKeyPairs();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("internal server error"));
            return result;
        }
        KeyPairResult keyPairResult = new KeyPairResult();
        keyPairResult.setPrivateKey((String) keyPairs.get("privateKey"));
        keyPairResult.setPublicKey((String) keyPairs.get("publicKey"));

        User user = userMapper.getById(userId);
        if (user == null){
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("No user registered."));
            return result;
        }
        user.setPublicKey(keyPairResult.getPublicKey());
        System.out.println("public key is param: " + keyPairResult.getPublicKey());
        try {
            userMapper.updatePublicKey(userId, keyPairResult.getPublicKey());
        } catch (Exception e){
            e.printStackTrace();
            result.setCode(ErrorCode.InternalServerError);
            result.setData(new ErrorMessage("store public key failed"));
            return result;
        }
        result.setCode(ErrorCode.OK);
        result.setData(keyPairResult);
        return result;
    }
}
