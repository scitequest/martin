package com.scitequest.martin.view;

import javax.swing.AbstractListModel;

/**
 * This list model is used to display a name list.
 */
public final class NameListModel extends AbstractListModel<String> {

    /** Reference to the dialog to provide the name list from. */
    private final ListDialog dialog;

    /**
     * Create a new name list model based on the provided dialog its data.
     *
     * @param dialog the dialog
     */
    public NameListModel(ListDialog dialog) {
        super();
        this.dialog = dialog;
    }

    @Override
    public String getElementAt(int index) {
        return dialog.getNameList().get(index);
    }

    @Override
    public int getSize() {
        return dialog.getNameList().size();
    }

    /**
     * Tell this model that the data has changed.
     *
     * This causes the list to update its contents.
     */
    public void updateData() {
        fireContentsChanged(this, 0, getSize() - 1);
    }
}
