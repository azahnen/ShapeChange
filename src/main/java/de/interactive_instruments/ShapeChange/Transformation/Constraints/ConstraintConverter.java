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
 * (c) 2002-2017 interactive instruments GmbH, Bonn, Germany
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
package de.interactive_instruments.ShapeChange.Transformation.Constraints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

import de.interactive_instruments.ShapeChange.MessageSource;
import de.interactive_instruments.ShapeChange.Options;
import de.interactive_instruments.ShapeChange.ProcessRuleSet;
import de.interactive_instruments.ShapeChange.ShapeChangeAbortException;
import de.interactive_instruments.ShapeChange.ShapeChangeResult;
import de.interactive_instruments.ShapeChange.ShapeChangeResult.MessageContext;
import de.interactive_instruments.ShapeChange.TransformerConfiguration;
import de.interactive_instruments.ShapeChange.Model.ClassInfo;
import de.interactive_instruments.ShapeChange.Model.Constraint;
import de.interactive_instruments.ShapeChange.Model.PropertyInfo;
import de.interactive_instruments.ShapeChange.Model.Generic.GenericClassInfo;
import de.interactive_instruments.ShapeChange.Model.Generic.GenericModel;
import de.interactive_instruments.ShapeChange.Model.Generic.GenericPropertyInfo;
import de.interactive_instruments.ShapeChange.Transformation.Transformer;
import de.interactive_instruments.ShapeChange.Util.ValueTypeOptions;

/**
 * @author Johannes Echterhoff (echterhoff at interactive-instruments dot de)
 *
 */
public class ConstraintConverter implements Transformer, MessageSource {

    private static final Joiner commaJoiner = Joiner.on(",").skipNulls();

    /* ------------------------------------------- */
    /* --- configuration parameter identifiers --- */
    /* ------------------------------------------- */

    /**
     * Alias for {@value #PARAM_VALUETYPE_REP_CONSTRAINT_REGEX}
     */
    public static final String PARAM_GEOM_REP_CONSTRAINT_REGEX = "geometryRepresentationConstraintRegex";

    public static final String PARAM_VALUETYPE_REP_CONSTRAINT_REGEX = "valueTypeRepresentationConstraintRegex";

    public static final String PARAM_GEOM_REP_TYPES = "geometryRepresentationTypes";

    public static final String PARAM_VALUETYPE_REP_TYPES = "valueTypeRepresentationTypes";

    public static final String PARAM_GEOM_REP_VALUE_TYPE_REGEX = "geometryRepresentationValueTypeRegex";

    public static final String VALUE_TYPE_OPTIONS_TV_NAME = "valueTypeOptions";

    /* ------------------------ */
    /* --- rule identifiers --- */
    /* ------------------------ */

    public static final String RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_INCL = "rule-trf-cls-constraints-geometryRestrictionToGeometryTV-inclusion";

    public static final String RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_EXCL = "rule-trf-cls-constraints-geometryRestrictionToGeometryTV-exclusion";

    public static final String RULE_TRF_CLS_CONSTRAINTS_VALUETYPERESTRICTIONTOTV_EXCL = "rule-trf-cls-constraints-valueTypeRestrictionToTV-exclusion";

    public static final String RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_NORESTRICTION_BYVALUETYPE = "rule-trf-cls-constraints-geometryRestrictionToGeometryTV-typesWithoutRestriction-byValueTypeMatch";

    public static final String RULE_TRF_CLS_CONSTRAINTS_CODELIST_RESTRICTION_TO_TV = "rule-trf-cls-constraints-codeListRestrictionToTV";

    private Options options = null;
    private ShapeChangeResult result = null;

