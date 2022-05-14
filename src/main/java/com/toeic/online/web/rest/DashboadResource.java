package com.toeic.online.web.rest;

import com.toeic.online.service.DashBoarchService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Transactional
public class DashboadResource {

    private final DashBoarchService dashBoadService;

    public DashboadResource(DashBoarchService dashBoadService) {
        this.dashBoadService = dashBoadService;
    }

    @GetMapping("/dashboard/home")
    public ResponseEntity<?> getHomeDashBoard(@RequestParam("code") String teacherCode){
        Map<String, Object> result = dashBoadService.getData(teacherCode);
        return ResponseEntity.ok().body(result);
    }
}
