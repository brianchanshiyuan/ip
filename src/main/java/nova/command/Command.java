package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;

/**
 * An abstract class representing a command in the Nova application.
 */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException;

    public boolean isExit() {
        return false;
    }
}