    @Override
    public void process(GenericModel genModel, Options options, TransformerConfiguration trfConfig,
	    ShapeChangeResult result) throws ShapeChangeAbortException {

	this.options = options;
	this.result = result;

	Map<String, ProcessRuleSet> ruleSets = trfConfig.getRuleSets();

	// get the set of all rules defined for the transformation
	Set<String> rules = new HashSet<String>();
	if (!ruleSets.isEmpty()) {
	    for (ProcessRuleSet ruleSet : ruleSets.values()) {
		if (ruleSet.getAdditionalRules() != null) {
		    rules.addAll(ruleSet.getAdditionalRules());
		}
	    }
	}

	/*
	 * because there are no mandatory - in other words default - rules for this
	 * transformer simply return the model if no rules are defined in the rule sets
	 * (which the schema allows)
	 */
	if (rules.isEmpty())
	    return;

	// apply pre-processing (nothing to do right now)

	// execute rules

	if (rules.contains(RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_INCL)) {

	    result.addProcessFlowInfo(null, 20103, RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_INCL);
	    applyRuleGeometryRestrictionToGeometryTaggedValue(genModel, trfConfig, rules, true);

	} else if (rules.contains(RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_EXCL)) {

	    result.addProcessFlowInfo(null, 20103, RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_EXCL);
	    applyRuleGeometryRestrictionToGeometryTaggedValue(genModel, trfConfig, rules, false);

	} else if (rules.contains(RULE_TRF_CLS_CONSTRAINTS_CODELIST_RESTRICTION_TO_TV)) {

	    result.addProcessFlowInfo(null, 20103, RULE_TRF_CLS_CONSTRAINTS_CODELIST_RESTRICTION_TO_TV);
	    applyRuleCodeListRestrictionToTaggedValue(genModel, trfConfig, rules);

	} else if (rules.contains(RULE_TRF_CLS_CONSTRAINTS_VALUETYPERESTRICTIONTOTV_EXCL)) {

	    result.addProcessFlowInfo(null, 20103, RULE_TRF_CLS_CONSTRAINTS_VALUETYPERESTRICTIONTOTV_EXCL);
	    applyRuleValueTypeRestrictionToPropertyTaggedValue(genModel, trfConfig, rules);
	}

	// apply post-processing (nothing to do right now)
    }

