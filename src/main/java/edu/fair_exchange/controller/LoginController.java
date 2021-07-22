package edu.fair_exchange.controller;

import edu.fair_exchange.domain.ErrorCode;
import edu.fair_exchange.domain.ErrorMessage;
import edu.fair_exchange.domain.Result;
import edu.fair_exchange.domain.User;
import edu.fair_exchange.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password){
        User user = userMapper.getByUsername(username);
        Result result = new Result();
        if (user == null){
            result.setCode(ErrorCode.Unauthorized);
            result.setData(new ErrorMessage("Username or password is wrong."));
            return result;
        }
        if (!user.getPassword().equals(password)){
            result.setCode(ErrorCode.Unauthorized);
            result.setData(new ErrorMessage("Username or password is wrong."));
            return result;
        }
        result.setCode(ErrorCode.OK);
        result.setData(user);
        return result;
    }
}
