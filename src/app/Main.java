package app;

import builder.JavaClassBuilder;
import exception.DuplicateElementException;
import exception.InvalidNameException;
import model.Constructor;
import model.Field;
import model.Method;
import model.Parameter;
import util.FileManager;
import util.ValidationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the application
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static JavaClassBuilder currentBuilder = null;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    
    public static void main(String[] args) {
        System.out.println(ANSI_CYAN + "Welcome to ClassBuilder!" + ANSI_RESET);
        System.out.println("Create Java classes dynamically and save them to file.");
        
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            
            try {
                processChoice(choice);
            } catch (Exception e) {
                System.err.println(ANSI_RED + "Error: " + e.getMessage() + ANSI_RESET);
            }
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n" + ANSI_CYAN + "=== ClassBuilder Menu ===" + ANSI_RESET);
        System.out.println("1. Create new class");
        System.out.println("2. Add attribute");
        System.out.println("3. Add method");
        System.out.println("4. Add constructor");
        System.out.println("5. Add interface implementation");
        System.out.println("6. Display class preview");
        System.out.println("7. Save class to file");
        System.out.println("8. Generate getters/setters");
        System.out.println("9. Generate toString method");
        System.out.println("10. Generate equals/hashCode methods");
        System.out.println("11. Exit");
        
        // Display current class if one exists
        if (currentBuilder != null) {
            System.out.println(ANSI_YELLOW + "\nCurrent class: " + currentBuilder.getClassName() + ANSI_RESET);
        }
        
        System.out.print("\nEnter your choice: ");
    }
    
    private static int getUserChoice() {
        int choice = -1;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            // Invalid input
        }
        return choice;
    }
    
    private static void processChoice(int choice) throws Exception {
        switch (choice) {
            case 1:
                createNewClass();
                break;
            case 2:
                addAttribute();
                break;
            case 3:
                addMethod();
                break;
            case 4:
                addConstructor();
                break;
            case 5:
                addInterfaceImplementation();
                break;
            case 6:
                displayClassPreview();
                break;
            case 7:
                saveClassToFile();
                break;
            case 8:
                generateGettersSetters();
                break;
            case 9:
                generateToString();
                break;
            case 10:
                generateEqualsHashCode();
                break;
            case 11:
                System.out.println(ANSI_GREEN + "Thank you for using ClassBuilder!" + ANSI_RESET);
                System.exit(0);
                break;
            default:
                System.out.println(ANSI_RED + "Invalid choice. Please try again." + ANSI_RESET);
        }
    }
    
    private static void createNewClass() throws InvalidNameException {
        System.out.print("Enter class name: ");
        String className = scanner.nextLine().trim();
        
        ValidationUtils.validateClassName(className);
        
        currentBuilder = new JavaClassBuilder(className);
        
        System.out.print("Enter package name (leave empty for default): ");
        String packageName = scanner.nextLine().trim();
        if (!packageName.isEmpty()) {
            ValidationUtils.validatePackageName(packageName);
            currentBuilder.setPackageName(packageName);
        }
        
        System.out.print("Is the class abstract? (y/n): ");
        String abstractChoice = scanner.nextLine().trim().toLowerCase();
        if (abstractChoice.equals("y") || abstractChoice.equals("yes")) {
            currentBuilder.setAbstract(true);
        }
        
        System.out.print("Is it an interface? (y/n): ");
        String interfaceChoice = scanner.nextLine().trim().toLowerCase();
        if (interfaceChoice.equals("y") || interfaceChoice.equals("yes")) {
            currentBuilder.setInterface(true);
        }
        
        System.out.print("Should this class extend another class? (y/n): ");
        String shouldExtend = scanner.nextLine().trim().toLowerCase();
        if (shouldExtend.equals("y") || shouldExtend.equals("yes")) {
            System.out.print("Enter parent class name: ");
            String parentClassName = scanner.nextLine().trim();
            ValidationUtils.validateClassName(parentClassName);
            
            // Check if the parent class exists in the current directory
            if (classExists(parentClassName)) {
                currentBuilder.setExtendsClass(parentClassName);
                System.out.println(ANSI_GREEN + "Class will extend " + parentClassName + ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW + "Warning: Class " + parentClassName + 
                    " was not found. The extends relationship has been set, but may cause compilation errors." + ANSI_RESET);
                
                System.out.print("Do you still want to extend this class? (y/n): ");
                String confirmExtend = scanner.nextLine().trim().toLowerCase();
                if (confirmExtend.equals("y") || confirmExtend.equals("yes")) {
                    currentBuilder.setExtendsClass(parentClassName);
                } else {
                    System.out.println(ANSI_BLUE + "Inheritance canceled." + ANSI_RESET);
                }
            }
        }
        
        System.out.println(ANSI_GREEN + "Class " + className + " created successfully." + ANSI_RESET);
    }
    
    /**
     * Check if a class exists in the current system
     * @param className The name of the class to check
     * @return true if the class exists, false otherwise
     */
    private static boolean classExists(String className) {
        // First check if it's a core Java class
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e1) {
            // Not a core class, check if it's in the output directory
            try {
                Class.forName("output." + className);
                return true;
            } catch (ClassNotFoundException e2) {
                // Not found in output directory, check if there's a .java file for it
                File file = new File("output/" + className + ".java");
                if (file.exists()) {
                    return true;
                }
                
                // Finally check if we've already created it in this session
                for (String createdClassName : getCreatedClassNames()) {
                    if (createdClassName.equals(className)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Get a list of class names created in the current session
     * @return A list of created class names
     */
    private static List<String> getCreatedClassNames() {
        List<String> result = new ArrayList<>();
        File outputDir = new File("output");
        if (outputDir.exists() && outputDir.isDirectory()) {
            File[] files = outputDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    result.add(fileName.substring(0, fileName.length() - 5)); // Remove .java extension
                }
            }
        }
        return result;
    }
    
    private static void addAttribute() throws InvalidNameException, DuplicateElementException {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        if (currentBuilder.isInterface()) {
            System.out.println(ANSI_YELLOW + "Note: Interfaces can only have constant fields (public static final)." + ANSI_RESET);
        }
        
        System.out.print("Enter attribute visibility (public, private, protected, or leave empty for package-private): ");
        String visibility = scanner.nextLine().trim();
        ValidationUtils.validateVisibility(visibility);
        
        System.out.print("Enter attribute type: ");
        String type = scanner.nextLine().trim();
        ValidationUtils.validateType(type);
        
        System.out.print("Enter attribute name: ");
        String name = scanner.nextLine().trim();
        ValidationUtils.validateVariableName(name);
        
        // Check for duplicate field
        if (currentBuilder.getFieldByName(name) != null) {
            throw new DuplicateElementException("A field with name '" + name + "' already exists");
        }
        
        Field field = new Field(name, type, visibility);
        
        System.out.print("Is the attribute final? (y/n): ");
        String finalChoice = scanner.nextLine().trim().toLowerCase();
        if (finalChoice.equals("y") || finalChoice.equals("yes")) {
            field.setFinal(true);
        }
        
        System.out.print("Is the attribute static? (y/n): ");
        String staticChoice = scanner.nextLine().trim().toLowerCase();
        if (staticChoice.equals("y") || staticChoice.equals("yes")) {
            field.setStatic(true);
        }
        
        // For interfaces, fields must be public static final
        if (currentBuilder.isInterface()) {
            field.setVisibility("public");
            field.setStatic(true);
            field.setFinal(true);
        }
        
        System.out.print("Initial value (leave empty for none): ");
        String initialValue = scanner.nextLine().trim();
        if (!initialValue.isEmpty()) {
            field.setInitialValue(initialValue);
        }
        
        currentBuilder.addField(field);
        System.out.println(ANSI_GREEN + "Attribute added successfully." + ANSI_RESET);
    }
    
    private static void addMethod() throws InvalidNameException {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        System.out.print("Enter method visibility (public, private, protected, or leave empty for package-private): ");
        String visibility = scanner.nextLine().trim();
        ValidationUtils.validateVisibility(visibility);
        
        // Interface methods are always public and abstract by default
        if (currentBuilder.isInterface()) {
            visibility = "public";
            System.out.println(ANSI_YELLOW + "Note: Interface methods are implicitly public." + ANSI_RESET);
        }
        
        System.out.print("Enter return type (void for no return value): ");
        String returnType = scanner.nextLine().trim();
        ValidationUtils.validateType(returnType);
        
        System.out.print("Enter method name: ");
        String name = scanner.nextLine().trim();
        ValidationUtils.validateMethodName(name);
        
        Method method = new Method(name, returnType, visibility);
        
        if (!currentBuilder.isInterface()) {
            System.out.print("Is the method static? (y/n): ");
            String staticChoice = scanner.nextLine().trim().toLowerCase();
            if (staticChoice.equals("y") || staticChoice.equals("yes")) {
                method.setStatic(true);
            }
            
            System.out.print("Is the method abstract? (y/n): ");
            String abstractChoice = scanner.nextLine().trim().toLowerCase();
            if (abstractChoice.equals("y") || abstractChoice.equals("yes")) {
                method.setAbstract(true);
                
                // Abstract class required for abstract methods
                if (!currentBuilder.isAbstract()) {
                    System.out.println(ANSI_YELLOW + "Class with abstract methods must be declared abstract." + ANSI_RESET);
                    currentBuilder.setAbstract(true);
                }
            }
        } else {
            // Interface methods are implicitly abstract unless static or default
            System.out.print("Is the method static? (y/n): ");
            String staticChoice = scanner.nextLine().trim().toLowerCase();
            if (staticChoice.equals("y") || staticChoice.equals("yes")) {
                method.setStatic(true);
            } else {
                method.setAbstract(true);
            }
        }
        
        // Add parameters
        System.out.println("Add parameters (enter blank line when done):");
        int paramCount = 1;
        while (true) {
            System.out.print("Parameter " + paramCount + " type (or enter to finish): ");
            String paramType = scanner.nextLine().trim();
            if (paramType.isEmpty()) {
                break;
            }
            
            ValidationUtils.validateType(paramType);
            
            System.out.print("Parameter " + paramCount + " name: ");
            String paramName = scanner.nextLine().trim();
            ValidationUtils.validateVariableName(paramName);
            
            Parameter parameter = new Parameter(paramName, paramType);
            method.addParameter(parameter);
            paramCount++;
        }
        
        // Add method body if not abstract
        if (!method.isAbstract()) {
            System.out.println("Enter method body (enter '###' on a new line when finished):");
            StringBuilder bodyBuilder = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equals("###")) {
                bodyBuilder.append(line).append("\n");
            }
            
            if (bodyBuilder.length() > 0) {
                // Remove the last newline
                bodyBuilder.deleteCharAt(bodyBuilder.length() - 1);
                method.setBody(bodyBuilder.toString());
            }
        }
        
        currentBuilder.addMethod(method);
        System.out.println(ANSI_GREEN + "Method added successfully." + ANSI_RESET);
    }
    
    private static void addConstructor() throws InvalidNameException {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        if (currentBuilder.isInterface()) {
            System.out.println(ANSI_RED + "Interfaces cannot have constructors!" + ANSI_RESET);
            return;
        }
        
        System.out.print("Enter constructor visibility (public, private, protected, or leave empty for package-private): ");
        String visibility = scanner.nextLine().trim();
        ValidationUtils.validateVisibility(visibility);
        
        // Constructor name is the same as class name
        Constructor constructor = new Constructor(currentBuilder.getClassName(), visibility);
        
        // Add parameters
        System.out.println("Add parameters (enter blank line when done):");
        int paramCount = 1;
        while (true) {
            System.out.print("Parameter " + paramCount + " type (or enter to finish): ");
            String paramType = scanner.nextLine().trim();
            if (paramType.isEmpty()) {
                break;
            }
            
            ValidationUtils.validateType(paramType);
            
            System.out.print("Parameter " + paramCount + " name: ");
            String paramName = scanner.nextLine().trim();
            ValidationUtils.validateVariableName(paramName);
            
            Parameter parameter = new Parameter(paramName, paramType);
            constructor.addParameter(parameter);
            paramCount++;
        }
        
        // Add constructor body
        System.out.println("Enter constructor body (enter '###' on a new line when finished):");
        StringBuilder bodyBuilder = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("###")) {
            bodyBuilder.append(line).append("\n");
        }
        
        if (bodyBuilder.length() > 0) {
            // Remove the last newline
            bodyBuilder.deleteCharAt(bodyBuilder.length() - 1);
            constructor.setBody(bodyBuilder.toString());
        }
        
        currentBuilder.addConstructor(constructor);
        System.out.println(ANSI_GREEN + "Constructor added successfully." + ANSI_RESET);
    }
    
    private static void addInterfaceImplementation() throws InvalidNameException {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        if (currentBuilder.isInterface()) {
            System.out.println(ANSI_RED + "An interface cannot implement other interfaces (use extends instead)." + ANSI_RESET);
            return;
        }
        
        System.out.print("Enter interface name to implement: ");
        String interfaceName = scanner.nextLine().trim();
        
        ValidationUtils.validateClassName(interfaceName);
        currentBuilder.addImplementsInterface(interfaceName);
        
        System.out.println(ANSI_GREEN + "Interface implementation added successfully." + ANSI_RESET);
    }
    
    private static void displayClassPreview() {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        String javaCode = currentBuilder.buildClass();
        System.out.println("\n" + ANSI_CYAN + "===== Class Preview =====" + ANSI_RESET);
        System.out.println(javaCode);
        System.out.println(ANSI_CYAN + "=========================" + ANSI_RESET);
    }
    
    private static void saveClassToFile() {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        String javaCode = currentBuilder.buildClass();
        boolean success = FileManager.writeToFile(currentBuilder.getClassName(), javaCode);
        
        if (success) {
            System.out.println(ANSI_GREEN + "Class saved successfully!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "Failed to save class!" + ANSI_RESET);
        }
    }
    
    private static void generateGettersSetters() {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        if (currentBuilder.isInterface()) {
            System.out.println(ANSI_RED + "Interfaces cannot have getters/setters!" + ANSI_RESET);
            return;
        }
        
        List<Field> fields = currentBuilder.getFields();
        if (fields.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No fields to generate getters/setters for!" + ANSI_RESET);
            return;
        }
        
        System.out.println(ANSI_BLUE + "Select fields to generate getters/setters for (enter blank line when done):" + ANSI_RESET);
        
        List<String> selectedFields = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            System.out.println((i + 1) + ". " + field.getType() + " " + field.getName());
        }
        
        System.out.println("Enter field numbers (comma-separated), 'all' for all fields, or 'exit' to cancel:");
        String selection = scanner.nextLine().trim();
        
        if (selection.isEmpty() || selection.equalsIgnoreCase("exit")) {
            return;
        }
        
        if (selection.equalsIgnoreCase("all")) {
            for (Field field : fields) {
                selectedFields.add(field.getName());
            }
        } else {
            String[] selections = selection.split(",");
            for (String sel : selections) {
                try {
                    int index = Integer.parseInt(sel.trim()) - 1;
                    if (index >= 0 && index < fields.size()) {
                        selectedFields.add(fields.get(index).getName());
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ANSI_RED + "Invalid selection: " + sel + ANSI_RESET);
                }
            }
        }
        
        int gettersGenerated = 0;
        int settersGenerated = 0;
        
        for (String fieldName : selectedFields) {
            Field field = currentBuilder.getFieldByName(fieldName);
            if (field != null) {
                // Generate getter
                String getterPrefix = field.getType().equals("boolean") ? "is" : "get";
                String capitalizedName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                String getterName = getterPrefix + capitalizedName;
                
                try {
                    // Check if getter already exists
                    if (currentBuilder.getMethodByName(getterName) == null) {
                        Method getter = new Method(getterName, field.getType(), "public");
                        getter.setBody("return " + fieldName + ";");
                        currentBuilder.addMethod(getter);
                        gettersGenerated++;
                    }
                    
                    // Generate setter (only for non-final fields)
                    if (!field.isFinal()) {
                        String setterName = "set" + capitalizedName;
                        
                        // Check if setter already exists
                        if (currentBuilder.getMethodByName(setterName) == null) {
                            Method setter = new Method(setterName, "void", "public");
                            setter.addParameter(new Parameter(fieldName, field.getType()));
                            setter.setBody("this." + fieldName + " = " + fieldName + ";");
                            currentBuilder.addMethod(setter);
                            settersGenerated++;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(ANSI_RED + "Error generating for field " + fieldName + ": " + e.getMessage() + ANSI_RESET);
                }
            }
        }
        
        System.out.println(ANSI_GREEN + "Generated " + gettersGenerated + " getters and " + settersGenerated + " setters." + ANSI_RESET);
    }
    
    private static void generateToString() {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        if (currentBuilder.isInterface()) {
            System.out.println(ANSI_RED + "Interfaces cannot have toString methods!" + ANSI_RESET);
            return;
        }
        
        List<Field> fields = currentBuilder.getFields();
        if (fields.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No fields to include in toString!" + ANSI_RESET);
            return;
        }
        
        try {
            // Check if toString already exists
            if (currentBuilder.getMethodByName("toString") != null) {
                System.out.print(ANSI_YELLOW + "A toString method already exists. Overwrite? (y/n): " + ANSI_RESET);
                String choice = scanner.nextLine().trim().toLowerCase();
                if (!choice.equals("y") && !choice.equals("yes")) {
                    return;
                }
                
                // Remove existing toString method
                Method existingToString = currentBuilder.getMethodByName("toString");
                currentBuilder.removeMethod(existingToString);
            }
            
            // Create toString method
            Method toString = new Method("toString", "String", "public");
            
            // Add @Override annotation
            StringBuilder body = new StringBuilder();
            body.append("return \"").append(currentBuilder.getClassName()).append("{\" +\n");
            
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (i > 0) {
                    body.append("    \", ");
                } else {
                    body.append("    \"");
                }
                
                body.append(field.getName()).append("=\" + ").append(field.getName());
                
                if (i == fields.size() - 1) {
                    body.append(" +\n    \"}\";\n");
                } else {
                    body.append(" +\n");
                }
            }
            
            toString.setBody(body.toString());
            currentBuilder.addMethod(toString);
            
            System.out.println(ANSI_GREEN + "toString method generated successfully." + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error generating toString method: " + e.getMessage() + ANSI_RESET);
        }
    }
    
    private static void generateEqualsHashCode() {
        if (currentBuilder == null) {
            System.out.println(ANSI_RED + "You need to create a class first!" + ANSI_RESET);
            return;
        }
        
        if (currentBuilder.isInterface()) {
            System.out.println(ANSI_RED + "Interfaces cannot have equals/hashCode methods!" + ANSI_RESET);
            return;
        }
        
        List<Field> fields = currentBuilder.getFields();
        if (fields.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No fields to include in equals/hashCode!" + ANSI_RESET);
            return;
        }
        
        try {
            // Check if methods already exist
            boolean hasEquals = currentBuilder.getMethodByName("equals") != null;
            boolean hasHashCode = currentBuilder.getMethodByName("hashCode") != null;
            
            if (hasEquals || hasHashCode) {
                System.out.print(ANSI_YELLOW + "Equals and/or hashCode methods already exist. Overwrite? (y/n): " + ANSI_RESET);
                String choice = scanner.nextLine().trim().toLowerCase();
                if (!choice.equals("y") && !choice.equals("yes")) {
                    return;
                }
                
                // Remove existing methods
                if (hasEquals) {
                    Method existingEquals = currentBuilder.getMethodByName("equals");
                    currentBuilder.removeMethod(existingEquals);
                }
                
                if (hasHashCode) {
                    Method existingHashCode = currentBuilder.getMethodByName("hashCode");
                    currentBuilder.removeMethod(existingHashCode);
                }
            }
            
            // Generate equals method
            Method equals = new Method("equals", "boolean", "public");
            Parameter objParam = new Parameter("obj", "Object");
            equals.addParameter(objParam);
            
            StringBuilder equalsBody = new StringBuilder();
            equalsBody.append("if (this == obj) return true;\n");
            equalsBody.append("if (obj == null || getClass() != obj.getClass()) return false;\n\n");
            equalsBody.append(currentBuilder.getClassName()).append(" other = (").append(currentBuilder.getClassName()).append(") obj;\n\n");
            
            for (Field field : fields) {
                String fieldName = field.getName();
                String fieldType = field.getType();
                
                if (ValidationUtils.isPrimitiveType(fieldType) && !fieldType.equals("boolean") && !fieldType.equals("float") && !fieldType.equals("double")) {
                    equalsBody.append("if (").append(fieldName).append(" != other.").append(fieldName).append(") return false;\n");
                } else if (fieldType.equals("float")) {
                    equalsBody.append("if (Float.compare(other.").append(fieldName).append(", ").append(fieldName).append(") != 0) return false;\n");
                } else if (fieldType.equals("double")) {
                    equalsBody.append("if (Double.compare(other.").append(fieldName).append(", ").append(fieldName).append(") != 0) return false;\n");
                } else if (fieldType.equals("boolean")) {
                    equalsBody.append("if (").append(fieldName).append(" != other.").append(fieldName).append(") return false;\n");
                } else {
                    equalsBody.append("if (").append(fieldName).append(" != null ? !").append(fieldName).append(".equals(other.")
                            .append(fieldName).append(") : other.").append(fieldName).append(" != null) return false;\n");
                }
            }
            
            equalsBody.append("\nreturn true;");
            equals.setBody(equalsBody.toString());
            
            // Generate hashCode method
            Method hashCode = new Method("hashCode", "int", "public");
            
            StringBuilder hashCodeBody = new StringBuilder();
            hashCodeBody.append("int result = 17;\n");
            
            for (Field field : fields) {
                String fieldName = field.getName();
                String fieldType = field.getType();
                
                if (fieldType.equals("boolean")) {
                    hashCodeBody.append("result = 31 * result + (").append(fieldName).append(" ? 1 : 0);\n");
                } else if (fieldType.equals("byte") || fieldType.equals("short") || fieldType.equals("char") || fieldType.equals("int")) {
                    hashCodeBody.append("result = 31 * result + ").append(fieldName).append(";\n");
                } else if (fieldType.equals("long")) {
                    hashCodeBody.append("result = 31 * result + (int) (").append(fieldName).append(" ^ (").append(fieldName).append(" >>> 32));\n");
                } else if (fieldType.equals("float")) {
                    hashCodeBody.append("result = 31 * result + Float.floatToIntBits(").append(fieldName).append(");\n");
                } else if (fieldType.equals("double")) {
                    hashCodeBody.append("long temp = Double.doubleToLongBits(").append(fieldName).append(");\n");
                    hashCodeBody.append("result = 31 * result + (int) (temp ^ (temp >>> 32));\n");
                } else {
                    hashCodeBody.append("result = 31 * result + (").append(fieldName).append(" != null ? ").append(fieldName).append(".hashCode() : 0);\n");
                }
            }
            
            hashCodeBody.append("return result;");
            hashCode.setBody(hashCodeBody.toString());
            
            // Add methods to class
            currentBuilder.addMethod(equals);
            currentBuilder.addMethod(hashCode);
            
            System.out.println(ANSI_GREEN + "equals and hashCode methods generated successfully." + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error generating equals/hashCode methods: " + e.getMessage() + ANSI_RESET);
        }
    }
} 