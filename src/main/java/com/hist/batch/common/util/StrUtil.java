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

		return input.substring(0, (maxLength-3)) + "...";
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

	// JSON 직렬화 시 2글자로 늘어나는 이스케이프 문자들 처리 (\n, \r, \t, \\, \")
	private static boolean needsEscape(char c) {
		return c == '"' || c == '\\' || c == '\n' || c == '\r' || c == '\t';
	}

	public static int getJsonLength(String input) {
		if (input == null) {
			return 0;
		}

		int length = 0;
		for (char c : input.toCharArray()) {
			length += needsEscape(c) ? 2 : 1;
		}

		return length;
	}

	public static String truncateForJsonWithEllipsis(String input, int maxChars) {
		if (input == null || maxChars <= 0) return input == null ? null : "";

		int len = 0, safeIdx = 0, strictIdx = 0;
		char[] chars = input.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			int charLen = needsEscape(chars[i]) ? 2 : 1;

			if (len + charLen > maxChars) {
				return maxChars <= 3 ? new String(chars, 0, strictIdx) : new String(chars, 0, safeIdx) + "...";
			}

			len += charLen;
			strictIdx = i + 1;

			if (len <= maxChars - 3) {
				safeIdx = i + 1;
			}
		}

		return input;
	}
}
