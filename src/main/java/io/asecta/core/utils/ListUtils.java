package io.asecta.core.utils;

import java.util.List;

public class ListUtils {

	public static String toCSV(List<?> list) {
		if (list == null) {
			return null;
		}

		if (list.isEmpty()) {
			return "";
		}

		if (list.size() == 0) {
			return list.get(0).toString();
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Object o : list) {
			stringBuilder.append(o.toString()).append(", ");
		}

		return stringBuilder.substring(0, stringBuilder.length() - 2);
	}
}
