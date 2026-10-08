package com.ems;

import com.ems.ui.ConsoleMenu;
import com.ems.ui.EmployeeGUI;
import com.ems.util.AnsiColors;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.Scanner;

/**
 * Application Entry Point for Employee Management System.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Check command line flags
        if (args.length > 0) {
            String mode = args[0].toLowerCase();
            if (mode.equals("--cli") || mode.equals("-c") || mode.equals("cli")) {
                launchConsole();
                return;
            } else if (mode.equals("--gui") || mode.equals("-g") || mode.equals("gui")) {
                launchGUI();
                return;
            }
        }

        // 2. If running without arguments, check environment and present choice or launch CLI
        boolean isHeadless = GraphicsEnvironment.isHeadless();
        if (isHeadless) {
            launchConsole();
            return;
        }

        System.out.println(AnsiColors.BRIGHT_CYAN + """
        +======================================================================+
        |                 EMPLOYEE MANAGEMENT SYSTEM LAUNCHER                  |
        +======================================================================+
        """ + AnsiColors.RESET);

        System.out.println("Select interface mode to launch:");
        System.out.println("  " + AnsiColors.boldCyan("1.") + " Interactive Terminal Console (CLI)");
        System.out.println("  " + AnsiColors.boldCyan("2.") + " Graphical Desktop Window (Swing GUI)");
        System.out.print("\n👉 Enter option [1 or 2, default is 1]: ");

        Scanner scanner = new Scanner(System.in);
        String input = "";
        try {
            if (scanner.hasNextLine()) {
                input = scanner.nextLine().trim();
            }
        } catch (Exception ignored) {}

        if ("2".equals(input)) {
            launchGUI();
        } else {
            launchConsole();
        }
    }

    private static void launchConsole() {
        ConsoleMenu menu = new ConsoleMenu();
        menu.start();
    }

    private static void launchGUI() {
        System.out.println(AnsiColors.boldGreen("Launching Employee Management Desktop GUI..."));
        SwingUtilities.invokeLater(() -> {
            try {
                EmployeeGUI gui = new EmployeeGUI();
                gui.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to open GUI: " + e.getMessage());
                System.out.println("Falling back to CLI console mode...");
                launchConsole();
            }
        });
    }
}
