package nova.task;

/**
 * A class representing a deadline task.
 */
public class Deadline extends Task {
    public String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (" + by + ")";
    }
}