package com.scitequest.martin.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.scitequest.martin.Const;
import com.scitequest.martin.export.Patient;

public final class TagInput extends JPanel {

    private static final Border NORMAL_TEXTFIELD_BORDER = UIManager
            .getLookAndFeel().getDefaults().getBorder("TextField.border");

    private final SortedSet<String> tags = new TreeSet<>();
    private final JTextField tagInput = new JTextField();
    private final JPanel tagPanel = new JPanel();
    private final JButton addButton = new JButton();

    private TagInput() {
        super();

        setLayout(new BorderLayout());

        // ======== entryPanel ========
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));

        // ---- tagInput ----
        tagInput.putClientProperty("JTextField.showClearButton", true);
        tagInput.putClientProperty("JTextField.placeholderText", "example-tag");
        tagInput.addActionListener(l -> handleAddTag());
        tagInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                handleTextFieldChanged("warning");
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                handleTextFieldChanged("error");
            }
        });
        tagInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                handleTextFieldChanged("warning");
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                handleTextFieldChanged("warning");
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                handleTextFieldChanged("warning");
            }
        });
        entryPanel.add(tagInput);

        // ---- addButton ----
        addButton.setText(Const.bundle.getString("tagInput.addButton.text"));
        addButton.setBackground(UIManager.getColor("Actions.Blue"));
        addButton.setForeground(SystemColor.text);
        addButton.setEnabled(false);
        addButton.addActionListener(l -> handleAddTag());
        entryPanel.add(addButton);

        add(entryPanel, BorderLayout.NORTH);

        // ======== scrollPane ========
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // ======== tagPanel ========
        tagPanel.putClientProperty("JComponent.roundRect", false);
        tagPanel.setLayout(new WrapLayout(FlowLayout.LEFT));

        scrollPane.setViewportView(tagPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static TagInput fromTags(Set<String> tags) {
        TagInput input = new TagInput();
        if (tags != null) {
            input.setTags(tags);
        }
        return input;
    }

    public static TagInput empty() {
        return new TagInput();
    }

    public void setTags(Set<String> newTags) {
        tags.clear();
        tags.addAll(newTags);
        rebuildList();
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    private void rebuildList() {
        tagPanel.removeAll();
        tags.stream().forEach(tag -> tagPanel.add(createTag(tag)));
        tagPanel.validate();
        tagPanel.revalidate();
        tagPanel.repaint();
    }

    private JTextField createTag(String tag) {
        JTextField tagTextField = new JTextField();
        tagTextField.putClientProperty("JComponent.roundRect", true);
        tagTextField.putClientProperty("JTextField.showClearButton", true);
        tagTextField.setFocusable(false);
        tagTextField.putClientProperty("JComponent.minimumWidth", 0);
        tagTextField.putClientProperty("JTextField.padding", new Insets(0, 5, 0, -5));
        tagTextField.putClientProperty("JTextField.clearCallback",
                (Runnable) () -> {
                    SwingUtilities.invokeLater(() -> {
                        tags.remove(tagTextField.getText());
                        tagPanel.remove(tagTextField);
                        tagPanel.validate();
                        tagPanel.revalidate();
                        tagPanel.repaint();
                    });
                });
        tagTextField.setFont(UIManager.getFont("monospaced.font"));
        tagTextField.setText(tag);
        return tagTextField;
    }

    private void handleTextFieldChanged(String outline) {
        // If text field is empty, disable button but don't show a special border
        String input = tagInput.getText();
        if (input.isEmpty()) {
            tagInput.setBorder(NORMAL_TEXTFIELD_BORDER);
            addButton.setEnabled(false);
            return;
        }
        // If the input is not a valid tag or already exists as tag disable button, but
        // set a warning border
        String tag = input.trim();
        if (!Patient.isValidTag(tag)
                || tags.contains(tag)) {
            tagInput.putClientProperty("JComponent.outline", outline);
            addButton.setEnabled(false);
            return;
        }

        // Tag is valid, enable the add button and show no special border
        tagInput.putClientProperty("JComponent.outline", null);
        addButton.setEnabled(true);
    }

    private void handleAddTag() {
        String input = tagInput.getText();
        if (input.isEmpty()) {
            return;
        }
        String tag = input.trim();
        if (!Patient.isValidTag(tag)) {

            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.invalidTag.text"),
                    Const.bundle.getString("messageDialog.invalidTag.title"),
                    JOptionPane.WARNING_MESSAGE);
            tagInput.putClientProperty("JComponent.outline", "error");
            return;
        }

        if (tags.add(tag)) {
            rebuildList();
            tagInput.setText("");
        }
    }
}
