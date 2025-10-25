package com.example.evaliabackoffice.dto;


import com.example.evaliabackoffice.entity.TypeUser;

public interface UserTypeCount {
    TypeUser getUserType();
    long getTotal();
}