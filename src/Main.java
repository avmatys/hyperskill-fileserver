import java.util.Scanner;

import uploader.*;
import storage.*;

public class Main {

    public static void main(String[] args) {
        
        FileStorage storage = new LocalFileStorage();
        FileUploader uploader = new FileUploader(storage);

        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        while (!userInput.equalsIgnoreCase("exit")) {
            userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("exit")) 
                break;
            handleCommand(uploader, userInput);
        }
    }

    public static void handleCommand(FileUploader uploader, String commandLine) {
        String[] parts = commandLine.split(" ");
        if (parts.length < 2) {
            System.out.println("Incorrect data format");
            return;
        }
        String operation = parts[0];
        String filename = parts[1];
        switch (operation) {
            case "add":
                System.out.println(uploader.upload(filename) ? "The file " + filename + " added successfully"                                                                         : "Cannot add the file " + filename);
                break;
            case "delete":
                System.out.println(uploader.delete(filename) ? "The file " + filename + " was deleted"
                                                             : "The file " + filename + " not found");
                break;
            case "get" : 
                System.out.println(uploader.exists(filename) ? "The file " + filename + " was sent"
                                                             : "The file " + filename + " not found");
                break;
            default:
                break;
        }
    }

}
