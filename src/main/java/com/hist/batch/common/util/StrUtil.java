package com.hist.batch.common.util;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class StrUtil {
	public static String toStr(Object source) {
		return (source == null || JSONObject.NULL.equals(source)) ? "" : String.valueOf(source);
	}

	public static String truncateWithEllipsis(String input, int maxBytes) {
		if (input == null || input.getBytes(StandardCharsets.UTF_8).length <= maxBytes) {
			return input;
		}

		byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
		int limit = maxBytes - 3;
		int endIndex = new String(bytes, 0, limit, StandardCharsets.UTF_8).length();
		return input.substring(0, endIndex) + "...";
	}
}
