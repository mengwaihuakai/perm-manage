package com.solid.subscribe.targeting;

import com.solid.subscribe.targeting.ex.TargetingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Set;

public class EngineBuilder {
    private static Logger logger = LoggerFactory.getLogger(EngineBuilder.class);
    private Matcher matcher;
    private EngineBuilder() {
        this.matcher = new Matcher();
    }
    public static EngineBuilder newBuilder() {
        return new EngineBuilder();
    }

    public EngineBuilder addTag(final String tagname, boolean withMarkup, boolean layered) throws TargetingException {
        matcher.addTag(tagname, withMarkup, layered);
        return this;
    }
    public EngineBuilder addData(int entityId, String entityName, Map<String, String> settings) throws TargetingException {
        matcher.addData(entityId, entityName, settings);
        return this;
    }
    public final Engine build() throws TargetingException {
        final Index index = buildIndex(matcher);
        return new Engine(matcher, index);
    }

    // build index from matcher data
    private static final Index buildIndex(final Matcher matcher) throws TargetingException {
        Index index = new Index();
           for (Tag tag : matcher.getTags()) {
            MatchResult defval = matcher.match(tag.getName(), Constants.VALUE_UNKNOWN, tag.isWithMarkup());
            MatchResult ignoreval = matcher.match(tag.getName(), Constants.VALUE_IGNORE, tag.isWithMarkup());
            if (defval == null || ignoreval == null) {
                return null;
            }
            index.createTag(tag.getName(), defval, ignoreval, tag.isLayered());
            Set<String> vals = matcher.collectTagValues(tag.getName());
            for (String tagval : vals) {
                MatchResult r = matcher.match(tag.getName(), tagval, tag.isWithMarkup());
                if (r == null) {
                    return null;
                }
                logger.debug("BuildIndex: ({} -> {}) result {} entities", tag.getName(), tagval, r.size());
                index.put(tag.getName(), tagval, r);
            }
        }
        return index;
    }
}