    /**
     * @param genModel
     * @param config
     * @param rules
     */
    private void applyRuleValueTypeRestrictionToPropertyTaggedValue(GenericModel genModel,
	    TransformerConfiguration config, Set<String> rules) {

	String valueTypeRepConstrRegex = config.parameterAsString(PARAM_VALUETYPE_REP_CONSTRAINT_REGEX, null, false,
		false);
	Pattern valueTypeRepConstrPattern = null;

	if (valueTypeRepConstrRegex != null) {
	    // parse regex
	    try {
		valueTypeRepConstrPattern = Pattern.compile(valueTypeRepConstrRegex);
	    } catch (PatternSyntaxException e) {
		// reported via configuration validator
	    }
	}

	List<String> valueTypesRepTypesIn = config.parameterAsStringList(ConstraintConverter.PARAM_VALUETYPE_REP_TYPES,
		null, true, true, ";");

	/*
	 * key: Supertype name; value: map with key: name of type within inheritance
	 * hierarchy (can include the supertype and abstract types), value: an optional
	 * with a string representing the name to which the type name shall be mapped
	 * (can be empty if the type name shall be used as-is)
	 */
	SortedMap<String, SortedMap<String, Optional<String>>> valueTypeRepTypes = new TreeMap<>();
	boolean foundInvalidValueTypeRepTypeValue = false;

	if (valueTypesRepTypesIn != null && !valueTypesRepTypesIn.isEmpty()) {

	    Pattern valueTypeRepTypesDetectionPattern = Pattern.compile("^\\s*(\\w+)\\s*\\{(.*)\\}\\s*$");

	    for (String s : valueTypesRepTypesIn) {

		// example for s: supertype {t1, t2=x}
		Matcher m = valueTypeRepTypesDetectionPattern.matcher(s);

		if (m.matches()) {

		    String supertypeName = m.group(1);
		    String suptertypeDefinitions = m.group(2);

		    String[] mappings = StringUtils.split(suptertypeDefinitions, ",");
		    SortedMap<String, Optional<String>> typeMap = new TreeMap<>();

		    for (String mapping : mappings) {

			String[] map = StringUtils.split(mapping, "=");

			if (map.length > 2 || map[0].trim().isEmpty()) {

			    foundInvalidValueTypeRepTypeValue = true;
			    break;
			    // details are reported via configuration validator

			} else {

			    String typeMapKey = map[0].trim();
			    String typeMapValue = map.length == 2 ? map[1].trim() : null;

			    typeMap.put(typeMapKey, Optional.ofNullable(typeMapValue));
			}
		    }

		    valueTypeRepTypes.put(supertypeName, typeMap);

		} else {
		    // reported by configuration validator TODO
		}
	    }
	}

	if (foundInvalidValueTypeRepTypeValue || valueTypeRepConstrPattern == null || valueTypeRepTypes.size() == 0) {

	    result.addError(this, 300);

	} else {

	    Pattern propertyNameDetectionPattern = Pattern.compile("^.*inv: (self\\.)?(\\w+).*$");

	    Pattern disallowedKindDetectionPattern = Pattern.compile("oclIsKindOf\\((\\w+)");
	    Pattern disallowedTypeDetectionPattern = Pattern.compile("oclIsTypeOf\\((\\w+)");

	    for (GenericClassInfo genCi : genModel.selectedSchemaClasses()) {

		if (genCi.category() == Options.ENUMERATION || genCi.category() == Options.CODELIST) {
		    continue;
		}

		ValueTypeOptions vto = new ValueTypeOptions();

		for (Constraint con : genCi.constraints()) {

		    Matcher matcherOnConstraintName = valueTypeRepConstrPattern.matcher(con.name());

		    if (matcherOnConstraintName.matches()) {

			// First, identify the property name (can be from a supertype)
			Matcher matcherOnPropertyName = propertyNameDetectionPattern.matcher(con.text());

			if (matcherOnPropertyName.matches()) {

			    String propertyName = matcherOnPropertyName.group(2);

			    /*
			     * The class must have a property of that name, otherwise OCL parsing would not
			     * have succeeded. So no need to check that the property exists. However, we
			     * need to check if the value type of the property is one of the supertypes for
			     * which a type restriction is defined.
			     */
			    PropertyInfo genPi = genCi.property(propertyName);
			    String propertyValueTypeName = genPi.typeInfo().name;

			    if (valueTypeRepTypes.containsKey(propertyValueTypeName)) {

				/*
				 * We need to identify which types are disallowed by the constraint (taking into
				 * account the difference between oclIsTypeOf and oclIsKindOf)
				 */
				Set<String> disallowedTypes = new HashSet<>();

				Matcher matcherOnOclIsKindOf = disallowedKindDetectionPattern.matcher(con.text());
				while (matcherOnOclIsKindOf.find()) {
				    String type = matcherOnOclIsKindOf.group(1);
				    disallowedTypes.add(type);
				    // identify all subtypes
				    ClassInfo typeCi = genModel.classByName(type);
				    if (typeCi != null) {
					SortedSet<ClassInfo> allSubtypes = typeCi.subtypesInCompleteHierarchy();
					for (ClassInfo subtype : allSubtypes) {
					    disallowedTypes.add(subtype.name());
					}
				    }
				}

				Matcher matcherOnOclIsTypeOf = disallowedTypeDetectionPattern.matcher(con.text());
				while (matcherOnOclIsTypeOf.find()) {
				    String type = matcherOnOclIsTypeOf.group(1);
				    disallowedTypes.add(type);
				}

				/*
				 * Now we need to identify which types are generally allowed, taking into
				 * account that transformation parameter 'valueTypeRepresentationTypes' may
				 * contain the name of a supertype but not the names of its subtypes.
				 */

				SortedMap<String, Optional<String>> typeMap = valueTypeRepTypes
					.get(propertyValueTypeName);

				SortedMap<String, Optional<String>> generallyAllowedTypeMap = new TreeMap<>();
				for (Entry<String, Optional<String>> e : typeMap.entrySet()) {
				    String generallyAllowedTypeName = e.getKey();
				    // can be null
				    Optional<String> vtTargetName = e.getValue();

				    generallyAllowedTypeMap.put(generallyAllowedTypeName, vtTargetName);

				    ClassInfo typeCi = genModel.classByName(generallyAllowedTypeName);
				    if (typeCi != null) {
					SortedSet<ClassInfo> allSubtypes = typeCi.subtypesInCompleteHierarchy();
					for (ClassInfo subtype : allSubtypes) {
					    generallyAllowedTypeMap.put(subtype.name(), typeMap.get(subtype.name()));
					}
				    }
				}

				/*
				 * now identify the types from the supertype hierarchy that are allowed for the
				 * property
				 */
				SortedSet<String> allowedTypeNames = new TreeSet<>();

				for (Entry<String, Optional<String>> valueType : generallyAllowedTypeMap.entrySet()) {

				    String vtName = valueType.getKey();
				    // can be null
				    Optional<String> vtTargetName = valueType.getValue();

				    if (!disallowedTypes.contains(vtName)) {

					if (vtTargetName == null || vtTargetName.isEmpty()) {
					    allowedTypeNames.add(vtName);
					} else {
					    allowedTypeNames.add(vtTargetName.get());
					}
				    }
				}

				if (allowedTypeNames.size() > 0) {
				    vto.add(propertyName,
					    (!genPi.isAttribute() && genPi.association().assocClass() != null),
					    allowedTypeNames);
				}
			    }
			}
		    }
		}

		if (!vto.isEmpty()) {

		    // create tag value

		    // check overwrite - warn if a value already exists for the tag
		    String oldValue = genCi.taggedValue(VALUE_TYPE_OPTIONS_TV_NAME);

		    if (StringUtils.isNotBlank(oldValue)) {
			MessageContext mc = result.addWarning(this, 301, genCi.name(), oldValue, vto.toString());
			if (mc != null) {
			    mc.addDetail(this, 1, genCi.fullName());
			}
		    }

		    // finally, set new tag value
		    genCi.setTaggedValue(VALUE_TYPE_OPTIONS_TV_NAME, vto.toString(), false);
		}
	    }
	}
    }

