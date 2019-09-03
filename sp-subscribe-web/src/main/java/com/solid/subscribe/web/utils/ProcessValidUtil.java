package com.solid.subscribe.web.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @author fanyongju
 * @Title: ProcessVaildUtil
 * @date 2019/4/19 17:27
 */
public class ProcessValidUtil {
    public static String errorMsg(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getDefaultMessage()).append(";");
        }
        return errorMsg.toString();
    }
}
