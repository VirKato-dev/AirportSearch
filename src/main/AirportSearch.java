package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AirportSearch {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Укажите путь к файлу CSV с данными аэропортов.");
            return;
        }

        String csvPath = args[0];
        AirportIndex airportIndex = new AirportIndex(csvPath);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Введите фильтр: ");
                String filterInput = scanner.nextLine();
                AirportFilter filter = airportIndex.parseFilter(filterInput);

                System.out.print("Введите начало имени аэропорта: ");
                String prefix = scanner.nextLine();

                if (prefix.equalsIgnoreCase("!quit")) {
                    break;
                }

                List<String[]> searchResults = airportIndex.searchAirportsByPrefix(prefix, filter);
                searchResults.sort(Comparator.comparing(row -> row[1]));

                for (String[] rowData : searchResults) {
                    System.out.println(rowData[1] + "[" + String.join(",", rowData) + "]");
                }

                System.out.println("Найдено строк: " + searchResults.size());
            }
        }
    }

    static class AirportIndex {
        private List<String[]> data;

        public AirportIndex(String csvPath) throws IOException {
            data = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    data.add(rowData);
                }
            }
        }

        public List<String[]> searchAirportsByPrefix(String prefix, AirportFilter filter) {
            List<String[]> results = new ArrayList<>();

            for (String[] rowData : data) {
                String airportName = rowData[1];

                if (airportName.toLowerCase().startsWith(prefix.toLowerCase()) && filter.matches(rowData)) {
                    results.add(rowData);
                }
            }

            return results;
        }

        private AirportFilter parseFilter(String filterInput) {
            AirportFilter filter = new AirportFilter();
            Pattern pattern = Pattern.compile("column\\[(\\d+)\\](=|<>|>|<)([^&|]+)");
            Matcher matcher = pattern.matcher(filterInput);

            while (matcher.find()) {
                int columnIndex = Integer.parseInt(matcher.group(1));
                String operator = matcher.group(2);
                String value = matcher.group(3).trim();
                FilterCondition condition = new FilterCondition(columnIndex, operator, value);
                filter.addCondition(condition);
            }

            return filter;
        }
    }

    static class AirportFilter {
        private List<FilterCondition> conditions;

        public AirportFilter() {
            this.conditions = new ArrayList<>();
        }

        public void addCondition(FilterCondition condition) {
            conditions.add(condition);
        }

        public boolean matches(String[] rowData) {
            for (FilterCondition condition : conditions) {
                if (!condition.matches(rowData)) {
                    return false;
                }
            }
            return true;
        }
    }

    static class FilterCondition {
        private int columnIndex;
        private String operator;
        private String value;

        public FilterCondition(int columnIndex, String operator, String value) {
            this.columnIndex = columnIndex;
            this.operator = operator;
            this.value = value;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public String getOperator() {
            return operator;
        }

        public String getValue() {
            return value;
        }

        public boolean matches(String[] rowData) {
            String columnValue = rowData[columnIndex - 1];

            switch (operator) {
                case "=":
                    return columnValue.equals(value);
                case "<>":
                    return !columnValue.equals(value);
                case ">":
                    return columnValue.compareTo(value) > 0;
                case "<":
                    return columnValue.compareTo(value) < 0;
                default:
                    return false;
            }
        }
    }
}

