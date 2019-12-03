/**
 * ShapeChange - processing application schemas for geographic information
 *
 * This file is part of ShapeChange. ShapeChange takes a ISO 19109 
 * Application Schema from a UML model and translates it into a 
 * GML Application Schema or other implementation representations.
 *
 * Additional information about the software can be found at
 * http://shapechange.net/
 *
 * (c) 2002-2019 interactive instruments GmbH, Bonn, Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact:
 * interactive instruments GmbH
 * Trierer Strasse 70-72
 * 53115 Bonn
 * Germany
 */
package de.interactive_instruments.ShapeChange;

import org.junit.Test;

public class SCXMLTest extends WindowsBasicTest {

	@Test
	public void testSCXML_loading_applyingDescriptorSources() {

		multiTest(
				"src/test/resources/scxml/applyingDescriptorSources/test_scxml_applyingDescriptorSources.xml",
				new String[] { "xml" },
				"testResults/scxml/applyingDescriptorSources/scxml",
				"src/test/resources/scxml/applyingDescriptorSources/reference/scxml");
	}
	
	@Test
	public void testSCXML_stereotypeNormalization() {

		multiTest(
				"src/test/resources/scxml/stereotypeNormalization/test_scxml_stereotypeNormalization.xml",
				new String[] { "xsd", "xml" },
				"testResults/scxml/stereotypeNormalization/results",
				"src/test/resources/scxml/stereotypeNormalization/reference/results");
	}
	
	@Test
	public void testSCXML_taggedValueNormalization() {

		multiTest(
				"src/test/resources/scxml/taggedValueNormalization/test_scxml_taggedValueNormalization.xml",
				new String[] { "xml" },
				"testResults/scxml/taggedValueNormalization/results",
				"src/test/resources/scxml/taggedValueNormalization/reference/results");
	}
	
	@Test
	public void testSCXML_includeConstraintDescriptions() {

		multiTest(
				"src/test/resources/scxml/includeConstraintDescriptions/test_scxml_includeConstraintDescriptions.xml",
				new String[] { "xml" },
				"testResults/scxml/includeConstraintDescriptions/results",
				"src/test/resources/scxml/includeConstraintDescriptions/reference/results");
	}
}
