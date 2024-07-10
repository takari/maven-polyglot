/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Taken from the Loop programming language compiler pipeline.
 *
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class Tokenizer {
    private final String input;

    public Tokenizer(String input) {
        this.input = input.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static final int NON = 0; // MUST be zero
    private static final int SINGLE_TOKEN = 1;
    private static final int SEQUENCE_TOKEN = 2;

    private static final int[] DELIMITERS = new int[256];
    private static final boolean[] STRING_TERMINATORS = new boolean[256];

    static {
        DELIMITERS['+'] = SEQUENCE_TOKEN;
        DELIMITERS['/'] = SEQUENCE_TOKEN;
        DELIMITERS['*'] = SEQUENCE_TOKEN;
        DELIMITERS['>'] = SEQUENCE_TOKEN;
        DELIMITERS['<'] = SEQUENCE_TOKEN;
        DELIMITERS['!'] = SEQUENCE_TOKEN;
        DELIMITERS['?'] = SEQUENCE_TOKEN;

        // SINGLE token delimiters are one char in length in any context
        DELIMITERS['\n'] = SINGLE_TOKEN;
        DELIMITERS['.'] = SINGLE_TOKEN;
        DELIMITERS[','] = SINGLE_TOKEN;
        DELIMITERS[':'] = SINGLE_TOKEN;
        DELIMITERS['('] = SINGLE_TOKEN;
        DELIMITERS[')'] = SINGLE_TOKEN;
        DELIMITERS['['] = SINGLE_TOKEN;
        DELIMITERS[']'] = SINGLE_TOKEN;
        DELIMITERS['{'] = SINGLE_TOKEN;
        DELIMITERS['}'] = SINGLE_TOKEN;
        DELIMITERS['@'] = SINGLE_TOKEN;

        STRING_TERMINATORS['"'] = true;
        STRING_TERMINATORS['\''] = true;
        STRING_TERMINATORS['`'] = true;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<Token>();
        char[] input = this.input.toCharArray();

        int i = 0, start = 0;
        boolean inWhitespace = false, inDelimiter = false, inComment = false, leading = true;
        char inStringSequence = 0;
        for (; i < input.length; i++) {
            char c = input[i];

            // strings and sequences
            if (STRING_TERMINATORS[c]) {

                if (inStringSequence > 0) {

                    // end of the current string sequence. bake.
                    if (inStringSequence == c) {
                        // +1 to include the terminating token.
                        bakeToken(tokens, input, i + 1, start);
                        start = i + 1;

                        inStringSequence = 0; // reset to normal language
                        continue;
                    }
                    // it's a string terminator but it's ok, it's part of the string, ignore...

                } else {
                    // Also bake if there is any leading tokenage.
                    if (i > start) {
                        bakeToken(tokens, input, i, start);
                        start = i;
                    }

                    inStringSequence = c; // start string
                }
            }

            // skip everything if we're in a string...
            if (inStringSequence > 0) continue;

            if (c == '\n') {
                leading = true;
            }

            // Comments beginning with #
            if (c == '#') {
                inComment = true;
            }

            // We run the comment until the end of the line
            if (inComment) {
                if (c == '\n') inComment = false;
                start = i;
                continue;
            }

            // whitespace is ignored unless it is leading...
            if (isWhitespace(c)) {
                inDelimiter = false;

                if (!inWhitespace) {
                    // bake token
                    bakeToken(tokens, input, i, start);
                    inWhitespace = true;
                }

                // leading whitespace is a special token...
                if (leading) {
                    tokens.add(new Token(" ", Token.Kind.INDENT));
                }

                // skip whitespace
                start = i + 1;
                continue;
            }

            // any non-whitespace character encountered
            inWhitespace = false;
            if (c != '\n') leading = false;

            // For delimiters that are 1-char long in all contexts,
            // break early.
            if (isSingleTokenDelimiter(c)) {

                bakeToken(tokens, input, i, start);
                start = i;

                // Also add the delimiter.
                bakeToken(tokens, input, i + 1, start);
                start = i + 1;
                continue;
            }

            // is delimiter
            if (isDelimiter(c)) {

                if (!inDelimiter) {
                    bakeToken(tokens, input, i, start);
                    inDelimiter = true;
                    start = i;
                }

                continue;
            }

            // if coming out of a delimiter, we still need to bake
            if (inDelimiter) {
                bakeToken(tokens, input, i, start);
                start = i;
                inDelimiter = false;
            }
        }

        // collect residual token
        if (i > start && !inComment) {
            // we don't want trailing whitespace
            bakeToken(tokens, input, i, start);
        }

        return tokens;
    }

    private static boolean isWhitespace(char c) {
        return '\n' != c && Character.isWhitespace(c);
    }

    static boolean isSingleTokenDelimiter(char c) {
        return DELIMITERS[c] == SINGLE_TOKEN;
    }

    public static String detokenize(List<Token> tokens) {
        StringBuilder builder = new StringBuilder();

        for (Token token : tokens) {
            if (Token.Kind.INDENT == token.kind) builder.append("~");
            else builder.append(token.value);
            builder.append(' ');
        }

        return builder.toString().trim();
    }

    private static boolean isDelimiter(char c) {
        return DELIMITERS[c] != NON;
    }

    private static void bakeToken(List<Token> tokens, char[] input, int i, int start) {
        if (i > start) {
            String value = new String(input, start, i - start);

            // remove this disgusting hack when you can fix the lexer.
            tokens.add(new Token(value, Token.Kind.determine(value)));
        }
    }
}
