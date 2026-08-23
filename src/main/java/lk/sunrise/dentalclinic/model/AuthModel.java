package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.factory.DAOFactory;
import lk.sunrise.dentalclinic.util.*;

public class AuthModel {
    private final UserDAO dao=DAOFactory.userDAO();
    public LoginResponseDTO authenticate(LoginRequestDTO x)throws Exception {
        if(x==null||x.getUsername()==null||x.getUsername().isBlank()||x.getPassword()==null||x.getPassword().isBlank())throw new IllegalArgumentException("Username and password are required.");
        User u=dao.findByUsername(x.getUsername()).orElseThrow(()->new SecurityException("Invalid username or password"));
        if(!u.isActive()||!PasswordEncoder.matches(x.getPassword(),u.getPasswordHash()))throw new SecurityException("Invalid username or password");
        return new LoginResponseDTO(u.getUserId(),TokenUtil.generate(u.getUserId()),u.getFullName(),u.getRole());
    }
    public void logout(String token) {
        if(!TokenUtil.isValid(token))throw new SecurityException("Invalid token");
        TokenUtil.invalidate(token);
    }
    public boolean register(String username,String password,String fullName,String email,UserRole role)throws Exception {
        if(dao.existsByUsername(username))throw new IllegalArgumentException("Username already exists.");
        User u=new User(0,username,PasswordEncoder.encode(password),fullName,email,role,true);
        return dao.save(u);
    }
}
