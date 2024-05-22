package com.example.DES.controller;

import com.example.DES.entity.ContextHolder;
import com.example.DES.service.DESService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping(path = "/api/v1")
@CrossOrigin
@RequiredArgsConstructor
@Log
public class Controller {
    private final DESService desService;
    @GetMapping(path = "/get-secret")
    public String sayHello() throws Exception {
        byte[] rawData = desService.getSecretKey().getEncoded();
        return Base64.getEncoder().encodeToString(rawData);
    }
    @PostMapping(path = "/encrypt")
    public String encrypt(@RequestBody ContextHolder encodeContextHolder) throws Exception {
        return desService.encode(encodeContextHolder.getText(), encodeContextHolder.getSecret_key());
    }
    @PostMapping(path = "/decrypt")
    public String decrypt(@RequestBody ContextHolder decodeContextHolder) throws Exception {
        return desService.decode(decodeContextHolder.getText(), decodeContextHolder.getSecret_key());
    }
}
