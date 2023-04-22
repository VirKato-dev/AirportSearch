package airports;


public record FilterCondition(int columnIndex, String operator, String value) {

    public boolean matches(String[] rowData) {
        String columnValue = rowData[columnIndex - 1];

        return switch (operator) {
            case "=" -> columnValue.equals(value);
            case "<>" -> !columnValue.equals(value);
            case ">" -> columnValue.compareTo(value) > 0;
            case "<" -> columnValue.compareTo(value) < 0;
            default -> false;
        };
    }
}