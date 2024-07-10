package org.sonatype.maven.polyglot.java.namedval;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.BeanMap;

public class NamedValueProcessor {

    public static Map<String, String> namedToMap(NamedValue[] keyValuePairs) {
        Map<String, String> map = new HashMap<>();
        asList(keyValuePairs).stream().filter(kvp -> kvp != null).forEach(kvp -> map.put(kvp.name(), kvp.value()));

        return map;
    }

    public static <E> E namedToObject(E entity, NamedValue[] keyValuePairs) {
        BeanMap beanMap = new BeanMap(entity);
        beanMap.putAll(namedToMap(keyValuePairs));
        return entity;
    }

    public static <E> E mapToObject(E entity, Map<String, String> map) {
        BeanMap beanMap = new BeanMap(entity);
        beanMap.putAll(map);
        return entity;
    }
}
