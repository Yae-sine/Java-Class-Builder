package util;

import exception.InvalidNameException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class for validating input for class elements
 */
public class ValidationUtils {
    // Java keywords that cannot be used as identifiers
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
            "true", "false", "null"
    ));

    // Valid Java visibility modifiers
    private static final Set<String> VALID_VISIBILITY = new HashSet<>(Arrays.asList(
            "public", "private", "protected", ""
    ));

    // Pattern for valid Java identifiers
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*");

    /**
     * Validates a Java class name
     *
     * @param name The class name to validate
     * @throws InvalidNameException If the name is invalid
     */
    public static void validateClassName(String name) throws InvalidNameException {
        if (name == null || name.isEmpty()) {
            throw new InvalidNameException("Class name cannot be empty");
        }

        if (!IDENTIFIER_PATTERN.matcher(name).matches()) {
            throw new InvalidNameException("Invalid class name format: " + name);
        }

        if (JAVA_KEYWORDS.contains(name)) {
            throw new InvalidNameException("Class name cannot be a Java keyword: " + name);
        }

        // Class names should start with an uppercase letter (convention)
        if (!Character.isUpperCase(name.charAt(0))) {
            throw new InvalidNameException("Class name should start with an uppercase letter: " + name);
        }
    }

    /**
     * Validates a Java variable name
     *
     * @param name The variable name to validate
     * @throws InvalidNameException If the name is invalid
     */
    public static void validateVariableName(String name) throws InvalidNameException {
        if (name == null || name.isEmpty()) {
            throw new InvalidNameException("Variable name cannot be empty");
        }

        if (!IDENTIFIER_PATTERN.matcher(name).matches()) {
            throw new InvalidNameException("Invalid variable name format: " + name);
        }

        if (JAVA_KEYWORDS.contains(name)) {
            throw new InvalidNameException("Variable name cannot be a Java keyword: " + name);
        }

        // Variable names should start with a lowercase letter (convention)
        if (!Character.isLowerCase(name.charAt(0)) && name.charAt(0) != '_') {
            throw new InvalidNameException("Variable name should start with a lowercase letter or underscore: " + name);
        }
    }

    /**
     * Validates a Java method name
     *
     * @param name The method name to validate
     * @throws InvalidNameException If the name is invalid
     */
    public static void validateMethodName(String name) throws InvalidNameException {
        if (name == null || name.isEmpty()) {
            throw new InvalidNameException("Method name cannot be empty");
        }

        if (!IDENTIFIER_PATTERN.matcher(name).matches()) {
            throw new InvalidNameException("Invalid method name format: " + name);
        }

        if (JAVA_KEYWORDS.contains(name)) {
            throw new InvalidNameException("Method name cannot be a Java keyword: " + name);
        }

        // Method names should start with a lowercase letter (convention)
        if (!Character.isLowerCase(name.charAt(0))) {
            throw new InvalidNameException("Method name should start with a lowercase letter: " + name);
        }
    }

    /**
     * Validates a Java visibility modifier
     *
     * @param visibility The visibility to validate
     * @throws InvalidNameException If the visibility is invalid
     */
    public static void validateVisibility(String visibility) throws InvalidNameException {
        if (!VALID_VISIBILITY.contains(visibility)) {
            throw new InvalidNameException("Invalid visibility modifier: " + visibility +
                    ". Must be 'public', 'private', 'protected', or empty for package-private");
        }
    }

    /**
     * Validates a Java type
     *
     * @param type The type to validate
     * @throws InvalidNameException If the type is invalid
     */
    public static void validateType(String type) throws InvalidNameException {
        if (type == null || type.isEmpty()) {
            throw new InvalidNameException("Type cannot be empty");
        }

        // Handle array types
        String baseType = type.replaceAll("\\[\\]", "").trim();

        // Primitive types are valid
        if (isPrimitiveType(baseType)) {
            return;
        }

        // For non-primitive types, check if it's a valid class name
        // This is a simplified check
        if (!IDENTIFIER_PATTERN.matcher(baseType).matches() || JAVA_KEYWORDS.contains(baseType)) {
            throw new InvalidNameException("Invalid type: " + type);
        }
    }

    /**
     * Checks if a type is a Java primitive type
     *
     * @param type The type to check
     * @return true if the type is a primitive type, false otherwise
     */
    public static boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("long") || type.equals("short") || type.equals("byte") ||
                type.equals("float") || type.equals("double") || type.equals("boolean") || type.equals("char") ||
                type.equals("void");
    }

    /**
     * Validates a package name
     *
     * @param packageName The package name to validate
     * @throws InvalidNameException If the package name is invalid
     */
    public static void validatePackageName(String packageName) throws InvalidNameException {
        if (packageName == null || packageName.isEmpty()) {
            return; // Empty package name is valid (default package)
        }

        String[] parts = packageName.split("\\.");
        for (String part : parts) {
            if (!IDENTIFIER_PATTERN.matcher(part).matches() || JAVA_KEYWORDS.contains(part)) {
                throw new InvalidNameException("Invalid package name part: " + part);
            }
        }

        // Package names should be lowercase (convention)
        for (int i = 0; i < packageName.length(); i++) {
            if (Character.isUpperCase(packageName.charAt(i))) {
                throw new InvalidNameException("Package names should be lowercase: " + packageName);
            }
        }
    }
}