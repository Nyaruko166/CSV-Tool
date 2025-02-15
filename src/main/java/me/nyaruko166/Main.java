package me.nyaruko166;

import me.nyaruko166.util.CSVHelper;
import me.nyaruko166.util.ExcelHelper;
import me.nyaruko166.util.TerminalHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final Scanner scanner = new Scanner(System.in);

    private static final KeyMap<String> keyMap = new KeyMap<>();


    public static void main(String[] args) {

        log.info("Checking working directory");
        File workingDir = new File("./temp");
        if (!workingDir.exists()) {
            log.info("Working directory created");
            workingDir.mkdirs();
        }

        // Create a key map to bind keys to specific actions
        keyMap.bind("UP", "w");
        keyMap.bind("DOWN", "s");
        keyMap.bind("SPACE", " ");
        keyMap.bind("ENTER", "e");
        keyMap.bind("NEXT", "d");
        keyMap.bind("PREVIOUS", "a");
        //Todo: Check box still not have quit and back yet
        keyMap.bind("QUIT", "q");

        try (Terminal terminal = TerminalBuilder.builder().system(true).jansi(true).build()) {
            menuConsole(terminal);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private static void convertToExcel(Terminal terminal) throws IOException {
        Path assetFilePath;
        //Quick validate shit
        while (true) {
            TerminalHelper.printLn(terminal, TerminalHelper.Color.GREEN, "Enter path to the asset file:");
            assetFilePath = Path.of(scanner.nextLine().replace("\\", "/"));
            if (!assetFilePath.toFile().exists() || assetFilePath.toFile().isDirectory()) {
                log.error("Please enter a valid path to the asset file!");
            } else if (!assetFilePath.getFileName().toString().endsWith(".txt")) {
                log.error("Please choose correct path to the asset file!");
            } else {
                break;
            }
        }

        String latinOnly = "^[A-Za-z0-9\\s,.'!?:;\"(){}\\-]+$";
        List<String> lstData = CSVHelper.readCSVFile(assetFilePath.toString(), latinOnly);
        log.info("Reading data from asset file completed");

        TerminalHelper.printLn(terminal, TerminalHelper.Color.GREEN, "Enter name of the excel file (without extension):");
        String fileName = scanner.nextLine();
        File outputFile = ExcelHelper.createExcelFile(lstData, fileName);
        log.info("Excel file created at: {}", outputFile.getCanonicalPath());

        TerminalHelper.anyKeyToCont(terminal);
    }

    private static void replaceAssetFile(Terminal terminal) throws IOException {
        Path assetFilePath;
        Path excelFilePath;
        //Quick validate shit
        while (true) {
            TerminalHelper.printLn(terminal, TerminalHelper.Color.GREEN, "Enter path to the asset file:");
            assetFilePath = Path.of(scanner.nextLine().replace("\\", "/"));
            if (!assetFilePath.toFile().exists() || assetFilePath.toFile().isDirectory()) {
                log.error("Please enter a valid path to the asset file!");
            } else if (!assetFilePath.getFileName().toString().endsWith(".txt")) {
                log.error("Please choose correct path to the asset file!");
            } else {
                break;
            }
        }

        while (true) {
            TerminalHelper.printLn(terminal, TerminalHelper.Color.GREEN, "Enter path to the excel file:");
            excelFilePath = Path.of(scanner.nextLine().replace("\\", "/"));
            if (!excelFilePath.toFile().exists() || excelFilePath.toFile().isDirectory()) {
                log.error("Please enter a valid path to the excel file!");
            } else if (!excelFilePath.getFileName().toString().endsWith(".xlsx")) {
                log.error("Please choose correct path to the excel file!");
            } else {
                break;
            }
        }

        Map<String, String> replacements = ExcelHelper.readFromExcelFile(excelFilePath.toString());
        log.info("Reading data from excel file completed");
        List<List<String>> recordsList = CSVHelper.replaceInCSVFile(assetFilePath.toString(), replacements);
        log.info("Replace data from excel file completed");
        Path outputPath = Path.of(assetFilePath.getParent() + "/new_" + assetFilePath.getFileName());
        CSVHelper.writeCSVFile(outputPath.toString(), recordsList);
        log.info("Replacement complete! Modified asset file saved at: {}", outputPath);
        TerminalHelper.anyKeyToCont(terminal);
    }

    // Method to handle the selected option
    private static void handleMenuSelection(String selectedOption, Terminal terminal) throws IOException {
        switch (selectedOption) {
            case "Convert asset file to excel file" -> convertToExcel(terminal);
            case "Replace translated sheet to asset file" -> replaceAssetFile(terminal);
            case "Lan dau thi chay cai nay frfr" -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
//            case "Upload video to YT and send to Discord" -> uploadToYoutube(terminal);
            case "QUIT" -> terminal.writer().println("Goodbye!");
            default -> TerminalHelper.printLn(terminal, TerminalHelper.Color.RED, "Invalid option!");
        }
    }

    private static void menuConsole(Terminal terminal) {
        List<String> options = Arrays.asList(
                "Convert asset file to excel file",
                "Replace translated sheet to asset file",
                "Lan dau thi chay cai nay frfr");

        try {
            BindingReader bindingReader = new BindingReader(terminal.reader());

            int selectedIndex = 0;

            while (true) {

                terminal.puts(InfoCmp.Capability.clear_screen);
                TerminalHelper.printLn(terminal, TerminalHelper.Color.GREEN, "==> Up/Down with W/S");
                TerminalHelper.printLn(terminal, TerminalHelper.Color.GREEN, "==> Quit/Confirm with Q/E");

                printMenu(options, selectedIndex);

                String key = bindingReader.readBinding(keyMap);

                if (key == null) {
                    continue; // Skip if no valid key pressed
                }

                switch (key) {
                    case "UP":
                        selectedIndex = (selectedIndex > 0) ? selectedIndex - 1 : options.size() - 1;
                        break;
                    case "DOWN":
                        selectedIndex = (selectedIndex < options.size() - 1) ? selectedIndex + 1 : 0;
                        break;
                    case "QUIT":
                        TerminalHelper.printLn(terminal, TerminalHelper.Color.RED, "Exiting...");
                        terminal.flush();
                        return;
                    case "ENTER":
                        handleMenuSelection(options.get(selectedIndex), terminal);
                        terminal.writer().println("Selected: " + options.get(selectedIndex));
                        terminal.flush();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    private static void printMenu(List<String> options, int selectedIndex) {
        System.out.println("\u001B[0;36mTool created by Nyaruko166\u001B[0m");
        for (int i = 0; i < options.size(); i++) {
            if (i == selectedIndex) {
                System.out.println("\u001B[38;5;212m> \u001B[0m" + options.get(i));
            } else {
                System.out.println("  " + options.get(i));
            }
        }
    }

}