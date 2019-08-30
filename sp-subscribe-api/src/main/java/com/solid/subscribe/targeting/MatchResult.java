package com.solid.subscribe.targeting;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MatchResult {
    private int[] markup;
    private BitSet filter;

    private void init(int size, boolean value, Integer markupValue) {
        filter = new BitSet(size);
        if (value) {
            filter.set(0, size);
        }
        if (markupValue != null) {
            markup = new int[size];
            if (markupValue != 0) {
                Arrays.fill(markup, markupValue);
            }
        } else {
            markup = null; // no markup
        }
    }

    // init the match result with markup value
    public MatchResult(int size, boolean initValue, Integer initMarkupValue) {
        init(size, initValue, initMarkupValue);
    }

    // init the match result without markup value
    public MatchResult(int size, boolean initValue) {
        init(size, initValue, null);
    }

    // set filter and markup value at some position
    public void set(int i, int markupValue) {
        if (filter != null && i >= 0 && i < filter.size()) {
            filter.set(i, markupValue != 0);
        }
        if (markup != null) {
            markup[i] = markupValue;
        }
    }

    // get all set bits with their markup value
    // return {offset -> markupValue}
    public Map<Integer, Integer> getAll() {
        Map<Integer, Integer> r = new HashMap<>();
        for (int i = filter.nextSetBit(0); i >= 0; i = filter.nextSetBit(i+1)) {
            int markupValue = (markup != null ? markup[i] : Constants.BASE_MARKUP_VALUE);
            r.put(i, markupValue);
            // operate on index i here
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
        }
        return r;
    }
    public boolean isEmpty() {
        return this.filter.isEmpty();
    }
    public int size() {
        return this.filter.cardinality();
    }
    // merge other match result, calc the new markup value
    private void merge_apply(final List<MatchResult> v, final BitSet f) {
        if (markup != null) {
            int pos = f.nextSetBit(0);
            while (pos >= 0) {
                int markup_val = Constants.BASE_MARKUP_VALUE;
                for (MatchResult r : v) {
                    if (r.markup != null && r.markup[pos] != 0) {
                        markup_val = Math.max(markup_val, r.markup[pos]);
                    }
                }
                markup[pos] = markup_val;
                pos = f.nextSetBit(pos + 1);
            }
        }
    }

    // and/or the match result
    public void merge_and(final List<MatchResult> v) {
        for (MatchResult r : v) {
            filter.and(r.filter);
        }
        merge_apply(v, filter);
    }

    public void merge_or(final List<MatchResult> v) {
        for (MatchResult r : v) {
            filter.or(r.filter);
        }
        merge_apply(v, filter);
    }
}