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
 * 食物单位控制器 
 */
@RestController()
@RequestMapping("/FoodUnit")
public class FoodUnitController {
    @Autowired
    private  FoodUnitService FoodUnitService;
    @Autowired
    private FoodUnitMapper FoodUnitMapper;
    /**
     * 食物单位分页查询
     */
    @RequestMapping(value = "/List", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<FoodUnitDto> List(@RequestBody FoodUnitPagedInput input)  {
        return FoodUnitService.List(input);
    }
     /**
     * 单个食物单位查询接口
     */
    @RequestMapping(value = "/Get", method = RequestMethod.POST)
    @SneakyThrows
    public FoodUnitDto Get(@RequestBody FoodUnitPagedInput input) {

        return FoodUnitService.Get(input);
    }
  
    /**
     * 食物单位创建或则修改
     */
    @RequestMapping(value = "/CreateOrEdit", method = RequestMethod.POST)
    public FoodUnitDto CreateOrEdit(@RequestBody FoodUnitDto input) throws Exception {
        return FoodUnitService.CreateOrEdit(input);
    }
    /**
     * 食物单位删除
     */
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public void Delete(@RequestBody IdInput input)
    {
        FoodUnitService.Delete(input);
    }

    /**
     * 食物单位批量删除
     */
    @RequestMapping(value = "/BatchDelete", method = RequestMethod.POST)
    public void BatchDelete(@RequestBody IdsInput input)
    {
        FoodUnitService.BatchDelete(input);
    }
  

 
}
