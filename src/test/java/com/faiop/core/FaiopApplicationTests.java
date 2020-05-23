package com.faiop.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class FaiopApplicationTests {

    @Test
    void contextLoads() {
//        try {
//            Path path = Paths.get(ResourceUtils.getURL("classpath:static").getPath().replace("%20", " "));
//            System.out.println(path);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

}
