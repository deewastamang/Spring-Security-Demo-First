package com.ecommerce.project.deewas.eShop.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<String> getAdmin() {
        return new ResponseEntity<>("get from admin", HttpStatus.OK);
    }
    @PostMapping("/admin")
    public ResponseEntity<String> postAdmin() {
        return new ResponseEntity<>("post from admin", HttpStatus.OK);
    }
    @PutMapping("/admin")
    public ResponseEntity<String> putAdmin() {
        return new ResponseEntity<>("put from admin", HttpStatus.OK);
    }
    @DeleteMapping("/admin")
    public ResponseEntity<String> deleteAdmin() {
        return new ResponseEntity<>("delete from admin", HttpStatus.OK);
    }

}
