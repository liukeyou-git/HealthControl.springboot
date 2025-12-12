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
 * 运动单位控制器 
 */
@RestController()
@RequestMapping("/SportUnit")
public class SportUnitController {
    @Autowired
    private  SportUnitService SportUnitService;
    @Autowired
    private SportUnitMapper SportUnitMapper;
    /**
     * 运动单位分页查询
     */
    @RequestMapping(value = "/List", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<SportUnitDto> List(@RequestBody SportUnitPagedInput input)  {
        return SportUnitService.List(input);
    }
     /**
     * 单个运动单位查询接口
     */
    @RequestMapping(value = "/Get", method = RequestMethod.POST)
    @SneakyThrows
    public SportUnitDto Get(@RequestBody SportUnitPagedInput input) {

        return SportUnitService.Get(input);
    }
  
    /**
     * 运动单位创建或则修改
     */
    @RequestMapping(value = "/CreateOrEdit", method = RequestMethod.POST)
    public SportUnitDto CreateOrEdit(@RequestBody SportUnitDto input) throws Exception {
        return SportUnitService.CreateOrEdit(input);
    }
    /**
     * 运动单位删除
     */
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public void Delete(@RequestBody IdInput input)
    {
        SportUnitService.Delete(input);
    }

    /**
     * 运动单位批量删除
     */
    @RequestMapping(value = "/BatchDelete", method = RequestMethod.POST)
    public void BatchDelete(@RequestBody IdsInput input)
    {
        SportUnitService.BatchDelete(input);
    }
  

 
}
