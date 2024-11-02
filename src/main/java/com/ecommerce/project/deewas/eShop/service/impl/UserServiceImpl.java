package com.ecommerce.project.deewas.eShop.service.impl;

import com.ecommerce.project.deewas.eShop.entity.User;
import com.ecommerce.project.deewas.eShop.repository.UserRepository;
import com.ecommerce.project.deewas.eShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {

        return this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUser(Long id, User userToUpdate) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    //update fields
                    existingUser.setFirstName(userToUpdate.getFirstName());
                    existingUser.setLastName(userToUpdate.getLastName());
                    existingUser.setGender(userToUpdate.getGender());
                    existingUser.setAge(userToUpdate.getAge());
                    existingUser.setEmail(userToUpdate.getEmail());
                    existingUser.setPassword(userToUpdate.getPassword());
                    existingUser.setAddress(userToUpdate.getAddress());
                    return userRepository.save(existingUser);
                });
    }

    @Override
    public Boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
