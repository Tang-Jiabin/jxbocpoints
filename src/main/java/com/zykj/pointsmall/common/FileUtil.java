package com.zykj.pointsmall.common;

import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 文件工具
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2018-11-01
 */
@Slf4j
public class FileUtil {


    public static String getFileSuffix(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            String string = fileName.trim();
            int index = fileName.lastIndexOf(".");
            if (index > 0 && index < string.length() - 1) {
                return string.substring(index + 1);
            }
        }
        return "";
    }

    public static void exportFile(String[] title, List<Map<String, String>> list, String path) {

        //创建excel工作簿
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        //创建工作表sheet
        SXSSFSheet sheet = workbook.createSheet();
        //创建第一行
        SXSSFRow row = sheet.createRow(0);
        //列
        SXSSFCell cell = null;
        //插入第一行数据的表头
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
        }

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int a = 0; a < title.length; a++) {
                cell = row.createCell(a);
                cell.setCellValue(String.valueOf(list.get(i).get(title[a])));
            }
        }

        File file = new File(path);
        FileOutputStream stream = null;
        try {
            file.createNewFile();
            //将excel写入
            stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Map<String, String>> readExcel(InputStream inputStream, String sheetName) {

        //定义工作簿
        XSSFWorkbook xssfWorkbook = null;
        Workbook wk = null;

        try {
            xssfWorkbook = new XSSFWorkbook(inputStream);
            wk = StreamingReader.builder()
                    .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                    .bufferSize(8192)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(inputStream);
        } catch (Exception e) {
            System.out.println("Excel data file cannot be found!");
        }

        //定义工作表
        XSSFSheet xssfSheet;
        if (sheetName.equals("")) {
            // 默认取第一个子表
            xssfSheet = xssfWorkbook.getSheetAt(0);
        } else {
            xssfSheet = xssfWorkbook.getSheet(sheetName);
        }

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        //定义行
        //默认第一行为标题行，index = 0
        XSSFRow titleRow = xssfSheet.getRow(0);

        //循环取每行的数据
        for (int rowIndex = 1; rowIndex < xssfSheet.getPhysicalNumberOfRows(); rowIndex++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowIndex);
            if (xssfRow == null) {
                continue;
            }

            Map<String, String> map = new LinkedHashMap<String, String>();
            //循环取每个单元格(cell)的数据

//            map.put(getString(xssfRow.getCell(0)), getString(xssfRow.getCell(1)));
            for (int cellIndex = 0; cellIndex < xssfRow.getPhysicalNumberOfCells(); cellIndex++) {
                XSSFCell titleCell = titleRow.getCell(cellIndex);
                XSSFCell xssfCell = xssfRow.getCell(cellIndex);
                map.put(getString(titleCell), getString(xssfCell));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 把单元格的内容转为字符串
     *
     * @param xssfCell 单元格
     * @return 字符串
     */
    public static String getString(XSSFCell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(xssfCell.getNumericCellValue());
        } else if (xssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else {
            return xssfCell.getStringCellValue();
        }
    }


    public static <T> List<List<T>> averageAssign(List<T> list, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = list.size() % n;  //(先计算出余数)
        int number = list.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = list.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = list.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
