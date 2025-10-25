package com.example.evaliaproject.controller;

import com.example.evaliaproject.entity.TypeUser;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.UserRepository;
import com.example.evaliaproject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("User")
@RestController
public class UserController {
    @Autowired
    IUserService iUserService;
    @Autowired
    UserRepository repos;
    private static final java.util.Set<String> ALLOWED_AGE_RANGES = java.util.Set.of("18_25", "26_35", "36_45", "46_60", "60_plus"
    );
    @PostMapping("/addUser")
    public User addUser(@RequestBody User user) {
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        if (user.getAgeRange() == null || !ALLOWED_AGE_RANGES.contains(user.getAgeRange().trim())) {
            throw new IllegalArgumentException("Tranche d'âge invalide");
        }

        return iUserService.addUser(user);

    }

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers(){
        return iUserService.getAllUsers();

    }

    @GetMapping("/getDetailsUser/{id}")
    public User getDetailsUser(@PathVariable("id") Long id){
        return iUserService.DetailsUser(id);
    }

//    @GetMapping("/getPanelist")
//    public List<User> getUserbyTypeUser(){
//        return repos.findByTypeUser(TypeUser.Paneliste);
//    }

    @PutMapping("/updateUser/{id}")
    public User updateUser (@RequestBody User user,@PathVariable("id") Long id){
        return iUserService.updateUser(user,id);
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        iUserService.deleteUser(id);
        return "User deleted";
    }
}
