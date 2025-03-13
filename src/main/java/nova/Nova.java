package nova;

import nova.command.Command;
import nova.NovaException;

/**
 * The main class for the Nova task management application.
 *
 * This class is responsible for initializing the application,
 * running the main command loop, and handling user input.
 */
public class Nova {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a new Nova instance.
     *
     * @param filePath The path to the file where tasks are stored.
     */
    public Nova(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        try {
            tasks = new TaskList(storage.load());
        } catch (NovaException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main command loop.
     *
     * This method continuously reads user input, parses it,
     * and executes the corresponding command.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (NovaException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * The main entry point for the application.
     *
     * @param args The command line arguments (not used).
     */
    public static void main(String[] args) {
        new Nova("./data/Nova.txt").run();
    }
}