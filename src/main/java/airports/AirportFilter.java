package airports;

import java.util.ArrayList;
import java.util.List;


public class AirportFilter {
    private final List<FilterCondition> conditions;

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
