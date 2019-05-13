package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.DormService;
import com.zxh.dormMG.Domain.Dorm;
import com.zxh.dormMG.Domain.Student;
import com.zxh.dormMG.dto.DataTableDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.FilePathUtil;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Api(tags = {"Dorm"}, description = "the Dorm API")
@RestController
@RequestMapping(value = "/dorm")
public class DormController {
    private static final Logger logger = Logger.getLogger(DormController.class);

    @Autowired
    private DormService dormService;

    @RequestMapping(value = "/dormList", method = RequestMethod.GET)
    @ResponseBody
    public DataTableDto dormList() {
        return new DataTableDto<>(dormService.dormList());
    }

    @RequestMapping(value = "/availableDormList", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<List<Dorm>> availableDormList() {
        return ResultDtoFactory.toAck("S",dormService.availableDormList());
    }

    @RequestMapping(value = "/availablePosList", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<List<Integer>> availablePosList(@RequestParam("dorm")String dorm) {
        return ResultDtoFactory.toAck("S",dormService.availablePosList(dorm));
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/dormStudents", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<List<Student>> dormStudents(@RequestParam("id") String id) {
        return ResultDtoFactory.toAck("S",dormService.dormStudents(id));
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/dormDelete", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> dormDelete(@RequestParam("id") String id) {
        return dormService.dormDelete(id);
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/dormAdd", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> dormAdd(Dorm dorm) {
//        int vol = 4;
//        try{
//            vol = Integer.valueOf(volume);
//        }catch (NumberFormatException e){
//            return ResultDtoFactory.toAck("F","寝室容量格式不正确");
//        }
//        Dorm dorm = new Dorm(id,vol);
        return (dormService.dormAdd(dorm));
    }

    @RequiresRoles(value = {"admin","root"},logical = Logical.OR)
    @RequestMapping(value = "/importDorms", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> importDorms(@RequestParam("file")MultipartFile file) {
//        return (studentService.studentAdd(student));
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String path = FilePathUtil.createUploadFile(fileName);
            File tempFile =new File(path);
            //写文件到本地
            try {
                file.transferTo(tempFile);
                return (dormService.importDorms(tempFile));
            } catch (IOException e) {
                logger.error(e);
                return ResultDtoFactory.toAck("F","导入失败");
            }
        }
        return ResultDtoFactory.toAck("F","导入失败");
    }

    @RequestMapping(value = "/downloadFailedImport", method = RequestMethod.POST)
    @ResponseBody
    public void downloadFailedImport(HttpServletResponse response) {

        try {
            File file = FilePathUtil.getDownloadFilePath("导入失败寝室列表.xls");
            FilePathUtil.download(file, response);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
