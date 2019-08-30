package com.solid.subscribe.targeting;


import com.solid.subscribe.targeting.ex.BadTagSettingException;
import com.solid.subscribe.targeting.ex.DuplicateTagException;
import com.solid.subscribe.targeting.ex.UnknownTagException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Matcher {

    private Map<String, Tag> tagMap = new HashMap<>();
    private List<Tag> tags = new ArrayList<>();
    private List<Entity> data = new ArrayList<>();

    /// register a new tag
    void addTag(final String tagname, boolean withMarkup, boolean layered) throws DuplicateTagException {
        if (tags.stream().filter(t -> t.getName().equals(tagname)).count() <= 0) {
            Tag tag = new Tag(tagname, withMarkup, layered);
            tags.add(tag);
            tagMap.put(tagname, tag);
        } else {
            throw new DuplicateTagException(tagname);
        }
    }
    /// add new entity data
    void addData(int entityId, String entityName, Map<String, String> settings) throws BadTagSettingException {
        Map<String, Setting> sets = new HashMap<>();
        for (Tag tag : tags) {
            String settingStr = settings.get(tag.getName());
            if (Setting.check(settingStr)) {
                Setting s = new Setting();
                s.set(settingStr, tag.isLayered());
                sets.put(tag.getName(), s);
            } else {
                throw new BadTagSettingException(entityId, entityName, tag.getName());
            }
        }
        data.add(new Entity(entityId, entityName, sets));
    }

    MatchResult newMatchResult(boolean initialVal) {
        return new MatchResult(data.size(), initialVal);
    }

    MatchResult newMatchResult(boolean initialVal, boolean withMarkup) {
        return withMarkup ? new MatchResult(data.size(), initialVal, Constants.BASE_MARKUP_VALUE) :
                new MatchResult(data.size(), initialVal);
    }

    MatchResult newMatchResultWithMarkup(boolean initialVal) {
        return new MatchResult(data.size(), initialVal, Constants.BASE_MARKUP_VALUE);
    }
    // collect all values of a tag
    Set<String> collectTagValues(String tagname) {
        Set<String> vals = new HashSet<>();
        for (Entity ent : data) {
            Setting tagset = ent.getSettings().get(tagname);
            if (tagset != null) {
                vals.addAll(tagset.collect());
            }
        }
        return vals;
    }

    // single tag match
    final MatchResult match(String tagname, String tagval, boolean withMarkup)  throws BadTagSettingException {
        MatchResult r = withMarkup ? newMatchResultWithMarkup(false) : newMatchResult(false);
        // iterate each data entry
        for (int i = 0; i < data.size(); i++) {
            final Entity ent = data.get(i);
            final Setting tagset = ent.getSettings().get(tagname);
            if (tagset != null) {
                int markup = tagset.get(tagval);
                r.set(i, markup);
            } else {
                throw new BadTagSettingException(ent.getId(), ent.getName(), tagname);
            }
        }
        return r;
    }
    // convert match result to entity entries
    // return {offset -> markupValue}
    Map<Integer, Integer> resolve(MatchResult r, int baseMarkupValue) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, Integer> ent : r.getAll().entrySet()) {
            result.put(data.get(ent.getKey()).getId(), ent.getValue() != Constants.BASE_MARKUP_VALUE ? ent.getValue() : baseMarkupValue);
        }
        return result;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Tag getTag(String tagname) throws UnknownTagException {
        if (tagMap.containsKey(tagname)) {
            return tagMap.get(tagname);
        }
        throw new UnknownTagException(tagname);
    }

    // inner structure
    public static class Entity {
        private int id;
        private String name;
        private Map<String, Setting> settings;

        public Entity(int id, String name, Map<String, Setting> settings) {
            this.id = id;
            this.name = name;
            this.settings = settings;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Setting> getSettings() {
            return settings;
        }

        public void setSettings(Map<String, Setting> settings) {
            this.settings = settings;
        }
    }
}
