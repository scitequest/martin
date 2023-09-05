package com.scitequest.martin.view;

import java.nio.file.Path;
import java.util.List;

public interface ListDialog {

    String CHAR_EXCEPTIONS_NAME = "()._";
    int CHAR_LIMIT_NAME = 30;
    String CHAR_EXCEPTIONS_DESCRIPTION = "(),._+-?!";
    int CHAR_LIMIT_DESCRIPTION = 500;

    /**
     * This method loads listable objects into a fitting Dialog.
     *
     * @param index index of the element in the data list to load.
     */
    void populateInputs(int index);

    /**
     * Validate wether the current inputs are a valid data element.
     *
     * @param index the index to validate
     * @return true if the data is valid.
     */
    boolean validateInputs(int index);

    /**
     * Tries to initiate export for current content of dialog.
     *
     * @param index selected list element by index.
     * @param path  the filepath to write the export data
     */
    void handleExport(int index, Path path);

    void handleImport(Path path);

    void deleteIndex(int index);

    void addPlaceholder();

    void addCopyOfElement(int lastSelectedIndex, String name);

    /**
     * Get a list of all names which should be displayed in the list.
     *
     * @return the name list
     */
    List<String> getNameList();

    /**
     * Get the default name for elements in the list.
     *
     * @return the default name
     */
    String getDefaultName();
}
