package com.example.web.controller;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import com.example.web.tools.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.SneakyThrows;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 健康知识控制器 
 */
@RestController()
@RequestMapping("/HealthArticle")
public class HealthArticleController {
    @Autowired
    private  HealthArticleService HealthArticleService;
    @Autowired
    private HealthArticleMapper HealthArticleMapper;
    /**
     * 健康知识分页查询
     */
    @RequestMapping(value = "/List", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<HealthArticleDto> List(@RequestBody HealthArticlePagedInput input)  {
        return HealthArticleService.List(input);
    }
     /**
     * 单个健康知识查询接口
     */
    @RequestMapping(value = "/Get", method = RequestMethod.POST)
    @SneakyThrows
    public HealthArticleDto Get(@RequestBody HealthArticlePagedInput input) {

        return HealthArticleService.Get(input);
    }
  
    /**
     * 健康知识创建或则修改
     */
    @RequestMapping(value = "/CreateOrEdit", method = RequestMethod.POST)
    public HealthArticleDto CreateOrEdit(@RequestBody HealthArticleDto input) throws Exception {
        return HealthArticleService.CreateOrEdit(input);
    }
    /**
     * 健康知识删除
     */
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public void Delete(@RequestBody IdInput input)
    {
        HealthArticleService.Delete(input);
    }

    /**
     * 健康知识批量删除
     */
    @RequestMapping(value = "/BatchDelete", method = RequestMethod.POST)
    public void BatchDelete(@RequestBody IdsInput input)
    {
        HealthArticleService.BatchDelete(input);
    }
  

 
}
