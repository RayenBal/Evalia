package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("userServiceProject")
public class UserService implements IUserService{
    @Autowired
    private UserRepository userRepository;
    @Override
    public User addUser(User user) {


        return userRepository.save(user);
    }



    @Override
    public void deleteUser(Long iduser) {
         userRepository.deleteById(iduser);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    @Override
    public User DetailsUser(Long idUser) {
        return userRepository.findById(idUser).get();
    }

    @Override
    public User updateUser(User user, Long id) {
        return null;
    }
}
