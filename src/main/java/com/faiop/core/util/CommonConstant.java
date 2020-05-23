package com.faiop.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author RM
 */
@Component
public class CommonConstant {
    public static String PATH_UPLOAD_FOLDER;
    public static String PATH_UPLOAD_AVATAR;
    public static String PATH_UPLOAD_CONTRACT;
    public static String PATH_UPLOAD_DOC;

    @Value("${file.uploadFolder}")
    public void setPATH_UPLOAD_FOLDER (String pathUploadFolder) {
        PATH_UPLOAD_FOLDER = pathUploadFolder;
    }
    @Value("${file.uploadAvatar}")
    public void setPathUploadAvatar(String pathUploadAvatar) {
        PATH_UPLOAD_AVATAR = pathUploadAvatar;
    }
    @Value("${file.uploadContract}")
    public void setPathUploadContract(String pathUploadContract) {
        PATH_UPLOAD_CONTRACT = pathUploadContract;
    }
    @Value("${file.uploadDoc}")
    public void setPathUploadDoc(String pathUploadDoc) {
        PATH_UPLOAD_DOC = pathUploadDoc;
    }
}
