package Util;

import lombok.Getter;

@Getter
public class ColumnUtil {
    private final String camelCaseColumn;

    public ColumnUtil(String column) {
        camelCaseColumn = column.replace(column.charAt(0), Character.toUpperCase(column.charAt(0)));
    }
}
