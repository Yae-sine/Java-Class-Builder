package menu;

import util.FileManager;

import builder.JavaClassBuilder;
import builder.JavaClassScan;

import model.Field;
import model.Method;

import java.io.File;
import java.util.Scanner;

public class Menu implements ConsoleDisplay  {
    private final Scanner scanner;
    private final String folderPath;

    public Menu(Scanner scanner, String folderPath) {
        this.scanner = scanner;
        this.folderPath = folderPath;
    }


    public void start() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    new CreateMenu(scanner, folderPath).start();
                    break;
                case "2":
                    readClass();
                    break;
                case "3":
                    modifyClass();
                    break;
                case "4":
                    deleteClass();
                    break;
                case "5":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number 1-5.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║              Menu Principal              ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ 1. Create new class/interface            ║");
        System.out.println("║ 2. Read (display) a class                ║");
        System.out.println("║ 3. Modify an existing class              ║");
        System.out.println("║ 4. Delete a class file                   ║");
        System.out.println("║ 5. Exit                                  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Enter your choice: ");
    }

    private void readClass() {
        System.out.print("Enter class name to read: ");
        String className = scanner.nextLine().trim();
        File classFile = new File(folderPath, className + ".java");

        if (!classFile.exists()) {
            System.out.println("Class file does not exist.");
            return;
        }

        try {
            String content = FileManager.readFile(classFile.getAbsolutePath());
            JavaClassBuilder builder = JavaClassScan.scan(content);
            displayClass(builder);
        } catch (Exception e) {
            System.out.println("Error reading or parsing class file: " + e.getMessage());
        }
    }

    private void modifyClass() {
        System.out.print("Enter class name to modify: ");
        String className = scanner.nextLine().trim();
        File classFile = new File(folderPath, className + ".java");
        if (!classFile.exists()) {
            System.out.println("Class file does not exist.");
            return;
        }
        new CreateMenu(scanner, folderPath, className).startModify();
    }

    private void deleteClass() {
        System.out.print("Enter class name to delete: ");
        String className = scanner.nextLine().trim();
        File classFile = new File(folderPath, className + ".java");
        if (!classFile.exists()) {
            System.out.println("Class file does not exist.");
            return;
        }
        if (classFile.delete()) {
            System.out.println("Class file deleted successfully.");
        } else {
            System.out.println("Failed to delete class file.");
        }
    }
    public void displayClass(JavaClassBuilder classBuilder) {
        final String topLeft = "╔";
        final String topRight = "╗";
        final String bottomLeft = "╚";
        final String bottomRight = "╝";
        final String horizontal = "═";
        final String vertical = "║";
        int boxWidth = 100;

        Runnable printTopLine = () -> System.out.println(topLeft + horizontal.repeat(boxWidth) + topRight);
        Runnable printBottomLine = () -> System.out.println(bottomLeft + horizontal.repeat(boxWidth) + bottomRight);

        java.util.function.Consumer<String> printCentered = text -> {
            int padding = (boxWidth - text.length()) / 2;
            int paddingRight = boxWidth - text.length() - padding;
            System.out.println(vertical + " ".repeat(padding) + text + " ".repeat(paddingRight) + vertical);
        };


        printTopLine.run();

        String type = classBuilder.isInterface() ? "Interface" : "Class";
        String abstractText = classBuilder.isAbstract() ? "(abstract)" : "";
        String className = classBuilder.getClassName();

        String parents = "";
        if (classBuilder.getExtendsClass() != null && !classBuilder.getExtendsClass().isEmpty()) {
            parents += " extends " + classBuilder.getExtendsClass();
        }
        if (!classBuilder.getImplementsInterfaces().isEmpty()) {
            parents += " implements " + String.join(", ", classBuilder.getImplementsInterfaces());
        }

        String header = String.format(" %s %s %s(%s)", type, abstractText, className, parents).trim();
        printCentered.accept(header);

        System.out.println("╠" + horizontal.repeat(boxWidth) + "╣");

        for (Field f : classBuilder.getFields()) {
            StringBuilder fieldLine = new StringBuilder();
            fieldLine.append(f.getVisibility() == null ? "" : f.getVisibility()).append(" ");
            if (f.isFinal()) fieldLine.append("final ").append(" ");
            if (f.isStatic()) fieldLine.append("static ").append(" ");
            fieldLine.append(f.getType() == null ? "" : f.getType()).append(" ");
            fieldLine.append(f.getName() == null ? "" : f.getName());
            if (f.getInitialValue() != null && !f.getInitialValue().isEmpty()) {
                fieldLine.append(" = ").append(f.getInitialValue());
            }
            System.out.println(vertical + fieldLine+ " ".repeat(boxWidth - fieldLine.length() ) + vertical);
        }

        System.out.println("╠" + horizontal.repeat(boxWidth) + "╣");


        for (Method m : classBuilder.getMethods()) {
            StringBuilder methodLine = new StringBuilder();
            methodLine.append(m.getVisibility() == null ? "" : m.getVisibility()).append(" ");
            if (m.isAbstract()) methodLine.append("abstract ").append(" ");
            if (m.isStatic()) methodLine.append("static ").append(" ");
            methodLine.append(m.getReturnType() == null ? "void" : m.getReturnType()).append(" ");
            methodLine.append(m.getName() == "" ? "" : m.getName());
            methodLine.append("(");
            String params = m.getParameters().stream()
                    .map(p -> (p.isFinal() ? "final " : "") + p.getType() + " " + p.getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            methodLine.append(params).append(")");
            System.out.println(vertical + methodLine.toString().strip() + " ".repeat(boxWidth - methodLine.toString().strip().length() ) + vertical);
        }

        printBottomLine.run();
    }


}

