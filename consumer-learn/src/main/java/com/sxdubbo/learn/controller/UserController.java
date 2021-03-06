package com.sxdubbo.learn.controller;


import com.sxdubbo.learn.utils.AppMD5Util;
import com.sxdubboapi.learn.domain.User;
import com.sxdubboapi.learn.service.CommentService;
import com.sxdubboapi.learn.service.CourseService;
import com.sxdubboapi.learn.service.RedisService;
import com.sxdubboapi.learn.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * created by  luwei
 * 2018-01-04 14:12.
 **/
@Controller
@RequestMapping(value = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    public UserService userService;

    @Autowired
    public CourseService courseService;

    @Autowired
    public CommentService commentService;
//    public User user;

    @Autowired
    public RedisService redisService;


    //    @ResponseBody
//    @GetMapping(value = "/index")
//    public JSONObject show(@PathVariable("name") String username){
////        BeanUtils.copyProperties();
//        JSONObject jsonObject = new JSONObject();
//        User user = new User();
//
//        user = userService.findByUsername(username);
//        System.out.println(user.getUsername()+"++++++++++++"+user.getPassword());
//        jsonObject.put("str", user);
//        return jsonObject;
//    }


    @GetMapping(value = "/register")
    public String register() {

        return "/register";
    }

    //    @GetMapping(value = "/test")
//    public String hello(){
//        System.out.println("hello this is test++++++++++++++");
//        return "/user/test";
//    }
//
//
    @RequestMapping(value = "/add")
//    @RequiresPermissions("user:add")
    public String add(@Valid User user) {
        System.out.println(user.getUsername());
        user.setUserType(0);
        user.setUserStatus(0);
        user.setCreateDate(new Date());
        user.setModifyDate(new Date());
        System.out.println("add success++++++++++++" + user.getUsername());
        userService.addUser(user);
        return "/user/login";
    }


    @GetMapping(value = "/getByName")
    public String getByUsername(@PathVariable(value = "username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user/show";
    }


    @GetMapping(value = "/login")
    public String loginForm() {

        return "/admin/user/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@Valid User user, HttpServletRequest request, HttpServletResponse response)throws IOException {
        User user1 = userService.findByUsername(user.getUsername());
        if(user1.getPassword().equals(AppMD5Util.getMD5(user.getPassword()))){
            System.out.println(user.getUsername());
            System.out.println(user.getPassword());

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            //使用request对象的getSession()获取session，如果session不存在则创建一个
            HttpSession session = request.getSession();
            //将数据存储到session中
            session.setAttribute("userInfo", user1);

            redisService.setObj("user", user1);
//            User user_redis = new User();
//            user_redis = (User) redisService.getObj("user");
            return "success";
        }else{
            return "error";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(RedirectAttributes redirectAttributes,HttpServletRequest request) {
        //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        try {
            redisService.delObj("user");
            HttpSession session1 = request.getSession();
            session1.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        return "/user/login";
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("------没有权限-------");
        return "user/403";
    }

    public List<User> getAll() {
        return userService.findAllUser();
    }

    @GetMapping(value = "/lecture")
    public String getAllLecture(Model model) {

        List<User> lectureList = getAll();

        model.addAttribute("lectureList", lectureList);
        return "/admin/user/lecture";
    }

    @GetMapping(value = "/common")
    public String getAllCommon(Model model) {

        List<User> commonList = getAll();
        model.addAttribute("commonList", commonList);
        return "/admin/user/common";
    }

    @GetMapping(value = "/view")
    public String getUserById(@RequestParam(value = "id", required = false) Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("userView", user);
        return "/admin/user/user_view";
    }

    @GetMapping(value = "/editStatus")
    public String editUserStatus(@RequestParam(value = "id") Integer id, @RequestParam(value = "status") Integer status, Model model) {

        User user = userService.updateUserStatusById(status, id);
        User user1 = userService.getUserById(id);
        System.out.println("here is update," + user.getUserType() + user.getUsername());
        if (user1.getUserType() == 1) {
            List<User> commonList = getAll();
            model.addAttribute("commonList", commonList);
            return "/admin/user/common";
        } else {
            List<User> lectureList = getAll();

            model.addAttribute("lectureList", lectureList);
            return "/admin/user/lecture";
        }

    }

    @GetMapping(value = "/delete")
    public String deleteUser(@RequestParam(value = "id") Integer id, Model model) {

        User user = userService.getUserById(id);
        userService.deleteUser(id);
        System.out.println("here is delete," + user.getUserType() + user.getUsername());
        if (user.getUserType() == 2) {
            List<User> lectureList = getAll();
            model.addAttribute("lectureList", lectureList);
            return "/admin/user/lecture";
        } else {
            List<User> commonList = getAll();
            model.addAttribute("commonList", commonList);
            return "/admin/user/common";
        }

    }


}
