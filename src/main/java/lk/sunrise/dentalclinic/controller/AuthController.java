package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.UserRole;
import lk.sunrise.dentalclinic.model.AuthModel;

public class AuthController {
    private final AuthModel model=new AuthModel();
    public LoginResponseDTO login(LoginRequestDTO request)throws Exception {
        return model.authenticate(request);
    }
    public boolean register(String username,String password,String fullName,String email,UserRole role)throws Exception {
        return model.register(username,password,fullName,email,role);
    }
    public void logout(String token) {
        model.logout(token);
    }
}
