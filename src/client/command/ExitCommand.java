package client.command;

import java.io.IOException;
import java.io.PrintWriter;

public class ExitCommand implements Command {

    private final PrintWriter out;

    public ExitCommand(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void execute() throws IOException {
        out.println("EXIT");
        System.out.println("The request was sent.");
    }
}
