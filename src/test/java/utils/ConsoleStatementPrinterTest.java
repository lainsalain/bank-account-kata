package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleStatementPrinterTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    public void setUpStreams() {
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should print a formatted console message in one line")
    void shouldPrintFormattedConsoleMessageInOneLine(){

        ConsoleStatementPrinter statementPrinter = new ConsoleStatementPrinter();
        final List<String> message = List.of("The formatted message is in one line.");
        statementPrinter.print(message);

        assertEquals("The formatted message is in one line.", out.toString().trim());

    }

    @Test
    @DisplayName("Should print a formatted console message in several lines")
    void shouldPrintFormattedConsoleMessageInSeveralLine(){
        ConsoleStatementPrinter printer = new ConsoleStatementPrinter();
        final List<String> message = List.of("The formatted message",
                                                "is in",
                                                "several lines.");
        printer.print(message);
        assertEquals(String.format("The formatted message%nis in%nseveral lines."), out.toString().trim());

    }

    @Test
    @DisplayName("Should print an empty console message")
    void shouldPrintEmptyConsoleMessage(){
        ConsoleStatementPrinter printer = new ConsoleStatementPrinter();
        final List<String> message = List.of("");
        printer.print(message);
        assertEquals("", out.toString().trim());
    }



}