    private void applyRuleCodeListRestrictionToTaggedValue(GenericModel genModel, TransformerConfiguration trfConfig,
	    Set<String> rules) {

	/*
	 * Regular expression for property with max mult = 1:
	 * (?s).*inv:\s*(?:(?:self\.)?(?:\w+)->notEmpty\(\)
	 * implies)?\s*(?:self\.)?(\w+)\.oclIsTypeOf\((\w+)\)\s*
	 * 
	 * The expression supports optional "self." in the expression, as well as a
	 * check that the property is not empty.
	 * 
	 * group 1: property name
	 * 
	 * group 2: type name
	 */
	Pattern regex1 = Pattern.compile(
		"(?s).*inv:\\s*(?:(?:self\\.)?(?:\\w+)->notEmpty\\(\\) implies)?\\s*(?:self\\.)?(\\w+)\\.oclIsTypeOf\\((\\w+)\\)\\s*");

	/*
	 * Regular expression that covers the case of a property with max mult > 1:
	 * 
	 * (?s).*inv:\s*(?:(?:self\.)?\w+->notEmpty\(\) implies
	 * )?(?:self\.)?(\w+)->forAll\(\w+\|\w+\.oclIsTypeOf\((\w+)\)\)\s*
	 * 
	 * group 1: property name
	 * 
	 * group 2: type name
	 */
	Pattern regex2 = Pattern.compile(
		"(?s).*inv:\\s*(?:(?:self\\.)?\\w+->notEmpty\\(\\) implies )?(?:self\\.)?(\\w+)->forAll\\(\\w+\\|\\w+\\.oclIsTypeOf\\((\\w+)\\)\\)\\s*");

	for (GenericClassInfo genCi : genModel.selectedSchemaClasses()) {

	    // ignore enumerations, code lists, and basic types
	    if (genCi.category() == Options.ENUMERATION || genCi.category() == Options.CODELIST
		    || genCi.category() == Options.BASICTYPE) {
		continue;
	    }

	    for (Constraint con : genCi.constraints()) {

		// use regex to identify the property name and type restriction
		Matcher matcher = null;
		Matcher matcher1 = regex1.matcher(con.text());
		Matcher matcher2 = regex2.matcher(con.text());

		if (matcher1.matches()) {
		    matcher = matcher1;
		} else if (matcher2.matches()) {
		    matcher = matcher2;
		}

		if (matcher != null) {

		    String propName = matcher.group(1);
		    String typeName = matcher.group(2);

		    // check if type defined by restriction is a code list, and
		    // type of the property is CharacterString

		    PropertyInfo pi = genCi.property(propName);

		    if (pi == null) {
			result.addError(this, 200, con.name(), genCi.name(), propName);
			continue;
		    }

		    ClassInfo ci = genModel.classByName(typeName);

		    if (ci == null) {
			result.addError(this, 201, con.name(), genCi.name(), typeName);
			continue;
		    }

		    if (pi.typeInfo().name.equalsIgnoreCase("CharacterString") && ci.category() == Options.CODELIST) {

			result.addInfo(this, 202, con.name(), genCi.name(), propName, typeName);

			/*
			 * Add name of code list in TV 'codeListRestriction' on the property.
			 * 
			 * NOTE: This will NOT result in a subtype specific restriction. The TV is
			 * defined on the property itself, which may belong to a supertype of genCi,
			 * i.e. the class for which the constraint is defined. That should be sufficient
			 * for the case of a metadata profile, where a property from ISO 19115 (with
			 * type CharacterString) is restricted (via OCL constraint, typically in a
			 * subtype) to a code list.
			 */

			((GenericPropertyInfo) pi).setTaggedValue("codeListRestriction", typeName, false);
		    }
		}
	    }
	}
    }

