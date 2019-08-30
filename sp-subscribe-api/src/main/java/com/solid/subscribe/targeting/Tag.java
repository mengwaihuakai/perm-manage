package com.solid.subscribe.targeting;

public class Tag {
    private String name;
    private boolean withMarkup;
    private boolean layered;

    public Tag(String name, boolean withMarkup, boolean layered) {
        this.name = name;
        this.withMarkup = withMarkup;
        this.layered = layered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWithMarkup() {
        return withMarkup;
    }

    public void setWithMarkup(boolean withMarkup) {
        this.withMarkup = withMarkup;
    }

    public boolean isLayered() {
        return layered;
    }

    public void setLayered(boolean layered) {
        this.layered = layered;
    }
}
