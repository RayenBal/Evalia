package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.User;

import java.util.List;

public interface IUserService {

    public User addUser(User user);


    void deleteUser(Long iduser);

    public List<User> getAllUsers();


    User DetailsUser(Long idUser);

    User updateUser(User user, Long id);
}
