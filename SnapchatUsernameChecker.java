import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SnapchatUsernameChecker {
    private static final Random RANDOM = new Random();
    private static final Scanner scanner = new Scanner(System.in);

    // ANSI color codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";

    public static void main(String[] args) {
        printSnapchatHeader("Snapchat Username Availability Checker By <RSR/>");

        int option = getUsernameOption();
        List<String> usernames = new ArrayList<>();

        if (option == 1) {
            String singleUsername = getSingleUsername();
            usernames.add(singleUsername);
        } else if (option == 2) {
            String filePath = getFilePath();
            usernames = getUsernamesFromFile(filePath);
        } else if (option == 3) {
            int count = getUsernamesCount();
            String customLetter = getCustomLetter();
            int length = getUsernameLength();
            List<String> generatedUsernames = generateUsernames(count, customLetter, length);
            printColoredMessage("Generated usernames:", Color.YELLOW);
            printUsernames(generatedUsernames);
            usernames.addAll(generatedUsernames);
        } else {
            System.out.println("Invalid option. Exiting the program.");
            return;
        }

        List<String> availabilityUsernames = checkUsernameAvailability(usernames);

        if (availabilityUsernames.isEmpty()) {
            printColoredMessage("All usernames are unavailable on Snapchat.", Color.RED);
        } else {
            printColoredMessage("Available usernames on Snapchat:", Color.GREEN);
            printUsernames(availabilityUsernames);
        }

        scanner.close();
    }

    public static int getUsernameOption() {
        System.out.println("Select an option:");
        System.out.println("1. Check a single username");
        System.out.println("2. Check usernames from a file");
        System.out.println("3. Generate usernames");

        int option = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        return option;
    }

    public static String getSingleUsername() {
        System.out.println("Enter the username to check:");
        String username = scanner.nextLine();
        return username;
    }

    public static String getFilePath() {
        System.out.println("Enter the file path:");
        String filePath = scanner.nextLine();
        return filePath;
    }

    public static List<String> getUsernamesFromFile(String filePath) {
        List<String> usernames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                usernames.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return usernames;
    }

    public static int getUsernamesCount() {
        int count = 0;
        try {
            printColoredMessage("Enter the number of usernames to generate:", Color.YELLOW);
            count = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
        } catch (Exception e) {
            printColoredMessage("Invalid input. Please enter a valid integer value.", Color.RED);
        }
        return count;
    }

    public static String getCustomLetter() {
        printColoredMessage("Enter the custom letter to include in the usernames:", Color.YELLOW);
        String customLetter = scanner.nextLine();
        return customLetter;
    }

    public static int getUsernameLength() {
        int length = 0;
        try {
            printColoredMessage("Enter the length of the usernames to generate:", Color.YELLOW);
            length = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
        } catch (Exception e) {
            printColoredMessage("Invalid input. Please enter a valid integer value.", Color.RED);
        }
        return length;
    }

    public static List<String> generateUsernames(int count, String customLetter, int length) {
        List<String> generatedUsernames = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            StringBuilder generatedUsername = new StringBuilder();

            while (generatedUsername.length() < length) {
                char randomChar = generateRandomAlphabet(customLetter);
                if (generatedUsername.length() == 0 && !Character.isDigit(randomChar)) {
                    generatedUsername.append(randomChar);
                } else if (generatedUsername.length() > 0) {
                    generatedUsername.append(randomChar);
                }
            }

            generatedUsernames.add(generatedUsername.toString());
        }

        return generatedUsernames;
    }

    public static char generateRandomAlphabet(String customLetter) {
        int randomIndex = RANDOM.nextInt(customLetter.length());
        return customLetter.charAt(randomIndex);
    }

    public static List<String> checkUsernameAvailability(List<String> usernames) {
        List<String> availabilityUsernames = new ArrayList<>();

        printColoredMessage("Checking username availability...", Color.CYAN);

        for (String username : usernames) {
            String url = "https://www.snapchat.com/add/" + username;
            try {
                printColoredMessage("Checking username: " + username, Color.CYAN);
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    availabilityUsernames.add(username);
                    printColoredMessage("Username '" + username + "' is available on Snapchat.", Color.GREEN);
                } else {
                    printColoredMessage("Username '" + username + "' is not available on Snapchat.", Color.RED);
                }

                // Sleep for 1 second before checking the next username
                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        return availabilityUsernames;
    }

    public static void printUsernames(List<String> usernames) {
        for (String username : usernames) {
            System.out.println(username);
        }
    }

    public static void printColoredMessage(String message, Color color) {
        String colorCode;
        switch (color) {
            case RED:
                colorCode = ANSI_RED;
                break;
            case GREEN:
                colorCode = ANSI_GREEN;
                break;
            case YELLOW:
                colorCode = ANSI_YELLOW;
                break;
            case CYAN:
                colorCode = ANSI_CYAN;
                break;
            default:
                colorCode = ANSI_RESET;
        }
        System.out.println(colorCode + message + ANSI_RESET);
    }

    public static void printSnapchatHeader(String text) {
        int length = text.length();
        StringBuilder headerLine = new StringBuilder();
        for (int i = 0; i < length + 4; i++) {
            headerLine.append("*");
        }

        System.out.println(headerLine);
        System.out.println("* " + text + " *");
        System.out.println(headerLine);
    }

    public enum Color {
        DEFAULT,
        RED,
        GREEN,
        YELLOW,
        CYAN
    }
}
