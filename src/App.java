import java.util.Random;
import java.util.Scanner;

public class App {


    static class User {
        private final String username;
        private final String role;

        public User(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }


    static class PermissionManager {

        public String getPermissions(String role) {
            switch (role.toLowerCase()) {
                case "student": return "VIEW ONLY";
                case "teacher": return "VIEW + EDIT";
                case "admin":   return "FULL ACCESS";
                default:        return "NO PERMISSIONS";
            }
        }

        public boolean canPerformAction(String role, String action) {
            String a = action.toLowerCase();
            switch (role.toLowerCase()) {
                case "student":
                    return a.equals("view");
                case "teacher":
                    return a.equals("view") || a.equals("edit");
                case "admin":
                    return true;
                default:
                    return false;
            }
        }
    }


    static class SecurityLayer {
        private final Scanner sc;
        private final Random rnd = new Random();

        public SecurityLayer(Scanner sc) {
            this.sc = sc;
        }


        public boolean passwordCheck(String correctPassword) {
            System.out.print("Enter password: ");
            String input = sc.nextLine();
            return input.equals(correctPassword);
        }


        public boolean twoFactorCheck() {
            int code = 1000 + rnd.nextInt(9000);
            System.out.println("Your 2FA code is: " + code);
            System.out.print("Enter 2FA code: ");
            String input = sc.nextLine().trim();

            try {
                int entered = Integer.parseInt(input);
                return entered == code;
            } catch (NumberFormatException e) {
                return false;
            }
        }


        public boolean intrusionDetection(String action) {
            String a = action.toLowerCase();
            if (a.equals("delete_database") || a.equals("shutdown_system")) {
                System.out.println("⚠ Intrusion detected! Action BLOCKED.");
                return false;
            }
            return true;
        }
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PermissionManager pm = new PermissionManager();

        boolean repeat = true;
        final String CORRECT_PASSWORD = "password123";

        while (repeat) {
            System.out.println("\n=== LOGIN SIMULATION ===");
            System.out.print("Enter username: ");
            String username = sc.nextLine().trim();
            String role = "";
            while (true) {
                System.out.print("Enter role (student / teacher / admin): ");
                role = sc.nextLine().trim().toLowerCase();
                if (role.equals("student") || role.equals("teacher") || role.equals("admin")) {
                    break;
                } else {
                    System.out.println("❌ Invalid role! Please choose only: student, teacher, or admin.\n");
                }
            }

            User user = new User(username, role);
            SecurityLayer sl = new SecurityLayer(sc);

            System.out.println("\n--- SECURITY CHECKS ---");

            boolean authSuccess = false;

            if (!sl.passwordCheck(CORRECT_PASSWORD)) {
                System.out.println("❌ Wrong password. Access denied.");
            } else {
                if (!sl.twoFactorCheck()) {
                    System.out.println("❌ 2FA failed. Access denied.");
                } else {
                    authSuccess = true;
                }
            }

            if (authSuccess) {
                System.out.println("✔ Authentication successful!");
                System.out.println("\nYour role: " + user.getRole());
                System.out.println("Permissions: " + pm.getPermissions(user.getRole()));

                System.out.print("\nEnter action (view / edit / delete_database / shutdown_system): ");
                String action = sc.nextLine().trim().toLowerCase();

         
                if (sl.intrusionDetection(action)) {
                    if (pm.canPerformAction(user.getRole(), action)) {
                        System.out.println("✔ Action allowed!");
                    } else {
                        System.out.println("❌ Action NOT allowed for your role!");
                    }
                }
            }

   
            System.out.print("\nDo you want to try again? (yes/no): ");
            String answer = sc.nextLine().trim().toLowerCase();
            if (!answer.equals("yes") && !answer.equals("y")) {
                repeat = false;
                System.out.println("\n👋 Exiting program... Goodbye!");
            } else {
                System.out.println("\n---------------------------------\n");
            }
        }

        sc.close();
    }
}
