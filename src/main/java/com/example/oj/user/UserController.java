package com.example.oj.user;

import com.example.oj.common.Result;
import com.example.oj.utils.JwtUtil;
import com.example.oj.utils.MD5Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLogin, HttpServletRequest request, HttpServletResponse response){
        log.info("login: {}", userLogin);
        userLogin.setPassword(MD5Utils.md5(userLogin.getPassword()));
       var result = userService.login(userLogin);

      if (result.getCode() == 1){
          User user = (User) result.getData();
          Cookie tokenCookie = new Cookie("token",JwtUtil.generateJWT(user.getId()));
          tokenCookie.setMaxAge(Integer.MAX_VALUE);
          response.addCookie(tokenCookie);
          log.info("logged in as:{}", user.getId());
      }
        else{
            log.info("login failed");
        }
        return result;
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user){
        user.setPassword(MD5Utils.md5(user.getPassword()));
        return userService.save(user);
    }
    @GetMapping("/user/{id}")
    public Result getById(@PathVariable Long id){
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PutMapping("/user")
    public Result update(@RequestBody UserUpdateDTO user){
        log.info("编辑员工信息: {}", user);
        User currentUser;
        if(user.id != null){
            currentUser = userService.getById(user.id);
            if(currentUser==null){
                return Result.fail("No user id");
            }
        }
        else{
            return Result.fail("No user id");
        }
        if(user.password != null){
            currentUser.setPassword(MD5Utils.md5(user.password));
        }
        if (user.name!=null){
            currentUser.setName(user.name);
        }

        userService.update(currentUser);

        return Result.success();
    }

}
