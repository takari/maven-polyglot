package org.sonatype.maven.polyglot.kotlin.util;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class Properties extends java.util.Properties {

    @Override
    @NotNull
    public Set<Object> keySet() {
        return super.keySet().stream()
                .sorted(Comparator.comparing(Object::toString))
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    @Override
    @NotNull
    public Set<Map.Entry<Object, Object>> entrySet() {
        return super.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().toString()))
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return new Enumeration<Object>() {

            private Iterator<Object> keys = keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return keys.hasNext();
            }

            @Override
            public Object nextElement() {
                return keys.next();
            }
        };
    }
}
