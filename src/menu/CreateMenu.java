package menu;

import builder.JavaClassBuilder;
import exception.InvalidNameException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import model.Field;
import model.Method;
import util.FileManager;
import util.ValidationUtils;

public class CreateMenu  {
    private final Scanner scanner;
    private final String folderPath;
    private JavaClassBuilder classBuilder;
    private boolean isModifyMode = false;
    private Stack<JavaClassBuilder> undoStack = new Stack<>();
    private Stack<JavaClassBuilder> redoStack = new Stack<>();

    public CreateMenu(Scanner scanner, String folderPath) {
        this.scanner = scanner;
        this.folderPath = folderPath;
    }

    public CreateMenu(Scanner scanner, String folderPath, String existingClassName) {
        this.scanner = scanner;
        this.folderPath = folderPath;
        this.isModifyMode = true;
        this.classBuilder = loadClass(existingClassName);
    }

    public void start() {
        if (!isModifyMode) {
            ClassName();
        }
        boolean done = false;
        while (!done) {
            printSubMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    pushUndo();
                    addField();
                    clearRedo();
                    showPreview();
                    break;
                case "2":
                    pushUndo();
                    addMethod();
                    clearRedo();
                    showPreview();
                    break;
                case "3":
                    pushUndo();
                    setModifiers();
                    clearRedo();
                    showPreview();
                    break;
                case "4":
                    saveClass();
                    done = true;
                    break;
                case "5":
                    System.out.println("Operation cancelled.");
                    done = true;
                    break;
                case "6":
                    undo();
                    showPreview();
                    break;
                case "7":
                    redo();
                    showPreview();
                    break;
                case "8":
                    pushUndo();
                    refactorMenu();
                    clearRedo();
                    showPreview();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 8.");
            }
        }
    }

    public void startModify() {
        if (classBuilder == null) {
            System.out.println("Failed to load class for modification.");
            return;
        }
        start();
    }

    private void ClassName() {
        while (true) {
            System.out.print("Enter class/interface name: ");
            String name = scanner.nextLine().trim();
            try {
                ValidationUtils.validateClassName(name);
                // Check if class file already exists
                File file = new File(folderPath, name + ".java");
                if (file.exists()) {
                    System.out.println("Error: A class/interface with this name already exists in the selected folder.");
                    continue;
                }
                this.classBuilder = new JavaClassBuilder(name);
                break;
            } catch (InvalidNameException e) {
                System.out.println("Invalid class name: " + e.getMessage());
                System.out.println("Suggestion: Class names should start with an uppercase letter, not be a Java keyword, and use only letters, digits, _ or $.");
            }
        }
    }
        private void printSubMenu() {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║           Features Menu             ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Add field                         ║");
            System.out.println("║ 2. Add method                        ║");
            System.out.println("║ 3. Set class/interface modifiers     ║");
            System.out.println("║ 4. Save class/interface              ║");
            System.out.println("║ 5. Cancel                            ║");
            System.out.println("║ 6. Undo                              ║");
            System.out.println("║ 7. Redo                              ║");
            System.out.println("║ 8. Refactor (rename/type change)     ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Enter your choice: ");
        }

    private void addField() {
        try {
            System.out.print("Field name: ");
            String name = scanner.nextLine().trim();
            try {
                ValidationUtils.validateVariableName(name);
            } catch (InvalidNameException e) {
                System.out.println("Validation error: " + e.getMessage());
                System.out.println("Suggestion: Field names should start with a lowercase letter, not be a Java keyword, and use only letters, digits, _ or $.");
                return;
            }

            // Check for duplicate field name
            if (classBuilder.getFieldByName(name) != null) {
                System.out.println("Error: A field with this name already exists in the class.");
                return;
            }

            System.out.print("Field type: ");
            String type = scanner.nextLine().trim();
            try {
                ValidationUtils.validateType(type);
            } catch (InvalidNameException e) {
                System.out.println("Validation error: " + e.getMessage());
                System.out.println("Suggestion: Use a valid Java type (e.g., int, String, double, or a valid class name). For arrays, use type[]");
                return;
            }

            System.out.print("Visibility (public/private/protected or empty for package-private): ");
            String visibility = scanner.nextLine().trim();
            try {
                ValidationUtils.validateVisibility(visibility);
            } catch (InvalidNameException e) {
                System.out.println("Validation error: " + e.getMessage());
                System.out.println("Suggestion: Use 'public', 'private', 'protected', or leave empty for package-private.");
                return;
            }

            Field field = new Field(name, type, visibility);
            classBuilder.addField(field);
            System.out.println("Field added.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void addMethod() {
        try {
            System.out.print("Method name: ");
            String name = scanner.nextLine().trim();
            try {
                ValidationUtils.validateMethodName(name);
            } catch (InvalidNameException e) {
                System.out.println("Validation error: " + e.getMessage());
                System.out.println("Suggestion: Method names should start with a lowercase letter, not be a Java keyword, and use only letters, digits, _ or $.");
                return;
            }

            // Check for duplicate method name
            if (classBuilder.getMethodByName(name) != null) {
                System.out.println("Error: A method with this name already exists in the class.");
                return;
            }

            System.out.print("Return type: ");
            String returnType = scanner.nextLine().trim();
            try {
                ValidationUtils.validateType(returnType);
            } catch (InvalidNameException e) {
                System.out.println("Validation error: " + e.getMessage());
                System.out.println("Suggestion: Use a valid Java type (e.g., int, String, double, or a valid class name). For arrays, use type[]");
                return;
            }

            System.out.print("Visibility (public/private/protected or empty for package-private): ");
            String visibility = scanner.nextLine().trim();
            try {
                ValidationUtils.validateVisibility(visibility);
            } catch (InvalidNameException e) {
                System.out.println("Validation error: " + e.getMessage());
                System.out.println("Suggestion: Use 'public', 'private', 'protected', or leave empty for package-private.");
                return;
            }

            Method method = new Method(name, returnType, visibility);
            classBuilder.addMethod(method);
            System.out.println("Method added.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void setModifiers() {
        try {
            System.out.print("Is abstract? (y/n): ");
            String abstractInput = scanner.nextLine().trim().toLowerCase();
            classBuilder.setAbstract(abstractInput.equals("y") || abstractInput.equals("yes"));

            System.out.print(" Interface/Class (y/n): ");
            String interfaceInput = scanner.nextLine().trim().toLowerCase();
            boolean isInterface = interfaceInput.equals("y") || interfaceInput.equals("yes");
            classBuilder.setInterface(isInterface);

            System.out.print("Extends (enter superclass/interface name or leave blank): ");
            String extendsName = scanner.nextLine().trim();
            if (!extendsName.isEmpty()) {
                ValidationUtils.validateClassName(extendsName);
                if (isInterface) {
                    // Ask if the user means to extend an interface or a class
                    System.out.print("You are editing an interface. Is '" + extendsName + "' an interface? (y/n): ");
                    String isExtendingInterface = scanner.nextLine().trim().toLowerCase();
                    if (isExtendingInterface.equals("y") || isExtendingInterface.equals("yes")) {
                        classBuilder.setExtendsClass(extendsName); // In Java, interfaces can extend other interfaces
                    } else {
                        System.out.println("Error: An interface cannot extend a class. In Java, interfaces can only extend other interfaces.");
                        System.out.println("Suggestion: Leave 'Extends' blank or enter an interface name if you want to extend another interface.");
                        classBuilder.setExtendsClass(null);
                    }
                } else {
                    classBuilder.setExtendsClass(extendsName);
                }
            } else {
                classBuilder.setExtendsClass(null);
            }

            System.out.print("Implements (comma separated interfaces or leave blank): ");
            String impls = scanner.nextLine().trim();
            if (!impls.isEmpty()) {
                String[] interfaces = impls.split(",");
                List<String> trimmed = new ArrayList<>();
                for (String iFace : interfaces) {
                    String ifaceTrim = iFace.trim();
                    if (!ifaceTrim.isEmpty()) {
                        ValidationUtils.validateClassName(ifaceTrim);
                        trimmed.add(ifaceTrim);
                    }
                }
                classBuilder.getImplementsInterfaces().clear();
                for (String iface : trimmed) {
                    classBuilder.addImplementsInterface(iface);
                }
            } else {
                classBuilder.getImplementsInterfaces().clear();
            }

            System.out.println("Modifiers set.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void saveClass() {
        // Check for duplicate field names before saving
        List<Field> fields = classBuilder.getFields();
        java.util.Set<String> fieldNames = new java.util.HashSet<>();
        boolean hasDuplicateField = false;
        for (Field f : fields) {
            if (!fieldNames.add(f.getName())) {
                System.out.println("Error: Duplicate field name detected: '" + f.getName() + "'. Please resolve duplicates before saving.");
                hasDuplicateField = true;
            }
        }
        // Check for duplicate method names before saving
        List<Method> methods = classBuilder.getMethods();
        java.util.Set<String> methodNames = new java.util.HashSet<>();
        boolean hasDuplicateMethod = false;
        for (Method m : methods) {
            if (!methodNames.add(m.getName())) {
                System.out.println("Error: Duplicate method name detected: '" + m.getName() + "'. Please resolve duplicates before saving.");
                hasDuplicateMethod = true;
            }
        }
        if (hasDuplicateField || hasDuplicateMethod) {
            System.out.println("Class not saved due to duplicate field or method names.");
            return;
        }
        String code = classBuilder.buildClass();
        String filePath = folderPath + File.separator + classBuilder.getClassName() + ".java";
        try {
            FileManager.writeAtomic(folderPath, classBuilder.getClassName(), code);
            System.out.println("Class saved to " + filePath);
        } catch (Exception e) {
            System.out.println("Failed to save class: " + e.getMessage());
        }
    }

    private JavaClassBuilder loadClass(String className) {
        File file = new File(folderPath, className + ".java");
        if (!file.exists()) {
            System.out.println("Class file not found.");
            return null;
        }
        try {
            String content = FileManager.readFile(file.getAbsolutePath());
            return new JavaClassBuilder(className);
        } catch (Exception e) {
            System.out.println("Error loading class file: " + e.getMessage());
            return null;
        }
    }

    private void pushUndo() {
        if (classBuilder != null) {
            undoStack.push(classBuilder.clone());
        }
    }

    private void clearRedo() {
        redoStack.clear();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(classBuilder.clone());
            classBuilder = undoStack.pop();
            System.out.println("Undo performed.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(classBuilder.clone());
            classBuilder = redoStack.pop();
            System.out.println("Redo performed.");
        } else {
            System.out.println("Nothing to redo.");
        }
    }

    // --- Syntax Highlighting for Java code preview ---
    private String highlightJava(String code) {
        // Basic ANSI color codes
        final String RESET = "\u001B[0m";
        final String KEYWORD = "\u001B[34m"; // Blue
        final String TYPE = "\u001B[36m";    // Cyan
        final String STRING = "\u001B[32m";  // Green
        final String COMMENT = "\u001B[90m"; // Bright black

        // Highlight keywords, types, and comments (very basic)
        String[] keywords = {"public", "private", "protected", "class", "interface", "abstract", "static", "final", "void", "extends", "implements", "return", "new"};
        String[] types = {"int", "long", "short", "byte", "float", "double", "boolean", "char", "String"};
        String highlighted = code;
        // Comments (single-line)
        highlighted = highlighted.replaceAll("(?m)//.*$", COMMENT + "$0" + RESET);
        // Comments (multi-line)
        highlighted = highlighted.replaceAll("(?s)/\\*.*?\\*/", COMMENT + "$0" + RESET);
        // Strings
        highlighted = highlighted.replaceAll("\"([^\"]*)\"", STRING + "\"$1\"" + RESET);
        // Keywords
        for (String kw : keywords) {
            highlighted = highlighted.replaceAll("\\b" + kw + "\\b", KEYWORD + kw + RESET);
        }
        // Types
        for (String t : types) {
            highlighted = highlighted.replaceAll("\\b" + t + "\\b", TYPE + t + RESET);
        }
        return highlighted;
    }

    private void showPreview() {
        System.out.println("\n--- Live Class Preview ---");
        System.out.println(highlightJava(classBuilder.buildClass()));
        showInheritanceTree();
        System.out.println("--------------------------\n");
    }

    // --- Inheritance Visualization ---
    private void showInheritanceTree() {
        System.out.println("Inheritance Tree:");
        System.out.print("  " + classBuilder.getClassName());
        if (classBuilder.getExtendsClass() != null && !classBuilder.getExtendsClass().isEmpty()) {
            System.out.print(" (extends " + classBuilder.getExtendsClass() + ")");
        }
        if (!classBuilder.getImplementsInterfaces().isEmpty()) {
            System.out.print(" (implements " + String.join(", ", classBuilder.getImplementsInterfaces()) + ")");
        }
        System.out.println();
    }

    // --- Refactor Tools ---
    private void refactorMenu() {
        System.out.println("\nRefactor Menu:");
        System.out.println("1. Rename field");
        System.out.println("2. Rename method");
        System.out.println("3. Change field type");
        System.out.println("4. Change method return type");
        System.out.println("5. Back");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                renameField();
                break;
            case "2":
                renameMethod();
                break;
            case "3":
                changeFieldType();
                break;
            case "4":
                changeMethodReturnType();
                break;
            default:
                break;
        }
    }
    private void renameField() {
        System.out.print("Enter current field name: ");
        String oldName = scanner.nextLine().trim();
        Field field = classBuilder.getFieldByName(oldName);
        if (field == null) {
            System.out.println("Field not found.");
            return;
        }
        System.out.print("Enter new field name: ");
        String newName = scanner.nextLine().trim();
        try {
            ValidationUtils.validateVariableName(newName);
            field.setName(newName);
            System.out.println("Field renamed.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
    private void renameMethod() {
        System.out.print("Enter current method name: ");
        String oldName = scanner.nextLine().trim();
        Method method = classBuilder.getMethodByName(oldName);
        if (method == null) {
            System.out.println("Method not found.");
            return;
        }
        System.out.print("Enter new method name: ");
        String newName = scanner.nextLine().trim();
        try {
            ValidationUtils.validateMethodName(newName);
            method.setName(newName);
            System.out.println("Method renamed.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
    private void changeFieldType() {
        System.out.print("Enter field name: ");
        String name = scanner.nextLine().trim();
        Field field = classBuilder.getFieldByName(name);
        if (field == null) {
            System.out.println("Field not found.");
            return;
        }
        System.out.print("Enter new field type: ");
        String newType = scanner.nextLine().trim();
        try {
            ValidationUtils.validateType(newType);
            field.setType(newType);
            System.out.println("Field type changed.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
    private void changeMethodReturnType() {
        System.out.print("Enter method name: ");
        String name = scanner.nextLine().trim();
        Method method = classBuilder.getMethodByName(name);
        if (method == null) {
            System.out.println("Method not found.");
            return;
        }
        System.out.print("Enter new return type: ");
        String newType = scanner.nextLine().trim();
        try {
            ValidationUtils.validateType(newType);
            method.setReturnType(newType);
            System.out.println("Method return type changed.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
}
