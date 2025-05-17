package util;

/**
 * Utility class for formatting strings and code
 */
public class StringFormatter {
    
    /**
     * Indents each line of the given text with the specified indentation
     * @param text The text to indent
     * @param indentLevel The number of indentation levels
     * @return The indented text
     */
    public static String indent(String text, int indentLevel) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String indentation = "    ".repeat(indentLevel);
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < lines.length; i++) {
            sb.append(indentation).append(lines[i]);
            if (i < lines.length - 1) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Convert a string to camelCase
     * @param input The input string
     * @return The camelCase version of the string
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : input.toCharArray()) {
            if (c == ' ' || c == '_' || c == '-') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Convert a string to PascalCase
     * @param input The input string
     * @return The PascalCase version of the string
     */
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String camelCase = toCamelCase(input);
        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }
} 