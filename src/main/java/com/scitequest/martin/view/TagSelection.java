package com.scitequest.martin.view;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class TagSelection extends JPanel {

    private final JPanel tagContainer = new JPanel();
    private final List<JCheckBox> checkboxes = new ArrayList<>();

    private TagSelection() {
        var scrollPane = new JScrollPane();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        tagContainer.setLayout(new WrapLayout(FlowLayout.LEFT));

        scrollPane.setViewportView(tagContainer);
        add(scrollPane);
    }

    public static TagSelection empty() {
        return new TagSelection();
    }

    public void clearTags() {
        checkboxes.clear();
        tagContainer.removeAll();
        update();
    }

    public void setTags(Set<String> tags) {
        // Remove existing tags
        clearTags();
        // Add new tags
        tags.stream()
                .sorted()
                .map(tag -> new JCheckBox(tag))
                .forEach(checkbox -> checkboxes.add(checkbox));
        for (var checkbox : checkboxes) {
            tagContainer.add(checkbox);
        }
        update();
    }

    public void setTagSelected(String tag, boolean selected) {
        checkboxes.stream()
                .filter(checkbox -> tag.equals(checkbox.getText()))
                .findFirst()
                .ifPresent(checkbox -> checkbox.setSelected(selected));
    }

    public Set<String> getSelectedTags() {
        Set<String> selectedTags = new HashSet<>();
        for (var checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                selectedTags.add(checkbox.getText());
            }
        }
        return selectedTags;
    }

    private void update() {
        validate();
        repaint();
    }
}
