package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.serializer.Serializer;

public class ModelRepresenterTest {
    private ModelRepresenter modelRepresenter;

    @Before
    public void setUp() throws Exception {
        modelRepresenter = new ModelRepresenter(new DumperOptions());
    }

    @Test
    public void representXpp3Dom() throws Exception {
        // given
        final Plugin plugin = new Plugin();

        InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("modelrepresenter/represent-xpp3-dom-lists.xml");
        Xpp3Dom configurationDom = Xpp3DomBuilder.build(input, "UTF-8");

        plugin.setConfiguration(configurationDom);
        plugin.setGroupId(null);

        String expectedRepresentation =
                Util.getLocalResource("modelrepresenter/represent-xpp3-dom-lists-expected.yaml");

        // when
        Node node = modelRepresenter.represent(plugin);

        // then
        Writer stringWriter = new StringWriter();
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setWidth(80);
        dumperOptions.setPrettyFlow(false);
        Serializer serializer =
                new Serializer(new Emitter(stringWriter, dumperOptions), new ModelResolver(), dumperOptions, Tag.MAP);
        serializer.open();
        serializer.serialize(node);
        serializer.close();

        assertEquals(expectedRepresentation.trim(), stringWriter.toString().trim());
    }
}
