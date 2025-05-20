import java.util.Scanner;
import menu.Menu;
import util.FolderUtils;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      Welcome to Java Class Builder                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝\n");

        String workingFolder = FolderUtils.validateOrCreateFolder(scanner);

        Menu menu = new Menu(scanner, workingFolder);
        menu.start();

        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                Exiting Java Class Builder. Goodbye!                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        scanner.close();
    }
}
