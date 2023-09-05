package com.scitequest.martin.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Duration;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.scitequest.martin.Const;
import com.scitequest.martin.export.Incubation;
import com.scitequest.martin.export.Quantity;
import com.scitequest.martin.export.Quantity.Unit;

public final class IncubationComponent extends JPanel {

    private static final double MAX_INCUBATION_VALUE = 1.0e12d;

    private final JTextField solution = new JTextField();
    private final SpinnerNumberModel stockConcentration;
    private final JComboBox<Unit> stockConcentrationUnit;
    private final SpinnerNumberModel finalConcentration;
    private final JComboBox<Unit> finalConcentrationUnit;
    private final SpinnerNumberModel incubationTime;

    public IncubationComponent() {
        super(new GridBagLayout());

        ((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 0, 0, 0, 0 };
        ((GridBagLayout) getLayout()).columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0E-4 };
        ((GridBagLayout) getLayout()).rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

        Color background = UIManager.getColor("Menu.background");
        JPanel spacer = new JPanel();
        spacer.setBackground(background);

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBackground(background);

        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0, 0 };
        ((GridBagLayout) getLayout()).rowHeights = new int[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ((GridBagLayout) getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
        ((GridBagLayout) getLayout()).rowWeights = new double[] {
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

        // ---- solutionLabel ----
        JLabel solutionLabel = new JLabel();
        solutionLabel.setText(Const.bundle.getString("incubationComponent.solutionLabel.text"));
        solutionLabel.putClientProperty("FlatLaf.styleClass", "h4");
        add(solutionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- solution ----
        solution.putClientProperty("JTextField.placeholderText", "Solution");
        solution.putClientProperty("JTextField.showClearButton", true);
        add(solution, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- solutionDescription ----
        JTextArea solutionDescription = new JTextArea();
        solutionDescription.setText(
                Const.bundle.getString("incubationComponent.solutionDescription.text"));
        solutionDescription.setLineWrap(true);
        solutionDescription.setWrapStyleWord(true);
        solutionDescription.setEditable(false);
        solutionDescription.setEnabled(false);
        solutionDescription.setOpaque(false);
        add(solutionDescription, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));
        add(spacer, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- stockConcentrationLabel ----
        JLabel stockConcentrationLabel = new JLabel();
        stockConcentrationLabel.setText(
                Const.bundle.getString("incubationComponent.stockConcentrationLabel.text"));
        stockConcentrationLabel.putClientProperty("FlatLaf.styleClass", "h4");
        add(stockConcentrationLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- stockConcentration ----
        stockConcentration = new SpinnerNumberModel(0.0, 0.0, MAX_INCUBATION_VALUE, 1.0);
        JSpinner stockConcentrationSpinner = new JSpinner(stockConcentration);
        add(stockConcentrationSpinner, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- stockConcentrationUnit ----
        stockConcentrationUnit = new JComboBox<>(Unit.values());
        add(stockConcentrationUnit, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- finalConcentrationLabel ----
        JLabel finalConcentrationLabel = new JLabel();
        finalConcentrationLabel.setText(
                Const.bundle.getString("incubationComponent.finalConcentrationLabel.text"));
        finalConcentrationLabel.putClientProperty("FlatLaf.styleClass", "h4");
        add(finalConcentrationLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- finalConcentration ----
        finalConcentration = new SpinnerNumberModel(0.0, 0.0, MAX_INCUBATION_VALUE, 1.0);
        JSpinner finalConcentrationSpinner = new JSpinner(finalConcentration);
        add(finalConcentrationSpinner, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
        // ---- finalConcentrationUnit ---
        finalConcentrationUnit = new JComboBox<>(Unit.values());
        add(finalConcentrationUnit, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        add(spacer, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- incubationTimeLabel ----
        JLabel incubationTimeLabel = new JLabel();
        incubationTimeLabel.setText(
                Const.bundle.getString("incubationComponent.incubationTimeLabel.text"));
        incubationTimeLabel.putClientProperty("FlatLaf.styleClass", "h4");
        add(incubationTimeLabel, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- incubationTime ----
        incubationTime = new SpinnerNumberModel(3600, 0, Integer.MAX_VALUE, 15);
        JSpinner incubationTimeSpinner = new JSpinner(incubationTime);
        add(incubationTimeSpinner, new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- incubationTimeDescription ----
        JTextArea incubationTimeDescription = new JTextArea();
        incubationTimeDescription.setText(
                Const.bundle.getString("incubationComponent.incubationTimeDescription.text"));
        incubationTimeDescription.setLineWrap(true);
        incubationTimeDescription.setWrapStyleWord(true);
        incubationTimeDescription.setEditable(false);
        incubationTimeDescription.setEnabled(false);
        incubationTimeDescription.setOpaque(false);
        add(incubationTimeDescription, new GridBagConstraints(0, 11, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));
    }

    /**
     * Collect all information into the incubation metadata.
     *
     * @return all currently set incubation metadata
     * @throws IllegalArgumentException if the current input constitutes invalid
     *                                  incubation metadata
     */
    public Incubation getMetadata() throws IllegalArgumentException {
        return Incubation.of(
                solution.getText(),
                Quantity.of(stockConcentration.getNumber().doubleValue(),
                        (Unit) stockConcentrationUnit.getSelectedItem()),
                Quantity.of(finalConcentration.getNumber().doubleValue(),
                        (Unit) finalConcentrationUnit.getSelectedItem()),
                Duration.ofSeconds(incubationTime.getNumber().intValue()));
    }

    /**
     * This method loads an incubation into the elements of IncubationComponent.
     *
     * @param incubation object containing all informations about an incubation.
     */
    public void setFromMetadata(Incubation incubation) {
        solution.setText(incubation.getSolution());
        stockConcentration.setValue(incubation.getStockConcentration().getValue());
        stockConcentrationUnit.setSelectedItem(incubation.getStockConcentration().getUnit());
        finalConcentration.setValue(incubation.getFinalConcentration().getValue());
        finalConcentrationUnit.setSelectedItem(incubation.getFinalConcentration().getUnit());
        incubationTime.setValue(incubation.getIncubationTime().getSeconds());
    }
}
