package client.command;

import java.util.Scanner;

public class CommandUtil {

    public static String[] getTypeAndValue(Scanner scanner, String action) {
        System.out.print("Do you want to " + action + " file by name or by id (1 - name, 2 - id): ");
        String type = scanner.nextLine().trim();
        if (!"1".equals(type) && !"2".equals(type)) {
            System.err.println("Invalid type. Please enter 1 or 2.");
            return null;
        }
        System.out.print("\nEnter " + ("1".equals(type) ? "name" : "id") + ": ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.err.println("Input cannot be empty.");
            return null;
        }
        return new String[]{type, value};
    }
}