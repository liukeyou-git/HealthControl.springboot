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
 * 食谱控制器 
 */
@RestController()
@RequestMapping("/Recipe")
public class RecipeController {
    @Autowired
    private  RecipeService RecipeService;
    @Autowired
    private RecipeMapper RecipeMapper;
    /**
     * 食谱分页查询
     */
    @RequestMapping(value = "/List", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<RecipeDto> List(@RequestBody RecipePagedInput input)  {
        return RecipeService.List(input);
    }
     /**
     * 单个食谱查询接口
     */
    @RequestMapping(value = "/Get", method = RequestMethod.POST)
    @SneakyThrows
    public RecipeDto Get(@RequestBody RecipePagedInput input) {

        return RecipeService.Get(input);
    }
  
    /**
     * 食谱创建或则修改
     */
    @RequestMapping(value = "/CreateOrEdit", method = RequestMethod.POST)
    public RecipeDto CreateOrEdit(@RequestBody RecipeDto input) throws Exception {
        return RecipeService.CreateOrEdit(input);
    }
    /**
     * 食谱删除
     */
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public void Delete(@RequestBody IdInput input)
    {
        RecipeService.Delete(input);
    }

    /**
     * 食谱批量删除
     */
    @RequestMapping(value = "/BatchDelete", method = RequestMethod.POST)
    public void BatchDelete(@RequestBody IdsInput input)
    {
        RecipeService.BatchDelete(input);
    }
  

 
}
