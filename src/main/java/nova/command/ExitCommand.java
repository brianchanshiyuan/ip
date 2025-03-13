package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;

/**
 * A command to exit the application.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        ui.showExit();
    }

    /**
     * Returns true, as this command is an exit command.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}