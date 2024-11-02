package com.ecommerce.project.deewas.eShop.service;

import com.ecommerce.project.deewas.eShop.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    public User createUser(User user);
    public List<User> getAllUsers();
    public Optional<User> getUserById(Long id);
    public Optional<User> updateUser(Long id, User userToUpdate);
    public Boolean deleteUser(Long id);;
}
