package client.command;

import java.io.DataOutputStream;
import java.io.IOException;

public class ExitCommand implements Command {

    private final DataOutputStream  out;

    public ExitCommand(DataOutputStream out) {
        this.out = out;
    }

    @Override
    public void execute() throws IOException {
        out.writeUTF("EXIT");
        System.out.println("The request was sent.");
    }
}
