package com.solid.subscribe.targeting.ex;


public class BadTagSettingException extends TargetingException {
    public BadTagSettingException(int entity, String entityName, String tagname) {
        super("Bad setting of entity:" + entity + "(" + entityName + ") tag:" + tagname);
    }
}
