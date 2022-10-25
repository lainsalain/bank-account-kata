package utils;

import java.util.List;

public class ConsoleStatementPrinter implements StatementPrinter {
    @Override
    public void print(List<String> lines) {
        for(String line: lines){
            System.out.println(line);
        }
    }
}
