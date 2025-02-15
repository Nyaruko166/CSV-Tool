package me.nyaruko166.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CSVHelper {

    private static final Logger log = LogManager.getLogger(CSVHelper.class);
    private static final CSVFormat format = CSVFormat.DEFAULT.builder()
                                                             .setDelimiter(',')
                                                             .setTrim(true)
                                                             .get();

    /**
     * Reads a CSV file using Apache Commons
     *
     * @param filePath Path to the original CSV file.
     * @param regex    Regex for filter.
     * @return List<String> of filtered field in CSV file
     */
    public static List<String> readCSVFile(String filePath, String regex) {
        log.info("Reading CSV file: {}", filePath);
        List<String> lstData = new ArrayList<>();
        Pattern latinOnly = Pattern.compile(regex);

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = format.parse(reader)) {

            for (CSVRecord record : csvParser) {
                for (int i = 0; i < record.size(); i++) {
                    if (!regex.isBlank()) {
                        if (latinOnly.matcher(record.get(i)).matches()) {
                            lstData.add(record.get(i));
                        }
                    } else {
                        lstData.add(record.get(i));
                    }
                }
            }
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
        }
        return lstData;
    }

    /**
     * Reads a CSV file using Apache Commons
     *
     * @param filePath Path to the original CSV file.
     * @return List<CSVRecord> of filtered field in CSV file
     */
    public static List<CSVRecord> readCSVFile(String filePath) {
        List<String> lstData = new ArrayList<>();

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = format.parse(reader)) {
            return csvParser.getRecords();
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
            return null;
        }
    }

    public static List<List<String>> replaceInCSVFile(String filePath, Map<String, String> replacements) {
        List<CSVRecord> lstRecords = readCSVFile(filePath);
        List<List<String>> recordsList = new ArrayList<>();

        for (CSVRecord record : lstRecords) {
            List<String> row = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                String field = record.get(i);
                // Iterate over each target-replacement pair.
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    String target = entry.getKey();
                    String replacement = entry.getValue();
                    // If the field contains the target substring, replace it.
                    if (field.contains(target)) {
                        field = field.replace(target, replacement);
                    }
                }
                row.add(field);
            }
            recordsList.add(row);
        }
        return recordsList;
    }

    public static void writeCSVFile(String outputPath, List<List<String>> recordsList) {
        try (Writer writer = new FileWriter(outputPath, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (List<String> row : recordsList) {
                printer.printRecord(row);
            }
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
        }
    }

//    public static void main(String[] args) {
//
//        List<String> lstTest = List.of(
//                "After I was \"\"\"purchased,\"\"\" Daigneux's {sech} first order was to write down the lives of Earthlings and how to take care of them.",
//                "Oh... You're going to ride me again. I'm so scared! {skbidi} (Not)",
//                "Chapter 7 of the Earthling Breeding Manual said that Earthlings can be manipulated by their sexual desires.");
//
//        Pattern pattern = Pattern.compile("^[A-Za-z0-9\\s,.'!?:;\"(){}\\-]+$", Pattern.CASE_INSENSITIVE);
//
//        for (String test : lstTest) {
//            if (pattern.matcher(test).matches()) {
//                System.out.println(test + " | is legit");
//            } else {
//                System.out.println(test + " | is not legit");
//            }
//        }

//        List<String> lstRecord = readCSVFile("C:\\Users\\ADMIN\\Documents\\Code\\SideProject\\SkibidiTool\\temp\\TextData-resources.assets-963.txt", "^[A-Za-z0-9\\s,.'!?:;\"()\\-]+$");
//        ExcelHelper.createExcelFile(lstRecord, "test");
//    }

}
