/*
 * Copyright (c) 2015 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.sonatype.maven.polyglot.xml;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Test;

import com.cedarsoftware.util.DeepEquals;

public class TestReaderComparedToDefault {

	private static final String POM_MAVEN_V_4 = "/pom/pom_maven_v4.xml";
	private static final String POM_MAVEN_V_4_1 = "/pom/pom_maven_v4_1.xml";

	@Test
	public void testModelEqualToReference() throws IOException, XmlPullParserException {
		Model modelV4 = new org.apache.maven.model.io.xpp3.MavenXpp3Reader().read(getClass().getResourceAsStream(POM_MAVEN_V_4));
		Model modelV41 = new org.sonatype.maven.polyglot.xml.xpp3.MavenXpp3Reader().read(getClass().getResourceAsStream(POM_MAVEN_V_4_1));

		Assert.assertTrue(DeepEquals.deepEquals(modelV4, modelV41));
	}
	
	@Test
	public void testAssertionIncludesDeepProperties() throws IOException, XmlPullParserException {
		Model modelV4 = new org.apache.maven.model.io.xpp3.MavenXpp3Reader().read(getClass().getResourceAsStream(POM_MAVEN_V_4));
		Model modelV41 = new org.sonatype.maven.polyglot.xml.xpp3.MavenXpp3Reader().read(getClass().getResourceAsStream(POM_MAVEN_V_4_1));
		
		modelV4.getDependencyManagement().getDependencies().get(3).setArtifactId("shouldFailtest");

		Assert.assertFalse(DeepEquals.deepEquals(modelV4, modelV41));
	}
	
	@Test
	public void testParsedDependencyManagement() throws IOException, XmlPullParserException {
		Model modelV41 = new org.sonatype.maven.polyglot.xml.xpp3.MavenXpp3Reader().read(getClass().getResourceAsStream(POM_MAVEN_V_4_1));		

		Assert.assertTrue(modelV41.getDependencyManagement().getDependencies().size() == 36);
	}
}
