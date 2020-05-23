package com.faiop.core.util;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@Component
public class PathUtil {
    public static String getProjectPath(){
        ApplicationHome home = new ApplicationHome(PathUtil.class);
        File jarFile = home.getSource();
        return jarFile.getParentFile().toString();
    }

    public static void main(String[] args) {
        System.out.println(PathUtil.getProjectPath());
    }
}
