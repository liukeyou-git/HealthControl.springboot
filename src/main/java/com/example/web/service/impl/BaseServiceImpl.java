package com.example.web.service.impl;

import java.util.List;

import com.example.web.mapper.AppUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.service.BaseService;

import lombok.SneakyThrows;

@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    private AppUserMapper AppUserMapper;

   
}
