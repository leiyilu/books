package cn.book.controller;

import cn.book.pojo.Admin;
import cn.book.pojo.User;
import cn.book.service.*;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource(name = "adminService")
    private AdminService adminService;
    @Resource
    private UserService userService;

    /**
     * 跳转登入界面
     *
     * @return
     */
    @RequestMapping("/skipLogin.html")
    public String skipLogin(HttpSession session) {
       session.removeAttribute("user");
        System.out.println("删除对象");
        return "login";
    }


    /**
     * 这是新分支
     * 管理员名称ajax
     *
     * @return
     */
    @RequestMapping("/verifyName.html")
    @ResponseBody
    public Object verifyName(String name, String role) {
        if (role.equals("1")) {
            System.out.println(role);
            User user = userService.getUserByUserName(name);
            if (user != null) {
                return JSONArray.toJSONString("exist");
            } else {
                return JSONArray.toJSONString("onexist");
            }
        } else {
            Admin admin = adminService.getAdminByName(name);
            if (admin != null) {
                return JSONArray.toJSONString("exist");
            } else {
                return JSONArray.toJSONString("onexist");
            }
        }
    }

    /**
     * 登入
     *
     * @return
     */
    @RequestMapping("/login.html")
    public String login(String name, String pwd, HttpSession session,String role) {
       if(role.equals("1")){
           User user = userService.getUserByUserNameAndPwd(name, pwd);
           if (user != null) {
               session.setAttribute("identity", "用户");
               session.setAttribute("user", user);
               session.removeAttribute("admin");
               return "redirect:/userAction/bookInfos.html";
           } else {
               if (name == "" || name == null) {
                   throw new RuntimeException("请先登入！");
               } else {
                   throw new RuntimeException("密码错误！");
               }
           }
       }else{
           Admin admin = adminService.getAdminByName(name, pwd);
           if (admin != null) {
               session.setAttribute("identity", "管理员");
               session.setAttribute("admin", admin);
               session.removeAttribute("user");
               return "home";
           } else {
               if (name == "" || name == null) {
                   throw new RuntimeException("请先登入！");
               } else {
                   throw new RuntimeException("密码错误！");
               }
           }
       }
    }

    /**
     * 退出登入
     * @param session
     * @return
     */
    @RequestMapping("/logout.html")
    public String logout(HttpSession session) {
        session.removeAttribute("admin");
        session.removeAttribute("user");
        return "login";
    }

    /**
     * 用户注册
     * @param user
     * @param session
     * @return
     */
    @RequestMapping("/userLogin.html")
    public String userLogin(User user,HttpSession session){
        user.setIsMember(0);
        user.setMoney(0);
        user.setBirthData("".equals(user.getBirthData())||user.getBirthData()==null? null :user.getBirthData());
        int row = userService.addUser(user);
        if(row>0){
            session.setAttribute("user",userService.getUserByUserName(user.getUserName()));
            return "redirect:/userAction/bookInfos.html";
        }else{
            return "login";
        }
    }
}
