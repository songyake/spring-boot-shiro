package com.test.springboot.service;

import com.test.springboot.dao.UserDAO;
import com.test.springboot.model.User;
import com.test.springboot.system.vo.Grid;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private UserDAO userDAO;

    public Grid findList(){
        Grid grid = new Grid();
        Page page = PageHelper.offsetPage(0, 10, true);
        userDAO.findList(null);
        grid.setRows(page.getResult());
        grid.setTotal(page.getTotal());
        return grid;
    }

    public User getUserByName(String name){
        return userDAO.getByName(name);
    }


    public User getByOpenid(String openid){
        //这里假装是通过openid从数据库中获取到用户信息
        User user = new User();
        user.setName("张三");
        user.setLoginName("zhangsan");
        user.setPhone("18888888888");
        user.setId(1);
        user.setLoginFlag("1");

        return user;
    }

    public User getByPhone(String phone){
        return userDAO.getByPhone(phone);
    }
}
