package com.scitequest.martin.export;

/** Exception that is thrown if the JSON parsing of a file failed. */
public final class JsonParseException extends Exception {

    /**
     * Create a this exception based on a message.
     *
     * @param message the message
     */
    public JsonParseException(String message) {
        super(message);
    }

    /**
     * Create this exception based on a message and a preexisting throwable.
     *
     * @param message the message
     * @param e       the throwable
     */
    public JsonParseException(String message, Throwable e) {
        super(message, e);
    }
}
