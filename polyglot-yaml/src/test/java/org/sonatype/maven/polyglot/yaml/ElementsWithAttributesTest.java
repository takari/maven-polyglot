package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.assertEquals;
import static org.sonatype.maven.polyglot.yaml.Util.getModel;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

public class ElementsWithAttributesTest {
    @Test
    public void testCompactExample() throws Exception {
        Model model = getModel("elements-with-attributes-example.yaml");
        Xpp3Dom configuration = (Xpp3Dom)
                model.getBuild().getPlugins().get(0).getExecutions().get(0).getConfiguration();

        assertEquals(
                "Hello from polyglot-yaml",
                configuration.getChild("target").getChild("echo").getAttribute("message"));
    }
}
