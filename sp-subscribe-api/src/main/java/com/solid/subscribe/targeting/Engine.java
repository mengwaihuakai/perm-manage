package com.solid.subscribe.targeting;

import com.solid.subscribe.targeting.ex.TargetingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Engine {
    private static Logger logger = LoggerFactory.getLogger(Engine.class);
    private final Matcher matcher;
    private final Index index;

    Engine(final Matcher matcher, Index index) {
        this.matcher = matcher;
        this.index = index;
    }

    public List<String> getTagNames() {
        return matcher.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    }

    public Map<Integer, Integer> target(Map<String, String> query, boolean ignoreMissedTag, int baseMarkupValue, List<String> debugTrace) {
        // step 1: parse the query request into experssion
        Map<String, MultiValue> exp = new HashMap<>(); // tag name -> multiple tag value
        boolean withMarkup = false;
        for (Tag tag : matcher.getTags()) {
            String multiValueStr = query.get(tag.getName());
            if (multiValueStr != null) {
                if (multiValueStr.contains("&")) {
                    exp.put(tag.getName(), MultiValue.newAndValues(multiValueStr.split("\\&")));
                } else {
                    exp.put(tag.getName(), MultiValue.newOrValues(multiValueStr.split("\\|")));
                }
            } else {
                // missed tag
                String[] vals = {ignoreMissedTag ? Constants.VALUE_IGNORE : Constants.VALUE_UNKNOWN};
                exp.put(tag.getName(), MultiValue.newAndValues(vals));
            }
            if (tag.isWithMarkup()) {
                withMarkup = true;
            }
        }
        try {
            // step 2: eval the expression
            //   OR the multi-value results under single tag then AND the tags
            List<MatchResult> andLi = new ArrayList<>();
            for (Map.Entry<String, MultiValue> ent : exp.entrySet()) {
                String tagname = ent.getKey();
                Tag tag = matcher.getTag(tagname);
                List<MatchResult> mvLi = new ArrayList<>();
                for (String tagval : ent.getValue().getValues()) {
                    final MatchResult r = index.get(tagname, tagval);
                    mvLi.add(r);
                }
                MatchResult tagResult = null;
                if (ent.getValue().isAndType()) {
                    tagResult = matcher.newMatchResult(true, tag.isWithMarkup());
                    tagResult.merge_and(mvLi);
                } else {
                    tagResult = matcher.newMatchResult(false, tag.isWithMarkup());
                    tagResult.merge_or(mvLi);
                }
                andLi.add(tagResult);

                if (debugTrace != null) {
                    if (tagResult.isEmpty()) {
                        String debugMsg = String.format("Tag(%s -> %s) match nothing", tagname, String.join(ent.getValue().isAndType() ? "&" : "|", ent.getValue().getValues()));
                        debugTrace.add(debugMsg);
                        logger.info("DEBUG: {}", debugMsg);
                    }
                }
            }
            MatchResult result = matcher.newMatchResult(true, withMarkup);
            result.merge_and(andLi);

            // step 3: convert the MatchResult to entity id and markup value
            return matcher.resolve(result, baseMarkupValue);
        } catch (TargetingException e) {
            return null;
        }
    }
    private static class MultiValue {
        private static final int OR = 0;
        private static final int AND = 1;
        private final int type;
        private final String[] values;

        private MultiValue(int type, String[] values) {
            this.type = type;
            this.values = values;
        }
        public static MultiValue newOrValues(final String[] values) {
            return new MultiValue(OR, values);
        }
        public static MultiValue newAndValues(final String[] values) {
            return new MultiValue(AND, values);
        }

        public boolean isAndType() {
            return type == AND;
        }
        public final String[] getValues() {
            return values;
        }

        @Override
        public String toString() {
            return  String.join(this.type == OR ? "|" : "&", this.values);
        }
    }
    /*
    public static class QueryBuilder {
        private Map<String, MultiValue> query;

        public QueryBuilder(final Engine engine) {
            this.query = new HashMap<>(engine.getTagNames().size());
        }
        public QueryBuilder putSingle(String tagname, String tagvalue) {
            query.put(tagname, MultiValue.newOrValues(new String[]{tagvalue}));
            return this;
        }
        public QueryBuilder putMultiOR(String tagname, String[] tagvalue) {
            query.put(tagname, MultiValue.newOrValues(tagvalue));
            return this;
        }
        public QueryBuilder putMultiOR(String tagname, List<String> tagvalue) {
            query.put(tagname, MultiValue.newOrValues((String[])tagvalue.toArray()));
            return this;
        }
        public QueryBuilder putMultiAND(String tagname, String[] tagvalue) {
            query.put(tagname, MultiValue.newAndValues(tagvalue));
            return this;
        }
        public QueryBuilder putMultiAND(String tagname, List<String> tagvalue) {
            query.put(tagname, MultiValue.newAndValues((String[])tagvalue.toArray()));
            return this;
        }
        public QueryBuilder put(String tagname, String tagvalue) {
            if (tagvalue.contains("&")) {
                putMultiAND(tagname, tagvalue.split("\\&"));
            } else {
                putMultiOR(tagname, tagvalue.split("\\|"));
            }
            return this;
        }
        public Map<String, MultiValue> build() {
            return query;
        }
    }
    */
}
