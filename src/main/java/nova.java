import java.util.Scanner;

public class nova {
    public static void main(String[] args) {
        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Nova");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100]; // Track task completion status
        int taskCount = 0;

        String input;
        while (!(input = scanner.nextLine()).equals("bye")) {
            if (input.equals("list")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". [" + (isDone[i] ? "X" : " ") + "] " + tasks[i]);
                }
                System.out.println("____________________________________________________________");
            } else if (input.startsWith("mark ")) {
                int taskIndex = getTaskIndex(input, taskCount);
                if (taskIndex != -1) {
                    isDone[taskIndex] = true;
                    System.out.println("____________________________________________________________");
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   [X] " + tasks[taskIndex]);
                    System.out.println("____________________________________________________________");
                }
            } else if (input.startsWith("unmark ")) {
                int taskIndex = getTaskIndex(input, taskCount);
                if (taskIndex != -1) {
                    isDone[taskIndex] = false;
                    System.out.println("____________________________________________________________");
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   [ ] " + tasks[taskIndex]);
                    System.out.println("____________________________________________________________");
                }
            } else {
                if (taskCount < 100) {
                    tasks[taskCount] = input;
                    isDone[taskCount] = false;
                    taskCount++;
                    System.out.println("____________________________________________________________");
                    System.out.println(" added: " + input);
                    System.out.println("____________________________________________________________");
                } else {
                    System.out.println(" Task list is full!");
                }
            }
        }

        scanner.close();
        System.out.println("____________________________________________________________");
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }

    private static int getTaskIndex(String input, int taskCount) {
        try {
            int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
            if (taskIndex >= 0 && taskIndex < taskCount) {
                return taskIndex;
            }
            System.out.println(" Invalid task number.");
        } catch (Exception e) {
            System.out.println(" Invalid input format. Use: mark [number] or unmark [number]");
        }
        return -1;
    }
}
