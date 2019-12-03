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
 * (c) 2002-2015 interactive instruments GmbH, Bonn, Germany
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
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;

import de.interactive_instruments.ShapeChange.Options;

public abstract class TaggedValuesImpl implements TaggedValues {

	protected Options options = null;

	@Override
	public Options options() {
		return options;
	}

	public String toString() {

		String res = "";

		for (String tag : keySet()) {

			String[] values = get(tag);

			res += "(" + tag;

			if (values.length > 0) {
				res += ":" + StringUtils.join(values, ",");
			}

			res += ")";
		}
		return res;
	}

	@Override
	public void putAll(TaggedValues other) {

		if (other != null) {

			SortedMap<String,List<String>> tvmap = other.asMap();
			
			for(String tag : tvmap.keySet()) {
				this.put(tag, tvmap.get(tag));
			}
		}
	}
}
