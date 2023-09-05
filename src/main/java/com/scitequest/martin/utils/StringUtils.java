package com.scitequest.martin.utils;

public final class StringUtils {

    public static String toKebapCase(String input) {
        var sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c) || Character.isDigit(c)) {
                sb.append(c);
            } else if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '-') {
                sb.append('-');
            }
        }
        int lastCharIdx = sb.length() - 1;
        if (lastCharIdx >= 0 && sb.charAt(lastCharIdx) == '-') {
            sb.deleteCharAt(lastCharIdx);
        }
        return sb.toString();
    }
}
