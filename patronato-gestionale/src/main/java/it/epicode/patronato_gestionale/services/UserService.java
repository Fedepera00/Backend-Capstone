package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.auth.AppUser;
import it.epicode.patronato_gestionale.auth.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private AppUserRepository appUserRepository;


    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }
}
