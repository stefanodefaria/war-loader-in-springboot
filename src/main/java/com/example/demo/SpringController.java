package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringController {

  @GetMapping("/spring")
  public String spring() {
    return "Spring controller";
  }
}
