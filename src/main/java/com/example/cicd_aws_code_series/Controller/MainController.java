package com.example.cicd_aws_code_series.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class MainController {

    @GetMapping("/")
    public String index() {
        String text = "최종 2 : Code Pipeline의 여부를 확인합니다.";
        String now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return text + "\n" + now;
    }
}
