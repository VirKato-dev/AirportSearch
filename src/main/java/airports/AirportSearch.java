package airports;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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

}

