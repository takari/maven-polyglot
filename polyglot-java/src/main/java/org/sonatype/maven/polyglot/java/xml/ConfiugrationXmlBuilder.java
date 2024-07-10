package org.sonatype.maven.polyglot.java.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class ConfiugrationXmlBuilder {

    XMLOutputFactory factory = createFactory();
    ByteArrayOutputStream outputStream;
    XMLStreamWriter writer;

    public ConfiugrationXmlBuilder() {}

    protected XMLStreamWriter createWriter(OutputStream outputStream) {
        try {
            return factory.createXMLStreamWriter(outputStream);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    protected XMLOutputFactory createFactory() {
        return XMLOutputFactory.newInstance();
    }

    public ConfiugrationXmlBuilder startXML() {

        this.outputStream = new ByteArrayOutputStream();
        this.writer = createWriter(outputStream);

        try {
            writer.writeStartDocument();
            return this;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public Xpp3Dom endXML() {
        try {
            writer.writeEndDocument();
            writer.flush();
            writer.close();

            Xpp3Dom dom = null;
            try {
                dom = Xpp3DomBuilder.build(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())));
            } catch (XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return dom;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfiugrationXmlBuilder tag(String name, TagFunction scopeFunction) {
        try {
            writer.writeStartElement(name);
            scopeFunction.apply(new TagContext(name, this));
            writer.writeEndElement();
            return this;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public interface TagFunction {
        void apply(TagContext tagContext);
    }

    public class TagContext {
        String name;
        private ConfiugrationXmlBuilder xmlBuilder;

        public TagContext(String name, ConfiugrationXmlBuilder xmlBuilder) {
            this.name = name;
            this.xmlBuilder = xmlBuilder;
        }

        public String getName() {
            return name;
        }

        public TagContext attribute(String name, String value) {
            try {
                writer.writeAttribute(name, value);
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public TagContext content(Object content) {
            try {
                writer.writeCharacters(content.toString());
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public ConfiugrationXmlBuilder child(String name, TagFunction scopeFunction) {
            return xmlBuilder.tag(name, scopeFunction);
        }

        public TagContext child(String name) {
            try {
                writer.writeEmptyElement(name);
                return this;
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }

        public TagContext cdata(Object content) {
            try {
                writer.writeCData(content.toString());
                return this;
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
