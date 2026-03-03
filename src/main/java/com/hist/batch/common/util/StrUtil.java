package com.hist.batch.common.util;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class StrUtil {
	public static String toStr(Object source) {
		return (source == null || JSONObject.NULL.equals(source)) ? "" : String.valueOf(source);
	}

	public static String truncateWithEllipsis(String input, int maxLength) {
		if (input == null || input.length() <= maxLength) {
			return input;
		}

		int limit = maxLength - 3;
		return input.substring(0, limit) + "...";
	}

	public static String truncateByBytesWithEllipsis(String input, int maxBytes) {
		if (input == null || input.getBytes(StandardCharsets.UTF_8).length <= maxBytes) {
			return input;
		}

		byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
		int limit = maxBytes - 3;
		int endIndex = new String(bytes, 0, limit, StandardCharsets.UTF_8).length();
		return input.substring(0, endIndex) + "...";
	}

	public static String truncateForJsonWithEllipsis(String input, int maxChars) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		StringBuilder sb = new StringBuilder();
		int estimatedJsonLength = 0;
		int limit = maxChars - 3;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			// JSON 직렬화 시 2글자로 늘어나는 이스케이프 문자들 처리 (\n, \r, \t, \\, \")
			if (c == '\n' || c == '\r' || c == '\t' || c == '\\' || c == '"') {
				estimatedJsonLength += 2;
			} else {
				estimatedJsonLength += 1;
			}

			if (estimatedJsonLength > limit) {
				return sb.toString() + "...";
			}
			sb.append(c);
		}

		return sb.toString();
	}
}
