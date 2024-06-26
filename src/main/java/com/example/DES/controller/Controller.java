package com.example.DES.controller;

import com.example.DES.entity.ContextHolder;
import com.example.DES.service.DESService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    @PostMapping(path = "/get-html")
    public String getHtml(@RequestParam("file") MultipartFile file) throws Exception{
        String html = "";
        try  {
            File tempFile = File.createTempFile("temp", null);
            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
            FileInputStream fileInputStream = new FileInputStream(tempFile);
            XWPFDocument document = new XWPFDocument(fileInputStream);
            StringBuilder htmlContent = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()){
                htmlContent.append("<p>");
                for (XWPFRun xwpfRun : paragraph.getRuns()){
                    String style = desService.getRunStyle(xwpfRun);
                    htmlContent.append("<span").append(style).append(">")
                            .append(xwpfRun.text()).append("</span>");
                }
                htmlContent.append("</p>");
            }
            html = htmlContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }
}
