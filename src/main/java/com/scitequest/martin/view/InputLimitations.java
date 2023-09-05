package com.scitequest.martin.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public final class InputLimitations extends PlainDocument {

    private final int signLimit;
    private final boolean allowNumbers;
    private final boolean allowUpperCase;
    private final boolean allowSpace;
    private final String allowedSpecialCharacters;

    public InputLimitations(int signLimit,
            boolean allowNumbers, boolean allowUpperCase, boolean allowSpace,
            String allowedSpecialCharacters) {
        super();
        this.signLimit = signLimit;
        this.allowNumbers = allowNumbers;
        this.allowUpperCase = allowUpperCase;
        this.allowSpace = allowSpace;
        this.allowedSpecialCharacters = allowedSpecialCharacters;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

        if (str == null || this.getLength() + str.length() > signLimit) {
            return;
        }

        if (!allowUpperCase) {
            str = str.toLowerCase();
        }
        String retString = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (Character.isLetterOrDigit(c) && allowNumbers) {
                retString += c;
                continue;
            }

            if (c == ' ' && allowSpace) {
                retString += c;
                continue;
            }

            for (int j = 0; j < allowedSpecialCharacters.length(); j++) {
                char specialChar = allowedSpecialCharacters.charAt(j);
                if (c == specialChar) {
                    retString += c;
                    break;
                }
            }
        }
        if (str == "\n") {
            retString = "\n";
        }
        super.insertString(offs, retString, a);
    }
}
