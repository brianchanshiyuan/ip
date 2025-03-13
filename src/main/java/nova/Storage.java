package nova;

import nova.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The storage class for the Nova application.
 *
 * This class is responsible for loading tasks from and saving tasks to a file.
 */
public class Storage {
    private final String filePath;

    /**
     * Constructs a new Storage instance.
     *
     * @param filePath The path to the file where tasks are stored.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }


    /**
     * Loads tasks from the storage file.
     *
     * @return An ArrayList of tasks loaded from the file.
     * @throws NovaException If there is an error loading the tasks.
     */
    public ArrayList<Task> load() throws NovaException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Files.createDirectories(Paths.get("./data"));
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    tasks.add(parseTask(scanner.nextLine()));
                }
                scanner.close();
            }
        } catch (IOException e) {
            throw new NovaException("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Parses a task from a line of text.
     *
     * @param line The line of text to parse.
     * @return The parsed task.
     * @throws NovaException If there is an error parsing the task.
     */
    private Task parseTask(String line) throws NovaException {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            throw new NovaException("Invalid task format in file.");
        }
        String type = parts[0].trim();
        String description = parts[2].trim();
        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (parts.length == 4) {
                task = new Deadline(description, "by: " + parts[3].trim());
            } else {
                throw new NovaException("Invalid deadline format in file.");
            }
            break;
        case "E":
            if (parts.length == 5) {
                task = new Event(description, "from: " + parts[3].trim(), "to: " + parts[4].trim());
            } else {
                throw new NovaException("Invalid event format in file.");
            }
            break;
        default:
            throw new NovaException("Unknown task type in file.");
        }
        task.markAsDone(parts[1].trim().equals("1"));
        return task;
    }


    /**
     * Saves tasks to the storage file.
     *
     * @param tasks The list of tasks to save.
     * @throws NovaException If there is an error saving the tasks.
     */
    public void save(ArrayList<Task> tasks) throws NovaException {
        try {
            FileWriter writer = new FileWriter(filePath);
            for (Task task : tasks) {
                writer.write(formatTask(task) + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            throw new NovaException("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Formats a task as a string for saving to the file.
     *
     * @param task The task to format.
     * @return The formatted task string.
     */
    private String formatTask(Task task) {
        String type = "";
        if (task instanceof Todo) {
            type = "T";
        } else if (task instanceof Deadline) {
            type = "D";
        } else if (task instanceof Event) {
            type = "E";
        }
        int isDone = task.isDone ? 1 : 0;
        String formatted = type + " | " + isDone + " | " + task.description;
        if (task instanceof Deadline) {
            formatted += " | " + ((Deadline) task).by.substring(4).trim();
        } else if (task instanceof Event) {
            formatted += " | " + ((Event) task).from.substring(6).trim() + " | " + ((Event) task).to.substring(4).trim();
        }
        return formatted;
    }
}