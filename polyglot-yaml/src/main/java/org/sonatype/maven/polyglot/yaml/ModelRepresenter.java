/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.beans.IntrospectionException;
import java.util.*;

import static java.lang.String.format;

/**
 * YAML model representer.
 *
 * @author jvanzyl
 * @author bentmann
 * @since 0.7
 */
class ModelRepresenter extends Representer {
  public ModelRepresenter() {
    this.representers.put(Xpp3Dom.class, new RepresentXpp3Dom());
    Represent stringRepresenter = this.representers.get(String.class);
    this.representers.put(Boolean.class, stringRepresenter);
    this.multiRepresenters.put(Number.class, stringRepresenter);
    this.multiRepresenters.put(Date.class, stringRepresenter);
    this.multiRepresenters.put(Enum.class, stringRepresenter);
    this.multiRepresenters.put(Calendar.class, stringRepresenter);
  }

  protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                Object propertyValue, Tag customTag) {
    if (propertyValue == null) return null;
    if (propertyValue instanceof Map) {
      Map map = (Map) propertyValue;
      if (map.isEmpty()) return null;
    }
    if (propertyValue instanceof List) {
      List map = (List) propertyValue;
      if (map.isEmpty()) return null;
    }
    if (javaBean instanceof Dependency) {
      //skip optional if it is false
      if (skipBoolean(property, "optional", propertyValue, false)) return null;
      //skip type if it is jar
      if (skipString(property, "type", propertyValue, "jar")) return null;
    }
    if (javaBean instanceof Plugin) {
      //skip extensions if it is false
      if (skipBoolean(property, "extensions", propertyValue, false)) return null;
      //skip inherited if it is true
      if (skipBoolean(property, "inherited", propertyValue, true)) return null;
    }
    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
  }

  private boolean skipString(Property property, String name, Object propertyValue, String value) {
    if (name.equals(property.getName())) {
      String v = (String) propertyValue;
      return (value.equals(v));
    }
    return false;
  }

  private boolean skipBoolean(Property property, String name, Object propertyValue, boolean value) {
    if (name.equals(property.getName())) {
      Boolean v = (Boolean) propertyValue;
      return (v.equals(value));
    }
    return false;
  }


  private class RepresentXpp3Dom implements Represent {

    public Node representData(Object data) {
      return representMapping(Tag.MAP, toMap((Xpp3Dom) data), null);
    }

    private Map<String, Object> toMap(Xpp3Dom node) {
      Map<String, Object> map = new LinkedHashMap<>();

      int n = node.getChildCount();
      for (int i = 0; i < n; i++) {
        Xpp3Dom child = node.getChild(i);
        String childName = child.getName();

        String singularName = null;
        int childNameLength = childName.length();
        if ("reportPlugins".equals(childName)) {
          singularName = "plugin";
        } else if (childNameLength > 3 && childName.endsWith("ies")) {
          singularName = childName.substring(0, childNameLength - 3);
        } else if (childNameLength > 1 && childName.endsWith("s")) {
          singularName = childName.substring(0, childNameLength - 1);
        }

        Object childValue = child.getValue();
        if (childValue == null) {
          boolean isList = singularName != null;
          if (isList) { // check for eventual list construction
            for (int j = 0, grandChildCount = child.getChildCount(); j < grandChildCount; j++) {
              String grandChildName = child.getChild(j).getName();
              isList &= grandChildName.equals(singularName);
            }
          }
          if (isList) {
            childValue = toList(child, singularName);
          } else {
            childValue = toMap(child);
          }
        }
        map.put(childName, childValue);
      }

      return map;
    }

    private List<Object> toList(Xpp3Dom node, String childName) {
      List<Object> list = new ArrayList<>();

      int n = node.getChildCount();
      for (int i = 0; i < n; i++) {
        Xpp3Dom child = node.getChild(i);

        if (!childName.equals(child.getName())) {
          throw new YAMLException(format("child name: '%s' does not match expected name: '%s' at node %s",
              child.getName(), childName, node));
        }

        Object childValue = child.getValue();
        if (childValue == null) {
          childValue = toMap(child);
        }
        list.add(childValue);
      }

      return list;
    }
  }

  /*
   * Change the default order. Important data goes first.
   */
  @Override
  protected Set<Property> getProperties(Class<? extends Object> type)
          throws IntrospectionException {
    if (type.isAssignableFrom(Model.class)) {
      Set<Property> standard = super.getProperties(type);
      List<String> order = new ArrayList<String>(Arrays.asList(
              "modelVersion",
              "modelEncoding",
              "parent",
              "groupId",
              "artifactId",
              "version",
              "packaging",
              "properties",
              "name",
              "description",
              "inceptionYear",
              "url",
              "issueManagement",
              "ciManagement",
              "mailingLists",
              "scm",
              "licenses",
              "developers",
              "contributers",
              "prerequisites",
              "dependencies",
              "distributionManagement",
              "build",
              "reporting"));
      Set<Property> sorted = new TreeSet<Property>(new ModelPropertyComparator(order));
      sorted.addAll(standard);
      return sorted;
    } else if (type.isAssignableFrom(Developer.class)) {
      Set<Property> standard = super.getProperties(type);
      List<String> order = new ArrayList<String>(Arrays.asList("name", "id", "email"));
      Set<Property> sorted = new TreeSet<Property>(new ModelPropertyComparator(order));
      sorted.addAll(standard);
      return sorted;
    }  else if (type.isAssignableFrom(Plugin.class)) {
      Set<Property> standard = super.getProperties(type);
      List<String> order = new ArrayList<String>(Arrays.asList("groupId", "artifactId", "version", "inherited", "extensions", "configuration"));
      Set<Property> sorted = new TreeSet<Property>(new ModelPropertyComparator(order));
      sorted.addAll(standard);
      return sorted;
    } else {
      return super.getProperties(type);
    }
  }

  private class ModelPropertyComparator implements Comparator<Property> {
    private List<String> names;

    public ModelPropertyComparator(List<String> names) {
      this.names = names;
    }

    public int compare(Property o1, Property o2) {
      // important go first
      for (String name : names) {
        int c = compareByName(o1, o2, name);
        if (c != 0) {
          return c;
        }
      }
      // all the rest
      return o1.compareTo(o2);
    }

    private int compareByName(Property o1, Property o2, String name) {
      if (o1.getName().equals(name)) {
        return -1;
      } else if (o2.getName().equals(name)) {
        return 1;
      }
      return 0;// compare further
    }
  }
}
