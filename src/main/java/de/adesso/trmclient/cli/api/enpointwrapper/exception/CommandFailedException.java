package de.adesso.trmclient.cli.api.enpointwrapper.exception;

public class CommandFailedException extends Throwable{

    public CommandFailedException() {
        super("Command failed on Server.");
    }
}
