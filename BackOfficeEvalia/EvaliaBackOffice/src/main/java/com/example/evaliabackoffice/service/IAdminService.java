package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Admin;


import java.util.List;

public interface IAdminService {

    public Admin addAdmin(Admin admin);


    void deleteAdmin(Long idAdmin);

    public List<Admin> getAllAdmins();


    Admin DetailsAdmin(Long idAdmin);

    Admin updateAdmin(Admin admin, Long id);


}