    private void applyRuleGeometryRestrictionToGeometryTaggedValue(GenericModel genModel,
	    TransformerConfiguration config, Set<String> rules, boolean isInclusion) {

	String geomRepConstrRegex = config.parameterAsString(PARAM_GEOM_REP_CONSTRAINT_REGEX, null, false, false);
	Pattern geomRepConstrPattern = null;

	if (geomRepConstrRegex != null) {
	    // parse regex
	    try {
		geomRepConstrPattern = Pattern.compile(geomRepConstrRegex);
	    } catch (PatternSyntaxException e) {
		// reported via configuration validator
	    }
	}

	String geomRepValueTypeRegex = config.parameterAsString(PARAM_GEOM_REP_VALUE_TYPE_REGEX, null, false, false);
	Pattern geomRepValueTypePattern = null;

	if (geomRepValueTypeRegex != null
		&& rules.contains(RULE_TRF_CLS_CONSTRAINTS_GEOMRESTRICTIONTOGEOMTV_NORESTRICTION_BYVALUETYPE)) {
	    // parse regex
	    try {
		geomRepValueTypePattern = Pattern.compile(geomRepValueTypeRegex);
	    } catch (PatternSyntaxException e) {
		// reported via configuration validator
	    }
	}

	List<String> geomRepTypesIn = config.parameterAsStringList(ConstraintConverter.PARAM_GEOM_REP_TYPES, null, true,
		true, ";");

	Map<String, String> geomRepTypes = new HashMap<String, String>();
	boolean foundInvalidGeomRepTypeValue = false;

	if (geomRepTypesIn != null && !geomRepTypesIn.isEmpty()) {

	    for (String s : geomRepTypesIn) {

		String[] map = s.split("=");

		if (map.length != 2 || map[0].trim().isEmpty() || map[1].trim().isEmpty()) {

		    foundInvalidGeomRepTypeValue = true;
		    break;
		    // details are reported via configuration validator

		} else {

		    geomRepTypes.put(map[0].trim(), map[1].trim());
		}
	    }
	}

	if (foundInvalidGeomRepTypeValue || geomRepConstrPattern == null || geomRepTypes.size() == 0) {

	    result.addError(this, 100);

	} else {

	    for (GenericClassInfo genCi : genModel.selectedSchemaClasses()) {

		if (genCi.category() == Options.ENUMERATION || genCi.category() == Options.CODELIST) {
		    continue;
		}

		SortedSet<String> geometryTVValues = new TreeSet<String>();

		int countGeomRepConstrFound = 0;

		for (Constraint con : genCi.constraints()) {

		    Matcher matcherOnConstraintName = geomRepConstrPattern.matcher(con.name());

		    if (matcherOnConstraintName.matches()) {

			countGeomRepConstrFound++;

			if (countGeomRepConstrFound == 1) {

			    for (Entry<String, String> geomRepType : geomRepTypes.entrySet()) {

				String geomTypeName = geomRepType.getKey();
				String geomTypeAbbrev = geomRepType.getValue();

				if ((isInclusion && con.text().contains(geomTypeName))
					|| (!isInclusion && !con.text().contains(geomTypeName))) {

				    geometryTVValues.add(geomTypeAbbrev);
				}
			    }
			}
		    }
		}

		if (countGeomRepConstrFound > 1) {

		    MessageContext mc = result.addError(this, 101, "" + countGeomRepConstrFound, genCi.name());
		    if (mc != null) {
			mc.addDetail(this, 1, genCi.fullName());
		    }

		} else if (countGeomRepConstrFound == 0 && geomRepValueTypePattern != null) {

		    for (PropertyInfo pi : genCi.propertiesAll()) {

			Matcher matcherOnPiTypeName = geomRepValueTypePattern.matcher(pi.typeInfo().name);

			if (matcherOnPiTypeName.matches()) {

			    for (String geomTypeAbbrev : geomRepTypes.values()) {

				geometryTVValues.add(geomTypeAbbrev);
			    }

			    break;
			}
		    }
		}

		if (geometryTVValues.size() > 0) {

		    String join = commaJoiner.join(geometryTVValues);

		    String geometryTV = genCi.taggedValue("geometry");
		    if (StringUtils.isNotBlank(geometryTV)) {
			MessageContext mc = result.addWarning(this, 102, genCi.name(), geometryTV, join);
			if (mc != null) {
			    mc.addDetail(this, 1, genCi.fullName());
			}
		    }

		    genCi.setTaggedValue("geometry", join, false);
		}
	    }
	}

    }
    
