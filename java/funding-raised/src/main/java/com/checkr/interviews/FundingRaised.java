package com.acme.interviews;

import java.util.*;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;

public class FundingRaised {

    // Константи за индекси в CSV файла
    private static final int PERMALINK_INDEX = 0;
    private static final int COMPANY_NAME_INDEX = 1;
    private static final int NUMBER_EMPLOYEES_INDEX = 2;
    private static final int CATEGORY_INDEX = 3;
    private static final int CITY_INDEX = 4;
    private static final int STATE_INDEX = 5;
    private static final int FUNDED_DATE_INDEX = 6;
    private static final int RAISED_AMOUNT_INDEX = 7;
    private static final int RAISED_CURRENCY_INDEX = 8;
    private static final int ROUND_INDEX = 9;

    // Метод за филтриране на записите според зададените опции
    public static List<Map<String, String>> filterByOptions(List<String[]> csvData, Map<String, String> options) {
        List<String[]> filteredData = new ArrayList<>(csvData);

        for (Map.Entry<String, String> option : options.entrySet()) {
            String key = option.getKey();
            String value = option.getValue();
            filteredData.removeIf(row -> !matchesOption(row, key, value));
        }

        return mapCsvData(filteredData);
    }

    // Метод за проверка дали даден ред от CSV-то съвпада с опцията
    private static boolean matchesOption(String[] row, String key, String value) {
        switch (key) {
            case "company_name":
                return row[COMPANY_NAME_INDEX].equalsIgnoreCase(value);
            case "city":
                return row[CITY_INDEX].equalsIgnoreCase(value);
            case "state":
                return row[STATE_INDEX].equalsIgnoreCase(value);
            case "round":
                return row[ROUND_INDEX].equalsIgnoreCase(value);
            default:
                return true;
        }
    }

    // Четене на CSV файла и връщане на данните без заглавния ред
    public static List<String[]> readCsvData(String filePath) throws IOException {
        List<String[]> csvData = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            csvData = reader.readAll();
        }
        csvData.remove(0); // Премахваме заглавния ред
        return csvData;
    }

    // Метод за преобразуване на CSV данните в списък от карти
    public static List<Map<String, String>> mapCsvData(List<String[]> csvData) {
        List<Map<String, String>> mappedData = new ArrayList<>();
        for (String[] row : csvData) {
            Map<String, String> record = new HashMap<>();
            record.put("permalink", row[PERMALINK_INDEX]);
            record.put("company_name", row[COMPANY_NAME_INDEX]);
            record.put("number_employees", row[NUMBER_EMPLOYEES_INDEX]);
            record.put("category", row[CATEGORY_INDEX]);
            record.put("city", row[CITY_INDEX]);
            record.put("state", row[STATE_INDEX]);
            record.put("funded_date", row[FUNDED_DATE_INDEX]);
            record.put("raised_amount", row[RAISED_AMOUNT_INDEX]);
            record.put("raised_currency", row[RAISED_CURRENCY_INDEX]);
            record.put("round", row[ROUND_INDEX]);
            mappedData.add(record);
        }
        return mappedData;
    }

    // Основен метод за търсене на записи, съответстващи на дадените критерии
    public static List<Map<String, String>> findFunding(Map<String, String> options) throws IOException {
        List<String[]> csvData = readCsvData("startup_funding.csv");
        return filterByOptions(csvData, options);
    }

    // Метод за намиране на един конкретен запис
    public static Map<String, String> findSingleEntry(Map<String, String> options) throws IOException, NoSuchEntryException {
        List<String[]> csvData = readCsvData("startup_funding.csv");
        List<Map<String, String>> filteredResults = filterByOptions(csvData, options);

        if (filteredResults.isEmpty()) {
            throw new NoSuchEntryException("No matching entry found.");
        }

        return filteredResults.get(0);
    }

    // Main метод за тестване
    public static void main(String[] args) {
        try {
            Map<String, String> searchOptions = new HashMap<>();
            searchOptions.put("company_name", "Facebook");
            searchOptions.put("round", "a");

            List<Map<String, String>> results = FundingRaised.findFunding(searchOptions);
            System.out.println("Number of results: " + results.size());

        } catch (IOException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        } catch (NoSuchEntryException e) {
            System.out.println(e.getMessage());
        }
    }
}

// Custom exception class
class NoSuchEntryException extends Exception {
    public NoSuchEntryException(String message) {
        super(message);
    }
}
