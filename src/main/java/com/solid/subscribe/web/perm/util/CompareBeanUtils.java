
package com.solid.subscribe.web.perm.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 *  处理日志分析、记录对应的实体修改的结果前后
 * @param <T>
 *
 */
public class CompareBeanUtils<T> {
    private Map<Class, PropertyEditor> propEditorMap = new HashMap<Class, PropertyEditor>();
    private boolean                    isNew         = true;
    private T                          oldObject;
    private T                          newObject;
    private Class<T>                   clazz;
    private StringBuffer               content;

    /**
     * @description
     *
     *
     * @param clazz
     */
    protected CompareBeanUtils(Class<T> clazz) {
        super();
        content    = new StringBuffer();
        this.clazz = clazz;
        register(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));    // 注册日期类型
    }

    /**
     *
     * @param clazz
     * @param newObject
     */
    public CompareBeanUtils(Class<T> clazz, T newObject) {
        this(clazz);
        this.newObject = newObject;
    }

    /**
     * 构造
     *
     *
     * @param clazz
     * @param oldObject 老对象
     * @param newObject 新的对象
     */
    public CompareBeanUtils(Class<T> clazz, T oldObject, T newObject) {
        this(clazz);
        this.oldObject = oldObject;
        this.newObject = newObject;
        this.isNew     = false;
    }

    /**
     * @description
     *
     * @author 
     *
     * @param prop          需要记录日志的属性
     * @param propLabel     需要记录日志的属性的中文名，一般对应表单当中的label属性
     */
    public void compare(String prop, String propLabel) {
        try {
            Field  field    = ReflectionUtils.findField(clazz, prop);
            Method m        = null;
            Object newValue = null;

            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                newValue = field.get((newObject == null)
                                     ? oldObject
                                     : newObject);
            } else {
                char c = prop.charAt(0);

                m        = clazz.getDeclaredMethod("get" + StringUtils.upperCase(String.valueOf(c))
                                                   + prop.substring(1));
                newValue = m.invoke((newObject == null)
                                    ? oldObject
                                    : newObject);
            }

            if (isNew) {
                if (!isNullOrEmpty(newValue)) {
                    comparedIsAdd(propLabel, newValue);
                }
            } else {
                if (null == oldObject) {
                    return;
                }

                if (null == newObject) {
                    comparedIsdel(propLabel);

                    return;
                }

                Object oldValue = null;

                if (field != null) {
                    oldValue = field.get(oldObject);
                } else {
                    oldValue = m.invoke(oldObject);
                }

                if (notEquals(oldValue, newValue)) {
                    comparedIsEdit(propLabel, oldValue, newValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 记录新值
     */

    /**
     * @description
     *
     * @author 
     *
     * @param propLabel
     * @param newValue
     */
    private void comparedIsAdd(String propLabel, Object newValue) {
        content.append("创建了 [");
        content.append(propLabel);
        content.append("],新值为\"");
        content.append(format(newValue));
        content.append("\";");
    }

    /*
     * 记录变更数据
     */

    /**
     * @description
     *
     * @author 
     *
     * @param propLabel
     * @param oldValue
     * @param newValue
     */
    private void comparedIsEdit(String propLabel, Object oldValue, Object newValue) {
        content.append("修改了[");
        content.append(propLabel);
        content.append("],");
        content.append("旧值为\"");
        content.append(format(oldValue));
        content.append("\",新值为\"");
        content.append(format(newValue));
        content.append("\";");
    }

    /**
     * @description
     *
     * @author 
     *
     * @param propLabel
     */
    private void comparedIsdel(String propLabel) {
        content.append("删除了 [");
        content.append(propLabel);
        content.append("]");
        content.append(";");
    }

    /**
     * 格式化数据类型
     *
     * @param obj
     * @return
     */
    private Object format(Object obj) {
        if (isNullOrEmpty(obj)) {
            return "";
        }

        Class clz = obj.getClass();

        if (propEditorMap.containsKey(clz)) {
            PropertyEditor pe = propEditorMap.get(clz);

            pe.setValue(obj);

            return pe.getAsText();
        } else {
            return obj;
        }
    }

    /**
     * @description
     *
     * @author 
     *
     * @param newObj
     * @param <T>
     *
     * @return
     */
    public static <T> CompareBeanUtils<T> newInstance(T newObj) {
        if (null == newObj) {
            return null;
        }

        Class clazz = newObj.getClass();

        return new CompareBeanUtils<T>(clazz, newObj);
    }

    /**
     * @description
     *
     * @author 
     *
     * @param oldObj
     * @param newObj
     * @param <T>
     *
     * @return
     */
    public static <T> CompareBeanUtils<T> newInstance(T oldObj, T newObj) {
        if ((null == oldObj) && (null == newObj)) {
            return null;
        }

        Class clazz;

        clazz = ((newObj == null)
                 ? oldObj.getClass()
                 : newObj.getClass());

        return new CompareBeanUtils<T>(clazz, oldObj, newObj);
    }

    /**
     * @description
     *
     * @author 
     *
     * @param oldValue
     * @param newValue
     *
     * @return
     */
    private boolean notEquals(Object oldValue, Object newValue) {
        String tmpOld, tmpNew;

        if (isNullOrEmpty(oldValue)) {
            tmpOld = "";
        } else {
            tmpOld = oldValue.toString();
        }

        if (isNullOrEmpty(newValue)) {
            tmpNew = "";
        } else {
            tmpNew = newValue.toString();
        }

        return !StringUtils.equals(tmpNew, tmpOld);
    }

    /**
     * @description
     *
     * @author 
     *
     * @param clazz
     * @param pe
     * @param <CC>
     */
    public <CC> void register(Class<CC> clazz, PropertyEditor pe) {
        propEditorMap.put(clazz, pe);
    }


    /**
     * @description 数据结果
     *
     * @author 
     *
     * @return
     */
    public String toResult() {
        return content.toString();
    }

    /**
     * @description
     *
     * @author 
     *
     * @param val
     *
     * @return
     */
    private boolean isNullOrEmpty(Object val) {
        if (val instanceof String) {
            return (StringUtils.isEmpty(String.class.cast(val)));
        } else {
            return val == null;
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
