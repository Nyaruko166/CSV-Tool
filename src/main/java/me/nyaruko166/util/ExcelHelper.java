package me.nyaruko166.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHelper {

    private static final Logger log = LogManager.getLogger(ExcelHelper.class);

    public static Map<String, String> readFromExcelFile(String filePath) {
        Map<String, String> data = new HashMap<>();
        File excelFile = new File(filePath);
        try (FileInputStream fis = new FileInputStream(excelFile)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                String oldText = row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue();
                String newText = row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue();
                if (!oldText.isEmpty() && !newText.isEmpty()) {
                    data.put(oldText, newText);
                }
            }
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
        }
        return data;
    }

    public static File createExcelFile(List<String> lstData, String fileName) {
        try {
            log.info("Creating excel file...");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Created By Nyaruko166");
            XSSFRow row;
            int rowId = 0;
            for (String data : lstData) {
                row = sheet.createRow(rowId++);
                Cell cell = row.createCell(0);
                cell.setCellValue(data);
            }

            // writing the workbook into the file...
            File outputFile = new File("./temp/%s.xlsx".formatted(fileName));
            FileOutputStream out = new FileOutputStream(outputFile);
            workbook.write(out);
            out.close();
            return outputFile;
//            log.info("Excel file created at: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
            return null;
        }
    }
}
