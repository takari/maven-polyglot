/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy;

import groovy.util.IndentPrinter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

//
// NOTE: Augmented from Groovy's DomToGroovy.
//

/**
 * A SAX handler for turning XML into Groovy scripts
 *
 * @author James Strachan
 * @author paulk
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class Dom2Groovy {
    // FIXME: Re-extend from DomToGroovy once version us is released

    // FIXME: Need the full list, look at Types for hints on how to generate a full list
    protected final List<String> keywords = Arrays.asList(
            "class",
            "extends",
            "implements",
            "package",
            "return",
            "def",
            "try",
            "finally",
            "this",
            "new",
            "catch",
            "switch",
            "case",
            "default",
            "while",
            "if",
            "else",
            "elseif",
            "private",
            "protected",
            "final",
            "for",
            "in",
            "byte",
            "short",
            "break",
            "instanceof",
            "synchronized",
            "const",
            "float",
            "null",
            "throws",
            "do",
            "super",
            "with",
            "threadsafe",
            "transient",
            "native",
            "interface",
            "any",
            "double",
            "volatile",
            "as",
            "assert",
            "goto",
            "enum",
            "int",
            "boolean",
            "char",
            "false",
            "true",
            "static",
            "abstract",
            "continue",
            "import",
            "void",
            "long");

    protected final IndentPrinter out;

    protected boolean inMixed = false;

    // FIXME: Do not quote things like 'true', 'false' or numbers
    protected String qt = "'";

    public Dom2Groovy(final PrintWriter out) {
        this(new IndentPrinter(out));
    }

    public Dom2Groovy(final IndentPrinter out) {
        assert out != null;
        this.out = out;
    }

    public void print(final Document document) {
        printChildren(document, new HashMap());
    }

    public void print(final Reader input) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new InputSource(input));
        print(doc);
    }

    protected void print(final Node node, final Map namespaces, final boolean endWithComma) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                printElement((Element) node, namespaces, endWithComma);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                printPI((ProcessingInstruction) node, endWithComma);
                break;
            case Node.TEXT_NODE:
                printText((Text) node, endWithComma);
                break;
            case Node.COMMENT_NODE:
                printComment((Comment) node, endWithComma);
                break;
        }
    }

    protected void printElement(final Element element, Map namespaces, final boolean endWithComma) {
        namespaces = defineNamespaces(element, namespaces);

        element.normalize();
        printIndent();

        String prefix = element.getPrefix();
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        String localName = getLocalName(element);
        boolean isKeyword = checkEscaping(localName);
        if (isKeyword || hasPrefix) print(qt);
        if (hasPrefix) {
            print(prefix);
            print(".");
        }
        print(localName);
        if (isKeyword || hasPrefix) print(qt);

        boolean hasAttributes = printAttributes(element);

        //
        // FIXME: Allow omitting () from empty attributes... needs some more work,
        //        move back to DomToGroovy once sorted
        //

        NodeList list = element.getChildNodes();
        int length = list.getLength();
        if (length == 0) {
            // empty xml <element/> translates to groovy 'element {}'
            printEnd(" {}", endWithComma);
        } else {
            print(" ");
            Node node = list.item(0);
            if (length == 1 && node instanceof Text) {
                Text textNode = (Text) node;
                String text = getTextNodeData(textNode);
                if (hasAttributes) print(", ");
                printQuoted(text);
                printEnd("", endWithComma);
            } else if (mixedContent(list)) {
                println("{");
                out.incrementIndent();
                boolean oldInMixed = inMixed;
                inMixed = true;
                for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                    print(node, namespaces, false);
                }
                inMixed = oldInMixed;
                out.decrementIndent();
                printIndent();
                printEnd("}", endWithComma);
            } else {
                println("{");
                out.incrementIndent();
                printChildren(element, namespaces);
                out.decrementIndent();
                printIndent();
                printEnd("}", endWithComma);
            }
        }
    }

    protected void printQuoted(final String text) {
        if (text.contains("\n")) {
            print("'''");
            print(text);
            print("'''");
        } else {
            print(qt);
            print(escapeQuote(text));
            print(qt);
        }
    }

    protected void printPI(final ProcessingInstruction instruction, final boolean endWithComma) {
        printIndent();
        print("mkp.pi(" + qt);
        print(instruction.getTarget());
        print(qt + ", " + qt);
        print(instruction.getData());
        printEnd(qt + ");", endWithComma);
    }

    protected void printComment(final Comment comment, final boolean endWithComma) {
        String text = comment.getData().trim();
        if (text.length() > 0) {
            printIndent();
            print("/* ");
            print(text);
            printEnd(" */", endWithComma);
        }
    }

    protected void printText(final Text node, final boolean endWithComma) {
        String text = getTextNodeData(node);
        if (text.length() > 0) {
            printIndent();
            if (inMixed) print("mkp.yield ");
            printQuoted(text);
            printEnd("", endWithComma);
        }
    }

    protected String escapeQuote(final String text) {
        return text.replaceAll("\\\\", "\\\\\\\\").replaceAll(qt, "\\\\" + qt);
    }

    protected Map defineNamespaces(final Element element, final Map namespaces) {
        Map answer = null;
        String prefix = element.getPrefix();
        if (prefix != null && prefix.length() > 0 && !namespaces.containsKey(prefix)) {
            answer = new HashMap(namespaces);
            defineNamespace(answer, prefix, element.getNamespaceURI());
        }
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Attr attribute = (Attr) attributes.item(i);
            prefix = attribute.getPrefix();
            if (prefix != null && prefix.length() > 0 && !namespaces.containsKey(prefix)) {
                if (answer == null) {
                    answer = new HashMap(namespaces);
                }
                defineNamespace(answer, prefix, attribute.getNamespaceURI());
            }
        }
        return (answer != null) ? answer : namespaces;
    }

    protected void defineNamespace(final Map namespaces, final String prefix, final String uri) {
        namespaces.put(prefix, uri);
        if (!prefix.equals("xmlns") && !prefix.equals("xml")) {
            printIndent();
            print("mkp.declareNamespace(");
            print(prefix);
            print(":" + qt);
            print(uri);
            println(qt + ")");
        }
    }

    protected boolean printAttributes(final Element element) {
        boolean hasAttribute = false;
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        if (length > 0) {
            print("(");
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < length; i++) {
                printAttributeWithPrefix((Attr) attributes.item(i), buffer);
            }
            for (int i = 0; i < length; i++) {
                hasAttribute = printAttributeWithoutPrefix((Attr) attributes.item(i), hasAttribute);
            }
            if (buffer.length() > 0) {
                if (hasAttribute) {
                    print(", ");
                }
                print(buffer.toString());
                hasAttribute = true;
            }
            print(")");
        }
        return hasAttribute;
    }

    protected void printAttributeWithPrefix(final Attr attribute, final StringBuffer buffer) {
        String prefix = attribute.getPrefix();
        if (prefix != null && prefix.length() > 0 && !prefix.equals("xmlns")) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(qt);
            buffer.append(prefix);
            buffer.append(":");
            buffer.append(getLocalName(attribute));
            buffer.append(qt + ":" + qt);
            buffer.append(escapeQuote(getAttributeValue(attribute)));
            buffer.append(qt);
        }
    }

    protected String getAttributeValue(final Attr attribute) {
        return attribute.getValue();
    }

    protected boolean printAttributeWithoutPrefix(final Attr attribute, boolean hasAttribute) {
        String prefix = attribute.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            if (!hasAttribute) {
                hasAttribute = true;
            } else {
                print(", ");
            }
            String localName = getLocalName(attribute);
            boolean needsEscaping = checkEscaping(localName);
            if (needsEscaping) print(qt);
            print(localName);
            if (needsEscaping) print(qt);
            print(":");
            printQuoted(getAttributeValue(attribute));
        }
        return hasAttribute;
    }

    protected boolean checkEscaping(final String localName) {
        return keywords.contains(localName)
                || localName.contains("-")
                || localName.contains(".")
                || localName.contains(":");
    }

    protected String getTextNodeData(final Text node) {
        return node.getData().trim();
    }

    protected boolean mixedContent(final NodeList list) {
        boolean hasText = false;
        boolean hasElement = false;
        for (int i = 0, size = list.getLength(); i < size; i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                hasElement = true;
            } else if (node instanceof Text) {
                String text = getTextNodeData((Text) node);
                if (text.length() > 0) {
                    hasText = true;
                }
            }
        }
        return hasText && hasElement;
    }

    protected void printChildren(final Node parent, final Map namespaces) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            print(node, namespaces, false);
        }
    }

    protected String getLocalName(final Node node) {
        String answer = node.getLocalName();
        if (answer == null) {
            answer = node.getNodeName();
        }
        return answer.trim();
    }

    protected void printEnd(final String text, final boolean endWithComma) {
        if (endWithComma) {
            print(text);
            println(",");
        } else {
            println(text);
        }
    }

    protected void println(final String text) {
        out.println(text);
    }

    protected void print(final String text) {
        out.print(text);
    }

    protected void printIndent() {
        out.printIndent();
    }
}
