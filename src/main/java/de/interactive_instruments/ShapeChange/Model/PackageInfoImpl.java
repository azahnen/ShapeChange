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
 * (c) 2002-2012 interactive instruments GmbH, Bonn, Germany
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

package de.interactive_instruments.ShapeChange.Model;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.platform.commons.util.StringUtils;

import de.interactive_instruments.ShapeChange.ShapeChangeResult.MessageContext;

public abstract class PackageInfoImpl extends InfoImpl implements PackageInfo {

	protected List<ImageMetadata> diagrams = null;

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: This method is intended to be "final", but not actually declared
	 * as such. A depending project can thus extend the method, if absolutely
	 * necessary.
	 */
	@Override
	public String language() {
		String lang = this.taggedValue("language");

		if (StringUtils.isBlank(lang)) {
			if (this.isSchema())
				return null;
			PackageInfo pi = this.owner();
			if (pi != null)
				return pi.language();
		} else
			return lang;

		return null;
	}

	/** Return the encoding rule relevant on the package, given the platform */
	public String encodingRule(String platform) {
		String s;
		if (options().ignoreEncodingRuleTaggedValues()) {
			s = super.encodingRule(platform);
		} else {
			s = taggedValue(platform + "EncodingRule");
			if (s == null || s.isEmpty()) {
				PackageInfo o = owner();
				if (o != null) {
					s = o.encodingRule(platform);
				}
				if (s == null)
					s = super.encodingRule(platform);
			}
		}
		if (s != null)
			s = s.toLowerCase();
		return s;
	}

	@Override
	public String targetNamespace() {
		String s = options().nsOfPackage(name());
		if (s != null) {
			s = s.trim();
			return s;
		}
		s = taggedValue("targetNamespace");
		if (s == null) {
			s = taggedValue("xmlNamespace");
		}
		if (s == null) {
			PackageInfo o = owner();
			if (o != null) {
				s = o.targetNamespace();
			}
		}
		if (s != null) {
			s = s.trim();
		}
		return s;
	}

	@Override
	public String xmlns() {
		String s = options().nsabrOfPackage(name());
		if (s != null) {
			s = s.trim();
			return s;
		}
		s = taggedValue("xmlns");
		if (s == null) {
			s = taggedValue("xmlNamespaceAbbreviation");
		}
		if (s == null) {
			PackageInfo o = owner();
			if (o != null) {
				s = o.xmlns();
			}
		}
		if (s != null) {
			s = s.trim();
			s = s.replace(":", "").replace("/", "");
		}

		return s;
	}

