package org.sonatype.maven.polyglot.java.dsl;

import org.sonatype.maven.polyglot.java.namedval.NamedValue;

public interface PropertyFactory {

    public default Property property(String name, String value) {
        Property property = new Property();
        property.name = name;
        property.value = value;
        return property;
    }

    /**
     * Factory method to define property.
     * <br>
     * Key-value pair is a construct <br>
     * 	key -> "value" <br>
     * Where 'key' is the name of the property to set and "value" is it's string value. <br>
     *
     * @param keyValuePair - combination of key -> "value" pairs. Example: <code>property(name1 -> "property_1")</code>
     * @return	defined property
     */
    public default Property property(NamedValue keyValuePair) {
        Property property = new Property();
        property.name = keyValuePair.name();
        property.value = keyValuePair.value();
        return property;
    }

    public class Property {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
