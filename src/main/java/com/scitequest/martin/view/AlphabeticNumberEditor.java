package com.scitequest.martin.view;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;

/**
 * The {@code AlphabeticNumberEditor} class is a {@link JSpinner} editor that
 * formats the value in
 * alphabetic numbering (A, B, C, ... Z, AA, AB, AC, ...).
 */
public final class AlphabeticNumberEditor extends JSpinner.DefaultEditor {

    /** The alphabet used for formatting values. */
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Converts the given integer to its alphabetic numbering representation.
     *
     * @param n the integer to be converted
     * @return the alphabetic numbering representation of the given integer
     */
    public static String toAlphabeticNumber(int n) {
        StringBuilder result = new StringBuilder();
        while (n >= 0) {
            result.insert(0, ALPHABET.charAt(n % 26));
            n = n / 26 - 1;
        }
        return result.toString();
    }

    /**
     * Converts the given alphabetic numbering representation to its corresponding
     * integer.
     *
     * @param alphabeticNumber the alphabetic numbering representation to be
     *                         converted
     * @return the corresponding integer of the given alphabetic numbering
     *         representation
     */
    public static int fromAlphabeticNumber(String alphabeticNumber) {
        int result = 0;
        for (int i = 0; i < alphabeticNumber.length(); i++) {
            result = result * 26 + (ALPHABET.indexOf(alphabeticNumber.charAt(i)) + 1);
        }
        return result - 1;
    }

    /**
     * Constructs a new {@code AlphabeticNumberEditor} for the specified
     * {@link JSpinner}.
     *
     * @param spinner the {@link JSpinner} to be edited by this editor
     */
    public AlphabeticNumberEditor(JSpinner spinner) {
        super(spinner);
        JFormattedTextField textField = getTextField();
        textField.setFormatterFactory(
                new DefaultFormatterFactory(new AlphabeticNumberFormatter()));
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setEditable(true);
    }

    /**
     * The {@code AlphabeticNumberFormatter} class is a {@link JFormattedTextField}
     * formatter that
     * formats values as alphabetic numbering.
     */
    private static class AlphabeticNumberFormatter extends JFormattedTextField.AbstractFormatter {
        @Override
        public String valueToString(Object value) throws ParseException {
            return toAlphabeticNumber((int) value);
        }

        @Override
        public Object stringToValue(String text) throws ParseException {
            return fromAlphabeticNumber(text);
        }
    }
}
