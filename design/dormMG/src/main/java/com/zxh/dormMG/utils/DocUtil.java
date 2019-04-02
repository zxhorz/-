package com.zxh.dormMG.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;

public class DocUtil {
    private final static Logger logger = Logger.getLogger(DocUtil.class);


    /**
     * 获取数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static List<List<String>> readExcel(File file) throws Exception {

        // 创建输入流，读取Excel
        InputStream is = new FileInputStream(file.getAbsolutePath());
        // jxl提供的Workbook类
        Workbook wb = Workbook.getWorkbook(is);
        // 只有一个sheet,直接处理
        //创建一个Sheet对象
        Sheet sheet = wb.getSheet(0);
        // 得到所有的行数
        int rows = sheet.getRows();
        // 所有的数据
        List<List<String>> allData = new ArrayList<>();

        for (int j = 0; j < rows; j++) {

            List<String> oneData = new ArrayList<>();
            // 得到每一行的单元格的数据
            Cell[] cells = sheet.getRow(j);
            for (int k = 0; k < cells.length; k++) {

                oneData.add(cells[k].getContents().trim());
            }
            // 存储每一条数据
            allData.add(oneData);
            // 打印出每一条数据
            //System.out.println(oneData);

        }
        return allData;

    }
}
