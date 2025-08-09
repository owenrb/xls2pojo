package io.owenrbee.xls.xls2pojo;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Xls2PojoTest {

    @Test
    public void load() {

        String filePath = "./sample/sample-01.xlsx";

        List<Sample> result1 = Xls2Pojo.convert(filePath, Sample.class);
        List<Sample> result2 = Xls2Pojo.convert(filePath, Sample.class, 0);
        List<Sample> result3 = Xls2Pojo.convert(filePath, Sample.class, "Sheet1");

        String str1 = result1.toString();
        String str2 = result2.toString();
        String str3 = result3.toString();

        Assert.assertEquals(str1, str2);
        Assert.assertEquals(str1, str3);

        log.info(str3);

    }

    @Data
    public static class Sample {
        private int sn;
        private String firstName;
        private String lastName;
        private String address;
        private Date birthday;
        private float score;
        private boolean active;
    }

}
