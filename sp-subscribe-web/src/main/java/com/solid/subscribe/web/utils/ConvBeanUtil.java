package com.solid.subscribe.web.utils;

import java.lang.reflect.Field;

public class ConvBeanUtil {
    public static void conv(Object sourceBean, Object targetBean) {
        Field[] sourceFields = sourceBean.getClass().getDeclaredFields();
        Field[] targetFields = targetBean.getClass().getDeclaredFields();
        for (int i = 0; i < targetFields.length; i++) {
            Field targetField = targetFields[i];
            targetField.setAccessible(true);
            for (Field sourceField : sourceFields) {
                if (sourceField.getName().equals(targetField.getName())
                        && sourceField.getType().getSimpleName().equals(targetField.getType().getSimpleName())) {//存在相同名称的属性,且类型相同
                    sourceField.setAccessible(true);
                    try {
                        if (!(sourceField.get(sourceBean) == null)) {//如果原bean的该字段不为空
                            targetField.set(targetBean, sourceField.get(sourceBean));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
