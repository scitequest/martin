package com.scitequest.martin.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.scitequest.martin.Const;
import com.scitequest.martin.export.Incubation;

/**
 * GUI for inputting metadata about the incubation processes.
 */
public final class IncubationInput extends JPanel {
    /** List to keep track of all currently added incubations. */
    private final List<IncubationComponent> incubationPanels = new ArrayList<>();
    /** The panel which holds all the incubation components and spacers. */
    private final JPanel incubationPanelsHolder = new JPanel();

    /** Create a new incubation input. */
    public IncubationInput() {
        super(new BorderLayout());

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 0));

        JButton addIncubationButton = new JButton(Const.bundle.getString("incubationInput.addIncubation.text"));
        addIncubationButton.addActionListener(e -> addIncubation(new IncubationComponent()));
        JButton removeIncubationButton = new JButton(Const.bundle.getString("incubationInput.removeIncubation.text"));
        removeIncubationButton.addActionListener(e -> removeIncubation());
        buttons.add(addIncubationButton);
        buttons.add(removeIncubationButton);

        incubationPanelsHolder.setLayout(new BoxLayout(incubationPanelsHolder, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(incubationPanelsHolder);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(buttons, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addIncubation(IncubationComponent incubationComponent) {
        // Add space between panels if there is already a panel
        if (!incubationPanels.isEmpty()) {
            incubationPanelsHolder.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        incubationPanels.add(incubationComponent);
        incubationPanelsHolder.add(incubationComponent);
        updateGuiElements();
    }

    private void removeIncubation() {
        if (incubationPanels.isEmpty()) {
            return;
        }
        // Remove the last incubation panel
        incubationPanelsHolder.remove(incubationPanelsHolder.getComponentCount() - 1);
        // If there was more than one incubation we have to remove the space
        if (incubationPanels.size() > 1) {
            incubationPanelsHolder.remove(incubationPanelsHolder.getComponentCount() - 1);
        }
        // Remove the panel from our own tracking list
        incubationPanels.remove(incubationPanels.size() - 1);
        updateGuiElements();
    }

    private void updateGuiElements() {
        validate();
        repaint();
    }

    /**
     * Get the incubation metadata entered by the user.
     *
     * @return the incubation metadata
     */
    public List<Incubation> getMetadata() {
        List<Incubation> l = new ArrayList<>();
        this.incubationPanels.stream()
                .map(panel -> panel.getMetadata())
                .forEach(metadata -> l.add(metadata));
        return l;
    }

    /**
     * This method loads all incubations of a project.
     *
     * @param incubations list of incubation-instances of a set project.
     */
    public void setIncubations(List<Incubation> incubations) {
        emptyIncubationInput();
        for (Incubation incubation : incubations) {
            IncubationComponent iComp = new IncubationComponent();
            iComp.setFromMetadata(incubation);
            addIncubation(iComp);
        }
    }

    /**
     * Returns the content of Incubation input as a list of incubation metadata.
     *
     * @return list filled with instances of incubation metadata.
     * @throws IllegalArgumentException if the current input constitutes invalid
     *                                  incubation metadata
     */
    public List<Incubation> getIncubations() throws IllegalArgumentException {
        return incubationPanels.stream()
                .map(panel -> panel.getMetadata())
                .collect(Collectors.toList());
    }

    /**
     * Empties the incubationInputPanel.
     */
    public void emptyIncubationInput() {
        while (incubationPanels.size() > 0) {
            removeIncubation();
        }
    }
}
