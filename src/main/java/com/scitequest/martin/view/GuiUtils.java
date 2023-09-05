package com.scitequest.martin.view;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.MissingResourceException;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.scitequest.martin.Const;

/** Static methods of commonly used GUI tasks such as showing dialogs. */
public final class GuiUtils {

    /** Utility classes should not have a constructor. */
    private GuiUtils() {
    }

    private static void showMessageDialog(Frame owner, String message, String title, int style) {
        JOptionPane.showMessageDialog(owner, message, title, style);
    }

    private static void showMessageDialog(Dialog owner, String message, String title, int style) {
        JOptionPane.showMessageDialog(owner, message, title, style);
    }

    /**
     * Show a error message dialog to the user.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Error".
     *
     * @param owner   the parent frame, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showErrorDialog(Frame owner, final String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Error";
        }
        showMessageDialog(owner, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a error message dialog to the user.
     * In addition a button to open the path to the logfile is present.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Error".
     *
     * @param owner   the parent frame, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showLogfileErrorDialog(Frame owner, final String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Error";
        }
        String[] optionLabels = new String[1];
        try {
            optionLabels[0] = Const.bundle.getString("errorDialog.openLogfilePath.text");
        } catch (MissingResourceException e) {
            optionLabels[0] = "Open Path";
        }

        int option = JOptionPane.showOptionDialog(owner, message, title, JOptionPane.YES_OPTION,
                JOptionPane.ERROR_MESSAGE, null, optionLabels, null);
        if (option == JOptionPane.YES_OPTION) {
            File logFile = new File(Const.PROJECT_DIRECTORIES.dataLocalDir);
            try {
                Desktop.getDesktop().open(logFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Show a error message dialog to the user.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Error".
     *
     * @param owner   the parent dialog, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showErrorDialog(Dialog owner, final String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Error";
        }
        showMessageDialog(owner, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a warning message dialog to the user.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Warning".
     *
     * @param owner   the parent frame, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showWarningDialog(Frame owner, String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Warning";
        }
        showMessageDialog(owner, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show a warning message dialog to the user.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Warning".
     *
     * @param owner   the parent dialog, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showWarningDialog(Dialog owner, String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Warning";
        }
        showMessageDialog(owner, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show a information message dialog to the user.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Information".
     *
     * @param owner   the parent frame, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showInfoDialog(Frame owner, String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Information";
        }
        showMessageDialog(owner, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a information message dialog to the user.
     *
     * Note: The title can be null or empty. In this case the title is set to
     * "Information".
     *
     * @param owner   the parent dialog, can be null
     * @param message The message to display. Recommended usage
     *                {@code String.format("Something happened:%n%s", exeption);}
     * @param title   The title of the dialog
     */
    public static void showInfoDialog(Dialog owner, String message, String title) {
        if (title == null || title.isEmpty()) {
            title = "Information";
        }
        showMessageDialog(owner, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Wrapper for choosing a file.
     *
     * @param owner       the parent dialog
     * @param mode        {@code FileDialog.LOAD} or {@code FileDialog.SAVE}
     * @param title       the title of the dialog
     * @param fileprefill the text that should be prefilled in the filename.
     * @return empty, if the dialog was canceled, otherwise the selected file
     */
    public static Optional<Path> chooseFile(Dialog owner, int mode,
            String title, String fileprefill) {
        return chooseFileInternal(new FileDialog(owner), mode, title, fileprefill);
    }

    /**
     * Wrapper for choosing a file.
     *
     * @param owner       the parent frame
     * @param mode        {@code FileDialog.LOAD} or {@code FileDialog.SAVE}
     * @param title       the title of the dialog
     * @param fileprefill the text that should be prefilled in the filename.
     * @return empty, if the dialog was canceled, otherwise the selected file
     */
    public static Optional<Path> chooseFile(Frame owner, int mode,
            String title, String fileprefill) {
        return chooseFileInternal(new FileDialog(owner), mode, title, fileprefill);
    }

    private static Optional<Path> chooseFileInternal(FileDialog dialog, int mode,
            String title, String fileprefill) {
        dialog.setMode(mode);
        dialog.setTitle(title);
        dialog.setFile(fileprefill);
        dialog.setVisible(true);
        // Get information from open dialog
        String dir = dialog.getDirectory();
        String filename = dialog.getFile();
        // Kill the dialog
        dialog.dispose();
        if (dir == null || filename == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Path.of(dir, filename));
    }

    /**
     * Wrapper for loading a json-File.
     *
     * @param owner the parent dialog, can be null
     * @param mode  {@code FileDialog.LOAD} or {@code FileDialog.SAVE}
     * @return empty, if the dialog was canceled, otherwise the selected file
     */
    public static Optional<Path> chooseJsonFile(Dialog owner, int mode) {
        return chooseFile(owner, mode, "Please choose a JSON file", "*.json");
    }

    /**
     * Wrapper for loading a json-File with a prefilled name.
     * This is used for exporting interactiveListElements.
     *
     * @param owner       the parent dialog, can be null
     * @param mode        {@code FileDialog.LOAD} or {@code FileDialog.SAVE}
     * @param namePrefill name suggestion for the file dialog
     * @return empty, if the dialog was canceled, otherwise the selected file
     */
    public static Optional<Path> chooseJsonFile(Dialog owner, int mode, String namePrefill) {
        return chooseFile(owner, mode, "Please choose a JSON file", namePrefill + ".json");
    }

    /**
     * Wrapper for choosing a directory.
     *
     * @param parent the parent dialog, can be null
     * @param title  the title to use for the dialog
     * @return empty, if the dialog was canceled, otherwise the selected file
     */
    public static Optional<Path> chooseDirectory(Component parent, String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (title != null) {
            chooser.setDialogTitle(title);
        }
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return Optional.ofNullable(chooser.getSelectedFile().toPath().toAbsolutePath());
        }
        return Optional.empty();
    }

    /**
     * Wrapper for choosing a directory.
     *
     * @param parent the parent dialog, can be null
     * @param title  the title to use for the dialog
     * @return empty, if the dialog was canceled, otherwise the selected file
     */
    public static Optional<Path> chooseDirectory(Dialog parent, String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (title != null) {
            chooser.setDialogTitle(title);
        }
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return Optional.ofNullable(chooser.getSelectedFile().toPath().toAbsolutePath());
        }
        return Optional.empty();
    }

    /**
     * Attempt to parse various date, time or datetime formats.
     *
     * The parsing defaults to the current local date for the system timezone.
     * For the default time the start of the day is used.
     *
     * @param s the String to parse
     * @return a datetime with an associated timezone
     * @throws DateTimeParseException if the datetime could not be parsed
     */
    public static ZonedDateTime parseDateTime(String s) throws DateTimeParseException {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter format = new DateTimeFormatterBuilder()
                .appendOptional(ISO_DATE_TIME)
                .appendOptional(ISO_INSTANT)
                .appendOptional(ISO_LOCAL_DATE)
                .appendOptional(ISO_LOCAL_TIME)
                .parseDefaulting(ChronoField.YEAR, now.getYear())
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, now.getMonthValue())
                .parseDefaulting(ChronoField.DAY_OF_MONTH, now.getDayOfMonth())
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter()
                .withZone(ZoneId.systemDefault());
        return ZonedDateTime.parse(s, format);
    }
}
