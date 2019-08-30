package com.solid.subscribe.targeting.ex;

public class UnknownTagException extends TargetingException {
    public UnknownTagException(String tagname) {
        super("Unknown tag: " + tagname);
    }
}
