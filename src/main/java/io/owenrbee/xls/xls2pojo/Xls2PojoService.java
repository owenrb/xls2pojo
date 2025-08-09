package io.owenrbee.xls.xls2pojo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for the core logic of converting an Excel sheet to
 * a list of JSON strings.
 * It reads an Excel sheet row by row, maps the cell values to the fields of a
 * target POJO,
 * and serializes each row's data into a JSON string.
 */
@Slf4j
public class Xls2PojoService {

    private final Sheet sheet;
    private final List<String> headerNames;
    private final List<FieldInfo> fieldInfoList;

    /**
     * Constructs a new Xls2PojoService.
     *
     * @param sheet         the Excel sheet to be processed.
     * @param fieldInfoList a list of metadata about the fields of the target POJO.
     */
    public Xls2PojoService(Sheet sheet, List<FieldInfo> fieldInfoList) {
        this.sheet = sheet;
        this.fieldInfoList = fieldInfoList;

        headerNames = getRowValues(sheet.getRow(0));
    }

    /**
     * Converts the Excel sheet into a list of JSON strings.
     * Each JSON string represents a row from the sheet, mapped to the target POJO's
     * structure.
     *
     * @return a list of JSON strings, where each string is a JSON representation of
     *         a row.
     */
    public List<String> convert() {

        List<String> result = new ArrayList<>();

        for (Row row : sheet) {

            if (row.getRowNum() == 0) {

                continue;
            }

            List<String> rowValues = getRowValues(row);

            try {

                Map<String, Object> map = new HashMap<>();

                for (int i = 0; i < headerNames.size(); i++) {
                    String headerName = headerNames.get(i);
                    String fieldName = getFieldName(headerName);
                    String fieldType = getFieldType(fieldName);

                    String value = rowValues.get(i);
                    if (value == null) {
                        continue; // Skip if the cell is empty
                    }

                    applyValue(map, fieldName,
                            "Date".equals(fieldType) ? row.getCell(i).getDateCellValue().getTime() + "" : value);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(map);

                result.add(jsonString);

            } catch (IllegalArgumentException | JsonProcessingException | ParseException e) {
                log.error("", e);
            }

        }

        return result;
    }

    /**
     * Parses a string value and puts it into the map with the correct data type.
     *
     * @param map       the map to store the parsed value.
     * @param fieldName the name of the field.
     * @param value     the string value to be parsed.
     * @throws ParseException if the value cannot be parsed.
     */
    private void applyValue(Map<String, Object> map, String fieldName, String value) throws ParseException {
        if (value == null || value.isEmpty()) {
            return; // Skip empty values
        }

        String fieldType = getFieldType(fieldName);
        if (fieldType == null) {
            log.warn("Field type not found for field name: {}", fieldName);
            return; // Skip if no matching field type
        }

        switch (fieldType) {
            case "Date":
                long time = Long.parseLong(value);
                map.put(fieldName, new Date(time));
                break;
            case "int":
            case "Integer":
                map.put(fieldName, Double.valueOf(value).intValue());
                break;
            case "long":
            case "Long":
                map.put(fieldName, Double.valueOf(value).longValue());
                break;
            case "float":
            case "Float":
                map.put(fieldName, Float.parseFloat(value));
                break;
            case "double":
            case "Double":
            case "Decimal":
                map.put(fieldName, Double.parseDouble(value));
                break;
            case "boolean":
            case "Boolean":
                map.put(fieldName, Boolean.parseBoolean(value.toLowerCase()));
                break;
            default:
                map.put(fieldName, value);
        }
    }

    /**
     * Retrieves the data type of a field by its name.
     *
     * @param fieldName the name of the field.
     * @return the string representation of the field's data type, or null if not
     *         found.
     */
    private String getFieldType(String fieldName) {
        for (FieldInfo fieldInfo : fieldInfoList) {
            if (fieldInfo.getName().equalsIgnoreCase(fieldName)) {
                return fieldInfo.getType();
            }
        }
        log.warn("Field type not found for field name: {}", fieldName);
        return null;
    }

    /**
     * Extracts all cell values from a given row and returns them as a list of
     * strings.
     *
     * @param row the row to process.
     * @return a list of cell values as strings.
     */
    private List<String> getRowValues(Row row) {
        int first = 0;
        int last = row.getLastCellNum();
        return java.util.stream.IntStream.range(first, last)
                .mapToObj(i -> getCellValue(row.getCell(i)))
                .toList();
    }

    /**
     * Retrieves the value of a cell as a string, handling different cell types.
     *
     * @param cell the cell to read.
     * @return the string value of the cell, or null if the cell is empty.
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null; // Handle other types as needed
        }
    }

    /**
     * Finds the corresponding POJO field name for a given Excel column header name.
     * The matching is case-insensitive and ignores whitespace in the header name.
     *
     * @param headerName the header name from the Excel sheet.
     * @return the matching field name, or null if no match is found.
     */
    private String getFieldName(String headerName) {

        // Find the field name that matches the header name
        for (FieldInfo fielInfo : fieldInfoList) {
            String fieldName = fielInfo.getName();
            if (fieldName.equalsIgnoreCase(headerName.replaceAll("\\s+", ""))) {
                return fieldName;
            }
        }

        return null;
    }

}
