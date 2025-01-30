import java.util.Scanner;

public class nova {
    public static void main(String[] args) {
        // Greeting message
        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Nova");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);

        String input;
        while (true) {
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("bye")) {
                break;
            }

            System.out.println("____________________________________________________________");
            System.out.println(" " + input);
            System.out.println("____________________________________________________________");
        }

        scanner.close();

        System.out.println("____________________________________________________________");
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}

