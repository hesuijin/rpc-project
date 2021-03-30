package com.example.demo.builderDemo;

import lombok.*;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Builder(toBuilder =  true)
public class UserInfo {
  private String name;
  private String address;

}