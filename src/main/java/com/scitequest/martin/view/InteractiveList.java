package com.scitequest.martin.view;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;

import com.scitequest.martin.Const;
import com.scitequest.martin.settings.exception.DuplicateElementException;
import com.scitequest.martin.settings.exception.OneElementRequiredException;

public final class InteractiveList extends JPanel {

    /** The logger for this class. */
    private static final Logger log = Logger.getLogger(
            "com.scitequest.martin.view.InteractiveList");
    /** The part to append when an item is copied. */
    public static final String COPY_LABEL = " (Copy)";

    private final ListDialog ownerDialog;

    private final JList<String> list;
    private final NameListModel listModel;

    /** -1 if no previous element exists (for example due to deletion). */
    private int prevIndex = -1;
    /**
     * Flag to selectively ignore selection events.
     *
     * We can't use setValueIsAdjusting for this because {@code setValueIsAdjusting}
     * triggers a event by itself ...
     */
    private boolean selectionEventsEnabled = true;

    private final JScrollPane listScroller;
    /**
     * This button will create a blank new project.
     */
    private final JButton addButton = new JButton();
    /**
     * This button will create a new list element pre-filled with information
     * of the currently selected list element.
     */
    private final JButton copyButton = new JButton();
    /**
     * This button will delete the currently selected list element.
     */
    private final JButton deleteButton = new JButton();
    /**
     * This button allows the user to import list contents.
     */
    private final JButton importButton = new JButton();
    /**
     * This button allows the user to export list contents.
     */
    private final JButton exportButton = new JButton();

    InteractiveList(ListDialog ownerDialog) {
        super();

        this.ownerDialog = ownerDialog;

        this.setLayout(new BorderLayout());

        listModel = new NameListModel(ownerDialog);
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(e -> handleSelectionChanged(e));

        listScroller = new JScrollPane(list);
        this.add(listScroller, BorderLayout.CENTER);

        // ======== actionPanel ========
        JPanel actionPanel = new JPanel();
        actionPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        actionPanel.setLayout(new GridLayout(0, 1, 5, 5));
        add(actionPanel, BorderLayout.SOUTH);

        // ======== modificationPanel ========
        JPanel modificationPanel = new JPanel();
        modificationPanel.setLayout(new GridLayout(1, 0, 5, 5));

        addButton.setText(
                Const.bundle.getString("interactiveList.addButton.text"));
        addButton.addActionListener(e -> handleAddButtonPressed());
        modificationPanel.add(addButton);

        copyButton.setText(
                Const.bundle.getString("interactiveList.copyButton.text"));
        copyButton.addActionListener(e -> handleCopyButtonPressed());
        modificationPanel.add(copyButton);

        deleteButton.setText(
                Const.bundle.getString("interactiveList.deleteButton.text"));
        deleteButton.addActionListener(e -> handleDeleteButtonPressed());
        modificationPanel.add(deleteButton);
        actionPanel.add(modificationPanel);

        // ======== importExportPanel ========
        JPanel importExportPanel = new JPanel();
        importExportPanel.setLayout(new GridLayout(1, 0, 5, 5));

        importButton.setText(
                Const.bundle.getString("interactiveList.importButton.text"));
        importButton.addActionListener(e -> handleImportButtonPressed());
        importExportPanel.add(importButton);

        exportButton.setText(
                Const.bundle.getString("interactiveList.exportButton.text"));
        exportButton.addActionListener(e -> handleExportButtonPressed());
        importExportPanel.add(exportButton);
        actionPanel.add(importExportPanel);
    }

    private void handleSelectionChanged(ListSelectionEvent e) {
        // Guard to ignore events if we are still adjusting the selection
        if (e.getValueIsAdjusting() || !selectionEventsEnabled) {
            return;
        }
        // Guard to just return if nothing is selected
        if (list.isSelectionEmpty()) {
            return;
        }
        // If no previous element was selected, just load
        if (prevIndex == -1) {
            loadSelection(list.getSelectedIndex());
            return;
        }
        if (!validateAndResetIfInvalid(prevIndex)) {
            return;
        }

        // The inputs were valid and stored so we load the new selection
        loadSelection(list.getSelectedIndex());
    }

    private boolean validateAndResetIfInvalid(int index) {
        // Validate and store current entries before loading the new selection.
        if (!ownerDialog.validateInputs(index)) {
            // Invalid inputs so we load the previous index
            loadSelection(index);
            // We also have to update the selection index but WITHOUT triggering a new event
            // We do that by saying the value is still adjusting which we catch in the guard
            // above
            selectionEventsEnabled = false;
            list.setSelectedIndex(index);
            selectionEventsEnabled = true;
            // Validation failed
            return false;
        }
        // Validation succeeded
        return true;
    }

