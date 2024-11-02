package com.ecommerce.project.deewas.eShop.dto;

import com.ecommerce.project.deewas.eShop.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private Integer age;
    private Gender gender;
    private String address;

    public boolean isPasswordMatching() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }

}