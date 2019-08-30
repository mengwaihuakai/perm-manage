package com.solid.subscribe.targeting;

import com.solid.subscribe.targeting.ex.UnknownTagException;
import java.util.HashMap;
import java.util.Map;

public class Index {
    Map<String, TagIndex> tags = new HashMap<>(); // tag name to tag index

    public void createTag(final String tagname, final MatchResult defval, final MatchResult ignoreval, boolean layered) {
        tags.put(tagname, new TagIndex(defval, ignoreval, layered));
    }
    // put index value
    public void put(String tagname, String tagval, final MatchResult r) throws UnknownTagException {
        TagIndex ind = tags.get(tagname);
        if (ind != null) {
            ind.put(tagval, r);
        } else {
            throw new UnknownTagException(tagname);
        }
    }
    // find index from index data
    public final MatchResult get(final String tagname, final String tagval) throws UnknownTagException {
        TagIndex ind = tags.get(tagname);
        if (ind != null) {
            return ind.get(tagval);
        } else {
            throw new UnknownTagException(tagname);
        }
    }

    /// inner data structure
    // index of single tag
    private static class TagIndex {
        private final MatchResult defval; // * rule
        private final MatchResult ignoreval; // IGNORE rule
        private final Map<String, MatchResult> data; // tagval => index
        private final boolean layered;
        TagIndex(final MatchResult defval, final MatchResult ignoreval, boolean layered) {
            this.defval = defval;
            this.ignoreval = ignoreval;
            this.data = new HashMap<>();
            this.layered = layered;
        }
        public void put(String tagval, final MatchResult r) {
            data.put(tagval, r);
        }
        public final MatchResult get(String tagval) {
            if (tagval.equals(Constants.VALUE_UNKNOWN)) {
                return defval;
            }
            if (tagval.equals(Constants.VALUE_IGNORE)) {
                return ignoreval;
            }

            // match rule:
            // aaa-bbb-ccc => aaa-bbb-ccc
            //                aaa-bbb
            //                aaa
            //                *
            if (layered) {
                String v = tagval;
                while (!v.isEmpty()) {
                    MatchResult r = data.get(v);
                    if (r != null) {
                        return r;
                    } else {
                        int pos = v.lastIndexOf('-');
                        if (pos >= 0) {
                            v = v.substring(0, pos);
                            continue;
                        } else {
                            return defval;
                        }
                    }
                }
                return defval;
            } else {
                MatchResult r = data.get(tagval);
                if (r != null) {
                    return r;
                } else {
                    return defval;
                }
            }
        }
    }
}
