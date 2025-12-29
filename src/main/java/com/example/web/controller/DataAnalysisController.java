package com.example.web.controller;

import com.example.web.service.DataAnalysisService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/DataAnalysis")
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @SneakyThrows
    @RequestMapping(value = "/GetAllAnalysisData", method = RequestMethod.POST)
    public HashMap<String, Object> GetAllAnalysisData() {
        return dataAnalysisService.GetAllAnalysisData();
    }
}