package com.solid.subscribe.targeting.ex;

public class DuplicateTagException extends TargetingException {
    public DuplicateTagException(String tagname) {
        super("Duplicate tag: " + tagname);
    }
}
