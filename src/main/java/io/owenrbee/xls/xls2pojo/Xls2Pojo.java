package io.owenrbee.xls.xls2pojo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A utility class to convert Excel sheets to a list of Plain Old Java Objects
 * (POJOs).
 * This class provides static methods to handle the conversion from an Excel
 * file
 * (provided as a file path or an InputStream) to a List of objects of a
 * specified class.
 */
@Slf4j
public class Xls2Pojo {

    private Xls2Pojo() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts the first sheet of an Excel file to a list of POJOs.
     *
     * @param <T>           the type of the output POJO.
     * @param inputFilePath the path to the input Excel file.
     * @param outputClass   the class of the output POJO.
     * @return a list of POJOs, or an empty list if an error occurs.
     * @throws IOException if an error occurs while reading the file or converting
     *                     the data.
     */
    public static <T> List<T> convert(String inputFilePath, Class<T> outputClass)
            throws IOException {

        try (FileInputStream excelFile = new FileInputStream(inputFilePath)) {
            return convert(excelFile, new SheetReference(0), outputClass); // Default to the first sheet
        }
    }

    /**
     * Converts the first sheet of an Excel file provided as an InputStream to a
     * list of POJOs.
     *
     * @param <T>         the type of the output POJO.
     * @param inputStream the InputStream of the Excel file.
     * @param outputClass the class of the output POJO.
     * @return a list of POJOs, or an empty list if an error occurs.
     * @throws IOException if an error occurs while reading the file or converting
     *                     the data.
     */
    public static <T> List<T> convert(InputStream inputStream, Class<T> outputClass)
            throws IOException {
        return convert(inputStream, new SheetReference(0), outputClass); // Default to the first sheet

    }

    /**
     * Converts specific sheets from an Excel file, identified by their indices,
     * into a single list of POJOs.
     *
     * @param <T>           the type of the output POJO.
     * @param inputFilePath the path to the input Excel file.
     * @param outputClass   the class of the output POJO.
     * @param indices       an array of sheet indices to convert.
     * @return a list of POJOs from the specified sheets, or an empty list if an
     *         error occurs.
     * @throws IOException if an error occurs while reading the file or converting
     *                     the data.
     */
    public static <T> List<T> convert(String inputFilePath, Class<T> outputClass, int... indices)
            throws IOException {

        List<T> resuList = new ArrayList<>();
        try (FileInputStream excelFile = new FileInputStream(inputFilePath)) {
            for (int index : indices) {
                resuList.addAll(convert(excelFile, new SheetReference(index), outputClass));
            }

        }

        return resuList;
    }

    /**
     * Converts specific sheets from an Excel file, identified by their names, into
     * a single list of POJOs.
     *
     * @param <T>           the type of the output POJO.
     * @param inputFilePath the path to the input Excel file.
     * @param outputClass   the class of the output POJO.
     * @param sheetNames    an array of sheet names to convert.
     * @return a list of POJOs from the specified sheets, or an empty list if an
     *         error occurs.
     * @throws IOException if an error occurs while reading the file or converting
     *                     the data.
     */
    public static <T> List<T> convert(String inputFilePath, Class<T> outputClass, String... sheetNames)
            throws IOException {

        List<T> resuList = new ArrayList<>();
        try (FileInputStream excelFile = new FileInputStream(inputFilePath)) {
            for (String sheetName : sheetNames) {
                resuList.addAll(convert(excelFile, new SheetReference(sheetName), outputClass));
            }

        }

        return resuList;
    }

    /**
     * Converts a single sheet from an Excel file, provided as an InputStream, to a
     * list of POJOs.
     *
     * @param <T>            the type of the output POJO.
     * @param inputStream    the InputStream of the Excel file.
     * @param sheetReference a reference to the sheet to be converted (by index or
     *                       name).
     * @param outputClass    the class of the output POJO.
     * @return a list of POJOs, or an empty list if an error occurs.
     * @throws IOException if an error occurs while reading the file or converting
     *                     the data.
     */
    public static <T> List<T> convert(InputStream inputStream, SheetReference sheetReference, Class<T> outputClass)
            throws IOException {

        List<FieldInfo> fieldInfo = getFieldInfo(outputClass);

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = sheetReference.getName() != null ? workbook.getSheet(sheetReference.getName())
                    : workbook.getSheetAt(sheetReference.getIndex()); // Get the first sheet

            Xls2PojoService service = new Xls2PojoService(sheet, fieldInfo);

            List<String> jsonList = service.convert();
            return jsonList.stream()
                    .map(json -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            return objectMapper.readValue(json, outputClass);
                        } catch (Exception e) {
                            log.error("Error converting JSON to POJO: {}", json, e);
                            return null; // Handle conversion error
                        }
                    })
                    .filter(Objects::nonNull) // Filter out null objects
                    .collect(Collectors.toList());

        }
    }

    /**
     * Retrieves metadata about the fields of a given class.
     * This information is used to map Excel columns to POJO fields.
     *
     * @param klass the class to analyze.
     * @return a list of {@link FieldInfo} objects containing field metadata.
     */
    private static List<FieldInfo> getFieldInfo(Class<?> klass) {
        // get declared field names via reflection
        Field[] fields = klass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setName(field.getName());
                    fieldInfo.setType(field.getType().getSimpleName());
                    return fieldInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * A helper class to reference an Excel sheet by either its name or its index.
     */
    @Getter
    static class SheetReference {

        public SheetReference(int index) {
            this.index = index;
        }

        public SheetReference(String name) {
            this.name = name;
        }

        private String name;
        private int index;

    }
}
