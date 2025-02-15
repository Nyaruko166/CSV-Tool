package me.nyaruko166.util;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

import java.io.IOException;

public class TerminalHelper {

    public static String colorLine(Terminal terminal, Color color, String content) {

        String colorString = "%s" + content + "\u001B[0m";
        String resultString = switch (color) {
            case BLACK -> String.format(colorString, "\u001B[0;30m");
            case RED -> String.format(colorString, "\u001B[0;31m");
            case GREEN -> String.format(colorString, "\u001B[0;32m");
            case YELLOW -> String.format(colorString, "\u001B[0;33m");
            case BLUE -> String.format(colorString, "\u001B[0;34m");
            case PURPLE -> String.format(colorString, "\u001B[0;35m");
            case CYAN -> String.format(colorString, "\u001B[0;36m");
            case WHITE -> String.format(colorString, "\u001B[0;37m");
        };

        return AttributedString.fromAnsi(resultString).toAnsi(terminal);
    }

    public static void printLn(Terminal terminal, Color color, String content) {
        terminal.writer().println(colorLine(terminal, color, content));
    }

    public static void anyKeyToCont(Terminal terminal) throws IOException {
        printLn(terminal, Color.CYAN, "Press any key to continue...");
        terminal.flush();
        terminal.reader().read();
    }

    public enum Color {

        BLACK,
        RED,
        GREEN,
        YELLOW,
        BLUE,
        PURPLE,
        CYAN,
        WHITE,

    }
}
