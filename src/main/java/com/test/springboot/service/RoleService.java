package com.test.springboot.service;

import com.test.springboot.dao.RoleDAO;
import com.test.springboot.model.Role;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleService {
    @Resource
    private RoleDAO roleDAO;

    public List<Role> findByUserid(Integer userId){
        return roleDAO.findByUserId(userId);
    }
}