    private void loadSelection(int idx) {
        ownerDialog.populateInputs(idx);
        prevIndex = idx;
    }

    /**
     * Initiates export of the currently selected list element.
     * Since data-format is very specific, this is handled by the respective
     * ownerDialogs.
     */
    private void handleExportButtonPressed() {
        log.config("Asking user to select a file to export");
        String prefill = list.getSelectedValue();
        Optional<Path> path = GuiUtils.chooseJsonFile((JDialog) ownerDialog, FileDialog.SAVE, prefill);
        if (path.isEmpty()) {
            log.config("Export has been canceled by user");
            return;
        }

        ownerDialog.handleExport(list.getSelectedIndex(), path.get());
    }

    private void handleImportButtonPressed() {
        log.config("Asking user to select a file to import");
        Optional<Path> path = GuiUtils.chooseJsonFile((JDialog) ownerDialog, FileDialog.LOAD);
        if (path.isEmpty()) {
            log.config("Import has been canceled by user");
            return;
        }
        try {
            ownerDialog.handleImport(path.get());
        } catch (DuplicateElementException e) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.duplicateListElement.text"),
                    Const.bundle.getString("messageDialog.duplicateListElement.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        update();
    }

    private void handleDeleteButtonPressed() {
        int index = list.getSelectedIndex();
        if (index < 0) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                String.format(Const.bundle.getString("interactiveList.deleteElement.text"),
                        listModel.getElementAt(index)),
                Const.bundle.getString("interactiveList.deleteElement.title"),
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.NO_OPTION) {
            return;
        }

        try {
            ownerDialog.deleteIndex(index);
        } catch (OneElementRequiredException e) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.listEmptyNotAllowed.text"),
                    Const.bundle.getString("messageDialog.listEmptyNotAllowed.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Before we select a new item we have to set the prevIndex to -1, otherwise we
        // would validate the deleted mask.
        prevIndex = -1;
        // Clamp the new index to a valid one
        int clampedIndex = Math.max(0, Math.min(index, listModel.getSize() - 1));
        // Select the nearest item
        list.setSelectedIndex(clampedIndex);
        // This is stupid, we can't just set the selected index to trigger a selection
        // changed event because the index did not change. Thus, we have to manually
        // call it
        if (clampedIndex == list.getSelectedIndex()) {
            handleSelectionChanged(new ListSelectionEvent(this, clampedIndex, clampedIndex, false));
        }
        // Finally we tell our list to fetch the new data
        update();
    }

    private void handleCopyButtonPressed() {
        int index = list.getSelectedIndex();
        if (index < 0) {
            return;
        }
        // We have to validate inputs before we attempt to add a placeholder
        if (!validateAndResetIfInvalid(index)) {
            return;
        }

        String currName = listModel.getElementAt(index);
        if (currName.contains(COPY_LABEL)) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.multipleCopiesNotAllowed.text"),
                    Const.bundle.getString("messageDialog.multipleCopiesNotAllowed.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        /*
         * Removes trailing letters in order to make space for the
         * copy label if necessary.
         */
        int overhang = Math.max(0,
                currName.length() + COPY_LABEL.length() - ListDialog.CHAR_LIMIT_NAME);
        String name = currName.substring(0, currName.length() - overhang) + COPY_LABEL;

        try {
            ownerDialog.addCopyOfElement(index, name);
        } catch (DuplicateElementException e) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.copyDuplicatesNotAllowed.text"),
                    Const.bundle.getString("messageDialog.copyDuplicatesNotAllowed.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        update();
    }

    private void handleAddButtonPressed() {
        int index = list.getSelectedIndex();
        if (index < 0) {
            return;
        }
        // We have to validate inputs before we attempt to add a placeholder
        if (!ownerDialog.validateInputs(index)) {
            return;
        }

        try {
            ownerDialog.addPlaceholder();
        } catch (DuplicateElementException e) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.doubleDefaultNotAllowed.text"),
                    Const.bundle.getString("messageDialog.doubleDefaultNotAllowed.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        update();
    }

    /**
     * Notifies this interactive list that the data to represent has changed.
     */
    public void update() {
        listModel.updateData();
    }

    /**
     * Sets the list item to be selected and update the list.
     *
     * @param index the index to be selected
     */
    public void select(int index) {
        list.setSelectedIndex(index);
    }

    /**
     * Return the index of the item that is currently selected the list.
     *
     * @return the selected index
     */
    public int getSelectedIndex() {
        // If the list selection is in progress, the NameListModel gets the name list.
        // However the name list replaces the selected element with the content of the
        // name text field.
        // To prevent setting the wrong text field whilest the selection changes, we
        // must not use getSelectedIndex.
        if (list.getValueIsAdjusting()) {
            return prevIndex;
        }
        return list.getSelectedIndex();
    }
}