	@Override
	public String xsdDocument() {
		String s = options().xsdOfPackage(name());
		if (s != null) {
			s = s.trim();
			return s;
		}
		s = taggedValue("xsdDocument");
		if (s == null) {
			s = taggedValue("xsdName");
		}
		if (s == null) {
		    
			if (isAppSchema()) {
			    
			    String name = name();
			    StringBuffer str = new StringBuffer();
				int len = name != null ? name.length() : 0;
				for (int i = 0; i < len; i++) {
					char ch = name.charAt(i);
					switch (ch) {
					case ' ': {
						break;
					}
					default: {
						str.append(ch);
					}
					}
				}
				s = str.toString() + ".xsd";
				
				result().addWarning(null, 101, name(), id(), s);
			}
		}
		if (s != null) {
			s = s.trim();
		}

		return s;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: This method is intended to be "final", but not actually declared
	 * as such. A depending project can thus extend the method, if absolutely
	 * necessary.
	 */
	@Override
	public SortedSet<PackageInfo> containedPackagesInSameTargetNamespace() {

		SortedSet<PackageInfo> result = new TreeSet<PackageInfo>();

		if (containedPackages() != null) {

			for (PackageInfo childPkg : containedPackages()) {

				if ((targetNamespace() == null
						&& childPkg.targetNamespace() == null)
						|| targetNamespace()
								.equals(childPkg.targetNamespace())) {

					result.add(childPkg);
					result.addAll(
							childPkg.containedPackagesInSameTargetNamespace());
				}
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: This method is intended to be "final", but not actually declared
	 * as such. A depending project can thus extend the method, if absolutely
	 * necessary.
	 */
	@Override
	public String gmlProfileSchema() {
		return taggedValue("gmlProfileSchema");
	}

	/**
	 * @see de.interactive_instruments.ShapeChange.Model.PackageInfo#version()
	 */
	public String version() {
		String s = options().versionOfPackage(name());
		if (s != null) {
			s = s.trim();
			return s;
		}
		s = taggedValue("version");
		PackageInfo o = owner();
		if (s == null && o != null) {
			s = o.version();
		}
		if (s != null) {
			s = s.trim();
		}

		return s;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: This method is intended to be "final", but not actually declared
	 * as such. A depending project can thus extend the method, if absolutely
	 * necessary.
	 */
	@Override
	public boolean isAppSchema() {
		if (stereotype("application schema") || stereotype("schema")) {
			return true;
		}
		return false;
	}

	/**
	 * Note: Additional support for deprecated tag "xmlNamespace".
	 * 
	 * @see de.interactive_instruments.ShapeChange.Model.PackageInfo#isSchema()
	 */
	public boolean isSchema() {
		String s = taggedValue("targetNamespace");
		if (s == null) {
			s = taggedValue("xmlNamespace");
		}
		if (s != null) {
			return true;
		}
		if (options().nsOfPackage(name()) != null) {
			return true;
		}
		return false;
	} // isSchema()

	@Override
	public String schemaId() {
		if (isSchema()) {
			return id();
		}
		PackageInfo o = owner();
		if (o != null) {
			return o.schemaId();
		}
		return "(null)";
	} // schemaId()

	@Override
	public String fullName() {
		String qualname = null;
		PackageInfo pi = this;
		while (pi != null) {
			if (qualname == null)
				qualname = pi.name();
			else
				qualname = pi.name() + "::" + qualname;
			pi = pi.owner();
		}
		return qualname;
	}

	@Override
	public String fullNameInSchema() {

		if (this.targetNamespace() == null) {

			return this.fullName();

		} else {

			String qualname = this.name();
			PackageInfo pi = this.owner();

			while (pi != null && (pi.targetNamespace() != null
					&& pi.targetNamespace().equals(this.targetNamespace()))) {

				qualname = pi.name() + "::" + qualname;
				pi = pi.owner();
			}
			return qualname;
		}
	}

	/*
	 * Validate the package against all applicable requirements and
	 * recommendations
	 */
	public void postprocessAfterLoadingAndValidate() {
		if (postprocessed)
			return;

		super.postprocessAfterLoadingAndValidate();

		/*
		 * Verify tagged values
		 */
		String s;
		if (matches("req-xsd-pkg-targetNamespace")) {
			s = taggedValue("targetNamespace");
			if (s == null || s.isEmpty()) {
				if (isAppSchema()) {
					MessageContext mc = result().addError(null, 146, name(),
							"targetNamespace");
					if (mc != null)
						mc.addDetail(null, 400, "Package", fullName());
				}
			} else if (s.equalsIgnoreCase("fixme")) {
				MessageContext mc = result().addError(null, 150, name(),
						"targetNamespace", s);
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			} else if (matches("req-xsd-pkg-namespace-schema-only")
					&& !isAppSchema()) {
				MessageContext mc = result().addError(null, 147, name(),
						"targetNamespace");
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			}
		}

		if (matches("req-xsd-pkg-xmlns") && this.isAppSchema()) {
			s = taggedValue("xmlns");
			if (s == null || s.isEmpty()) {
				if (isAppSchema()) {
					MessageContext mc = result().addError(null, 146, name(),
							"xmlns");
					if (mc != null)
						mc.addDetail(null, 400, "Package", fullName());
				}
			} else if (s.equalsIgnoreCase("fixme")) {
				MessageContext mc = result().addError(null, 150, name(),
						"xmlns", s);
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			} else if (matches("req-xsd-pkg-namespace-schema-only")
					&& !isAppSchema()) {
				MessageContext mc = result().addError(null, 147, name(),
						"xmlns");
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			}
		}

		if (matches("req-xsd-pkg-xsdDocument") && this.isAppSchema()) {
			s = taggedValue("xsdDocument");
			if (s == null || s.isEmpty()) {
				if (isAppSchema()) {
					MessageContext mc = result().addError(null, 146, name(),
							"xsdDocument");
					if (mc != null)
						mc.addDetail(null, 400, "Package", fullName());
				}
			} else if (s.equalsIgnoreCase("fixme")) {
				MessageContext mc = result().addError(null, 150, name(),
						"xsdDocument", s);
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			}
		}

		if (matches("rec-xsd-pkg-version")) {
			s = taggedValue("version");
			if (s == null || s.isEmpty()) {
				if (isAppSchema()) {
					MessageContext mc = result().addWarning(null, 146, name(),
							"version");
					if (mc != null)
						mc.addDetail(null, 400, "Package", fullName());
				}
			} else if (s.equalsIgnoreCase("fixme")) {
				MessageContext mc = result().addWarning(null, 150, name(),
						"version", s);
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			}
		}

		if (matches("req-all-all-documentation")) {
			s = documentation();
			if (!s.contains(options().nameSeparator())) {
				MessageContext mc = result().addError(null, 151, name(),
						options().nameSeparator());
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			}
			if (!s.contains(options().definitionSeparator())) {
				MessageContext mc = result().addError(null, 151, name(),
						options().definitionSeparator());
				if (mc != null)
					mc.addDetail(null, 400, "Package", fullName());
			}
		}

		if (matches("req-xsd-pkg-dependencies")) {
			for (String pid : supplierIds()) {
				if (!this.isSchema()) {
					MessageContext mc = result().addError(null, 159, name());
					if (mc != null)
						mc.addDetail(null, 400, "Package", fullName());
				}
				PackageInfo pi = model().packageById(pid);
				if (pi == null) {
					MessageContext mc = result().addError(null, 161, "Package",
							pid);
					if (mc != null)
						mc.addDetail(null, 400, "Package", fullName());
				} else {
					if (!pi.isSchema()) {
						MessageContext mc = result().addError(null, 160,
								pi.name(), name());
						if (mc != null)
							mc.addDetail(null, 400, "Package", fullName());
					}
				}
			}
		}

		// NEW EDB 12/7/12: Check that the xsdDocument tag, if present, ends
		// with .xsd. If not, create a warning.
		s = xsdDocument();
		if (s != null && !s.isEmpty() && !s.endsWith(".xsd")) {
			MessageContext mc = result().addWarning(null, 167, xsdDocument());
			if (mc != null)
				mc.addDetail(null, 400, "Package", fullName());
		}

		postprocessed = true;
	}

	public List<ImageMetadata> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<ImageMetadata> diagrams) {
		this.diagrams = diagrams;
	}

	/**
	 * @see de.interactive_instruments.ShapeChange.Model.PackageInfo#containedClasses()
	 */
	public SortedSet<ClassInfo> containedClasses() {

		SortedSet<ClassInfo> result = new TreeSet<ClassInfo>();

		SortedSet<ClassInfo> classes = this.model().classes(this);

		for (ClassInfo ci : classes) {
			if (ci.pkg() == this) {
				result.add(ci);
			}
		}

		return result;
	}

	@Override
	public PackageInfo rootPackage() {
		PackageInfo pi = this;
		while (pi != null && !pi.isSchema())
			pi = pi.owner();
		return pi;
	}
}
