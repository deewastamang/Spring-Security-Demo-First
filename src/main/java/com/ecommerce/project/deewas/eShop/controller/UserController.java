package com.ecommerce.project.deewas.eShop.controller;

import com.ecommerce.project.deewas.eShop.entity.User;
import com.ecommerce.project.deewas.eShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/api")
public class UserController {

    @Autowired
    private UserService userService;

    //CREATE A NEW USER
    @PostMapping(value = "/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //READ: GET ALL USERS
    @GetMapping(value = "/user")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //READ: GET USER BY ID
    @GetMapping(value = "/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> result = userService.getUserById(id);
        User user = null;
        if(result.isPresent()) {
            user = result.get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);

        /**
         *  or in short, you can also write:
         *  Optional<User> user = userService.getUserById(id);
         *         return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
         *                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
         */
    }

    //UPDATE
    @PutMapping(value = "/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userToUpdate) {
        Optional<User> user = userService.updateUser(id, userToUpdate);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //DELETE
    @DeleteMapping(value = "/user/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
