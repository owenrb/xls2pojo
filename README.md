# xls2pojo

A simple Java library to convert Microsoft Excel (XLS and XLSX) rows into a list of Plain Old Java Objects (POJOs).

## Features

- Convert Excel rows to Java objects.
- Supports both XLS and XLSX formats.
- Maps Excel columns to POJO fields based on header names.
- Handles various data types: `String`, `Long`, `Integer`, `Double`, `Float`, `Boolean`, and `Date`.
- Easy to use with a simple and intuitive API.

## Requirements

- Java 11 or higher
- Maven 3.2 or higher

## Installation

To use xls2pojo in your Maven project, add the following dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>io.github.owenrb</groupId>
  <artifactId>xls2pojo</artifactId>
  <version>1.1.0</version>
</dependency>
```

## Usage

### 1. Create a POJO Class

Create a Java class that represents the data in your Excel sheet. The field names in the class must match the header names in the Excel sheet (case-insensitive, ignores whitespace).

**Example:**

```java
import java.util.Date;
import lombok.Data;

@Data
public class User {
    private String name;
    private String email;
    private Date dateOfBirth;
    private boolean isActive;
}
```

### 2. Prepare the Excel File

Create an Excel file with the data you want to convert. The first row of the sheet should contain the header names that match the field names in your POJO class.

**Example (`users.xlsx`):**

| Name       | Email                  | Date of Birth | Is Active |
| ---------- | ---------------------- | ------------- | --------- |
| John Doe   | john.doe@example.com   | 1990-01-15    | true      |
| Jane Smith | jane.smith@example.com | 1985-05-20    | false     |

### 3. Convert the Excel Data

Use the `Xls2Pojo.convert()` method to convert the Excel data into a list of POJOs.

**Example:**

```java
import io.owenrbee.xls.xls2pojo.Xls2Pojo;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "path/to/your/users.xlsx";
        List<User> users = Xls2Pojo.convert(filePath, User.class);

        for (User user : users) {
            System.out.println(user);
        }
    }
}
```

## API

The main entry point for the library is the `io.owenrbee.xls.xls2pojo.Xls2Pojo` class.

- `Xls2Pojo.convert(String inputFilePath, Class<T> outputClass)`

  Converts the data from the first sheet of the specified Excel file into a list of objects of the given class.

- `Xls2Pojo.convert(String inputFilePath, Class<T> outputClass, int... sheetIndices)`

  Converts the data from the specified sheets (by index) of the Excel file into a list of objects.

- `Xls2Pojo.convert(String inputFilePath, Class<T> outputClass, String... sheetNames)`

  Converts the data from the specified sheets (by name) of the Excel file into a list of objects.

## How to Build

To build the project from the source, clone the repository and run the following command:

```bash
mvn clean install
```

## How to Run Tests

To run the tests, execute the following command:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
