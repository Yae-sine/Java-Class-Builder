package menu;

public interface ConsoleDisplay {
    default void displayClass() {
        System.out.println("     ClassName      \n        Modifiers        \n       fields      \n         methodes        ");
    }

}
