package com.example.oj.user;

import com.example.oj.common.Result;
//import com.example.oj.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service

public class UserServiceImpl {
    @Autowired
    UserRepository userRepository;

    public Result login(UserLoginDTO userLogin) {
        String username = userLogin.getUsername();
        String password = userLogin.getPassword();


        User user = userRepository.findByUsername(username);

        if (user==null){
            return Result.fail("User does not exist");
        } else if (!user.getPassword().equals(password)) {
            return Result.fail("Wrong password");
        }
        else {
//            user.setPassword("******");
            return Result.success(user);
        }
    }
    public void logout() {
        return;
    }

    public Result save(User user){

        User savedUser = null;
        savedUser = userRepository.save(user);
        if (savedUser==null){
            return Result.fail("Registration failed");
        }
        else {
//            user.setPassword("******");
            return Result.success(savedUser);
        }
    }

    public User getById(@PathVariable Long id){
        User user = userRepository.getUserById(id);
        return user;
    }


    public void update(User user) {
        userRepository.save(user);
    }
}