    @Override
    public String message(int mnr) {

	switch (mnr) {
	case 0:
	    return "Context: property '$1$'.";
	case 1:
	    return "Context: class '$1$'.";
	case 2:
	    return "Context: association class '$1$'.";
	case 3:
	    return "Context: association between class '$1$' (with property '$2$') and class '$3$' (with property '$4$')";

	// Messages for RULE_
	case 100:
	    return "Invalid value(s) for configuration parameters '" + PARAM_GEOM_REP_CONSTRAINT_REGEX + "' and/or '"
		    + PARAM_GEOM_REP_TYPES + "'. For further details, check the configuration validator log messages.";
	case 101:
	    return "Found multiple ($1$) constraints to restrict geometry representation on type '$2$'. An arbitrary constraint is chosen.";
	case 102:
	    return "Overwriting tagged value 'geometry' in type '$1$'. Old value: '$2$'. New value: '$3$'.";

	// Messages for RULE_TRF_CLS_CONSTRAINTS_CODELIST_RESTRICTION_TO_TV
	case 200:
	    return "Presence of code list restriction could not be determined for type restriction constraint '$1$' of class '$2$'. Property '$3$' identified by the type restriction was not found for the class. The constraint will be ignored.";
	case 201:
	    return "Presence of code list restriction could not be determined for type restriction constraint '$1$' of class '$2$'. Type '$3$' identified by the type restriction was not found in the model. The constraint will be ignored.";
	case 202:
	    return "Code list restriction identified for type restriction constraint '$1$' of class '$2$'. Property '$3$' is restricted to code list '$4$'.";

	// Messages for RULE_TRF_CLS_CONSTRAINTS_VALUETYPERESTRICTIONTOPROPTV_EXCL
	case 300:
	    return "Invalid value(s) for configuration parameters '" + PARAM_VALUETYPE_REP_CONSTRAINT_REGEX
		    + "' and/or '" + PARAM_VALUETYPE_REP_TYPES
		    + "'. For further details, check the configuration validator log messages.";
	case 301:
	    return "Overwriting tagged value '" + VALUE_TYPE_OPTIONS_TV_NAME
		    + "' in type '$1$'. Old value: '$2$'. New value: '$3$'.";

	default:
	    return "(" + this.getClass().getName() + ") Unknown message with number: " + mnr;
	}
    }
}
