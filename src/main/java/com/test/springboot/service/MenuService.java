package com.test.springboot.service;

import com.test.springboot.dao.MenuDAO;
import com.test.springboot.model.Menu;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MenuService {
    @Resource
    private MenuDAO menuDAO;

    public List<Menu> getAllMenuByRoleId(Integer roleId){
        return menuDAO.getAllMenuByRoleId(roleId);
    }
}
