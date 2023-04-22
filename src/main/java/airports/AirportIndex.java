package airports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AirportIndex {
    private final List<String[]> data = new ArrayList<>();

    public AirportIndex(String csvPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("java.class.path"),csvPath)))) {
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

    public AirportFilter parseFilter(String filterInput) {
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