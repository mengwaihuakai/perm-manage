package com.solid.subscribe.targeting;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// tageting setting for single tag
// value => markup value
// example:
//      a:1,b:0
//      *:1,a:0,a-b:1
public class Setting {
    private Map<String, Integer> markup_setting_ = new HashMap<>();
    private int default_markup_ = 0;
    private boolean enable_alias_ = false;
    private boolean layered = false;
    public static boolean check(String settingStr) {
        return settingStr != null && (!settingStr.isEmpty());
    }
    // set targeting rule
    // value1:markup1[,value2:markup2[,...]
    public void set(final String setting_str, boolean layered){
        this.layered = layered;
        String[] items = setting_str.split(",");
        for (String setting : items) {
            // setting rules:
            // xxx:yyy => xxx:int(yyy)
            // xxx:    => xxx:${base_markup}
            // xxx     => xxx:${base_markup}
            // *:zzz   => default:int(zzz)
            int pos = setting.lastIndexOf(':');
            int markup = Constants.BASE_MARKUP_VALUE;
            String value = null;
            if (pos >= 0) {
                if (pos != setting.length() - 1) {
                    markup = Integer.parseInt(setting.substring(pos + 1));
                }
                value = setting.substring(0, pos);
            } else {
                value = setting;
            }
            if (!value.equals("*")) {
                if (enable_alias_) {
                    // a|b:zz => a:zz + b:zz
                    String[] aliases = value.split("\\|");
                    for (String alias : aliases) {
                        if (!alias.isEmpty()) {
                            markup_setting_.put(alias, markup);
                        }
                    }
                } else {
                    markup_setting_.put(value, markup);
                }
            } else { // *:xxx for default path
                default_markup_ = markup;
            }
        }
    }

    // find out markup value by targeting rules
    public int get(final String value) {
        if (value.equals(Constants.VALUE_UNKNOWN)) {
            return default_markup_;
        }
        if (value.equals(Constants.VALUE_IGNORE)) {
            return Constants.BASE_MARKUP_VALUE;
        }
        if (layered) {
            // match rule:
            // aaa-bbb-ccc => aaa-bbb-ccc
            //                aaa-bbb
            //                aaa
            //                *
            String v = value;
            while (!v.isEmpty()) {
                Integer mv = markup_setting_.get(v);
                if (mv != null) {
                    return mv;
                } else {
                    int pos = v.lastIndexOf('-');
                    if (pos >= 0) {
                        v = v.substring(0, pos);
                        continue;
                    } else {
                        return default_markup_;
                    }
                }
            }
            return default_markup_;
        } else {
            Integer mv = markup_setting_.get(value);
            if (mv != null) {
                return mv;
            } else {
                return default_markup_;
            }
        }
    }
    public int get_default() {
        return default_markup_;
    }
    public Set<String> collect() {
        return markup_setting_.keySet();
    }
}
