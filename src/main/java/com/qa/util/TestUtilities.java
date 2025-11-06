package com.qa.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class for extracting values from JSON responses using simplified
 * JPath-style strings.
 */
public class TestUtilities {

	/**
	 * Returns the value of a JSON object property addressed by simple JPath (e.g.
	 * "data[0]/id").
	 * 
	 * @param responsejson The JSON object from which to extract the value.
	 * @param jpath        The "path" string in format "foo/bar[0]/baz".
	 * @return The value as a string.
	 * @throws JSONException, NumberFormatException
	 */
	public static String getValueByJPath(JSONObject responsejson, String jpath)
			throws NumberFormatException, JSONException {
		Object obj = responsejson;
		for (String s : jpath.split("/")) { // <-- NO semicolon; loop body starts here
			if (!s.isEmpty()) {
				// If jpath token does NOT have array brackets
				if (!(s.contains("[") || s.contains("]"))) {
					obj = ((JSONObject) obj).get(s);
				} else {
					// Handle array notation, e.g. "data[0]"
					String key = s.split("\\[")[0];
					int index = Integer.parseInt(s.split("\\[")[1].replace("]", ""));
					obj = ((JSONArray) ((JSONObject) obj).get(key)).get(index);
				}
			}
		}
		return obj.toString();
	}
}
