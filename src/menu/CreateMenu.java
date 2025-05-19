package menu;

import builder.JavaClassBuilder;
import model.Field;
import model.Method;
import exception.InvalidNameException;
import util.ValidationUtils;
import util.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateMenu  {
    private final Scanner scanner;
    private final String folderPath;
    private JavaClassBuilder classBuilder;
    private boolean isModifyMode = false;

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
                    addField();
                    break;
                case "2":
                    addMethod();
                    break;
                case "3":
                    setModifiers();
                    break;
                case "4":
                    saveClass();
                    done = true;
                    break;
                case "5":
                    System.out.println("Operation cancelled.");
                    done = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
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
                this.classBuilder = new JavaClassBuilder(name);
                break;
            } catch (InvalidNameException e) {
                System.out.println("Invalid class name: " + e.getMessage());
            }
        }
    }
        private void printSubMenu() {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║           Feartures Menu             ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Add field                         ║");
            System.out.println("║ 2. Add method                        ║");
            System.out.println("║ 3. Set class/interface modifiers     ║");
            System.out.println("║ 4. Save class/interface              ║");
            System.out.println("║ 5. Cancel                            ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Enter your choice: ");
        }

    private void addField() {
        try {
            System.out.print("Field name: ");
            String name = scanner.nextLine().trim();
            ValidationUtils.validateVariableName(name);

            System.out.print("Field type: ");
            String type = scanner.nextLine().trim();
            ValidationUtils.validateType(type);

            System.out.print("Visibility (public/private/protected or empty for package-private): ");
            String visibility = scanner.nextLine().trim();
            ValidationUtils.validateVisibility(visibility);

            Field field = new Field(name, type, visibility);
            classBuilder.addField(field);
            System.out.println("Field added.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void addMethod() {
        try {
            System.out.print("Method name: ");
            String name = scanner.nextLine().trim();
            ValidationUtils.validateMethodName(name);

            System.out.print("Return type: ");
            String returnType = scanner.nextLine().trim();
            ValidationUtils.validateType(returnType);

            System.out.print("Visibility (public/private/protected or empty for package-private): ");
            String visibility = scanner.nextLine().trim();
            ValidationUtils.validateVisibility(visibility);

            Method method = new Method(name, returnType, visibility);
            classBuilder.addMethod(method);
            System.out.println("Method added.");
        } catch (InvalidNameException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void setModifiers() {
        try {
            System.out.print("Is abstract? (y/n): ");
            String abstractInput = scanner.nextLine().trim().toLowerCase();
            classBuilder.setAbstract(abstractInput.equals("y") || abstractInput.equals("yes"));

            System.out.print(" Interface/Class (y/n): ");
            String interfaceInput = scanner.nextLine().trim().toLowerCase();
            classBuilder.setInterface(interfaceInput.equals("y") || interfaceInput.equals("yes"));

            System.out.print("Extends (enter superclass name or leave blank): ");
            String extendsName = scanner.nextLine().trim();
            if (!extendsName.isEmpty()) {
                ValidationUtils.validateClassName(extendsName);
                classBuilder.setExtendsClass(extendsName);
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
}
