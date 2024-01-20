package com.scitequest.martin.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.scitequest.martin.Const;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.settings.MaskExt;
import com.scitequest.martin.settings.MaskSettings;
import com.scitequest.martin.settings.MaskSettings.MeasureShape;
import com.scitequest.martin.settings.Settings;
import com.scitequest.martin.settings.exception.DuplicateElementException;

public final class MaskDesigner extends JDialog implements ListDialog {

    /** The logger for this class. */
    private static final Logger log = Logger.getLogger(
            "com.scitequest.martin.view.SlideMaskDesignerGui");

    private final Controlable control;
    private final Settings settings;
    private final MaskSettings maskSettings;
    private final SettingsGui settingsGui;

    private final JLabel maskNameLabel = new JLabel();
    private final JTextField maskName = new JTextField();
    private final JLabel maskDescriptionLabel = new JLabel();
    private final JTextArea maskDescription = new JTextArea();

    private final JPanel sidePanel = new JPanel();
    private final JPanel actionPanel = new JPanel();

    private final SpinnerModel maskWidth;
    private final SpinnerModel maskHeight;

    private final SpinnerModel xInset;
    private final SpinnerModel yInset;

    private final SpinnerModel superGridRows;
    private final SpinnerModel superGridColumns;

    private final JCheckBox uniformIntersections = new JCheckBox();
    private final JComboBox<String> intersections = new JComboBox<>();
    private final SpinnerModel horizontalSpacing;
    private final JSpinner horizontalSpacingSpinner;
    private final SpinnerModel verticalSpacing;
    private final JSpinner verticalSpacingSpinner;

    private final SpinnerModel spotfieldHeight;
    private final SpinnerModel spotfieldWidth;

    private final SpinnerModel spotfieldRows;
    private final SpinnerModel spotfieldColumns;

    private final JComboBox<String> shape;
    private final JSpinner spotWidthSpinner;
    private final JSpinner spotHeightSpinner;
    private final SpinnerModel spotWidth;
    private final SpinnerModel spotHeight;

    private final InteractiveList interactiveList;

    private final JButton applyButton = new JButton();
    private final JButton closeButton = new JButton();

    /**
     * Set if the change listeners are active
     *
     * <p>
     * This is used to disable the listeners while we are modifying GUI elements
     * that would trigger new events while we want to supress them.
     * </p>
     */
    private boolean activeListener = false;
    /**
     * Set to true while a mask is validating.
     *
     * <p>
     * This is used to <i>not</i> replace the selected masks name with the text
     * field input. This mitigates the case if there are three masks. The name text
     * field is set to the name of mask B and then switched to mask C. Without this
     * flag we would briefly replace the name of mask C with the text field input
     * from mask A during the time the warning dialog is displayed.
     * </p>
     *
     * It is not recommended to use this flag for other cases.
     */
    private boolean validating = false;

    private List<Integer> horizontalSpacings = new ArrayList<Integer>();
    private List<Integer> verticalSpacings = new ArrayList<Integer>();
    public static final int STANDARD_SPACING = 50;

    public MaskDesigner(Controlable control, Settings settings,
            SettingsGui optionsGui) {
        super(optionsGui, "Mask Designer", false);

        setLayout(new BorderLayout());
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        this.control = control;
        this.settings = settings;
        this.maskSettings = settings.getMaskSettings();
        this.settingsGui = optionsGui;

        // ======== maskPanel ========
        JPanel maskPanel = new JPanel();
        maskPanel.setLayout(new BoxLayout(maskPanel, BoxLayout.X_AXIS));

        // ======== maskPanel1 ========
        JPanel maskPanel1 = new JPanel();
        maskPanel1.setBorder(new EmptyBorder(5, 5, 5, 5));
        maskPanel1.setLayout(new GridBagLayout());
        ((GridBagLayout) maskPanel1.getLayout()).columnWidths = new int[] { 175, 170, 0 };
        ((GridBagLayout) maskPanel1.getLayout()).rowHeights = new int[] {
                0, 0, 0, 0, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ((GridBagLayout) maskPanel1.getLayout()).columnWeights = new double[] { 1.0, 1.0, 1.0E-4 };
        ((GridBagLayout) maskPanel1.getLayout()).rowWeights = new double[] {
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 1.0E-4 };

        // ---- maskInformationHeader ----
        JLabel maskInformationHeader = new JLabel();
        maskInformationHeader.setText(
                Const.bundle.getString("maskDesigner.maskInformationHeader.text"));
        maskInformationHeader.putClientProperty("FlatLaf.styleClass", "h2");
        maskPanel1.add(maskInformationHeader, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

        // ---- maskNameLabel ----
        maskNameLabel.setText(Const.bundle.getString("maskDesigner.maskNameLabel.text"));
        maskNameLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(maskNameLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- maskName ----
        maskName.putClientProperty("JTextField.showClearButton", true);
        maskName.putClientProperty("JTextField.placeholderText", "CelluSpots");
        maskName.setDocument(new InputLimitations(CHAR_LIMIT_NAME,
                true, true, true, CHAR_EXCEPTIONS_NAME));
        maskName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                if (activeListener) {
                    interactiveList.update();
                }
            }
        });
        maskPanel1.add(maskName, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- maskDescriptionLabel ----
        maskDescriptionLabel.setText(
                Const.bundle.getString("maskDesigner.maskDescriptionLabel.text"));
        maskDescriptionLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(maskDescriptionLabel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- maskDescription ----
        maskDescription.setLineWrap(true);
        maskDescription.setWrapStyleWord(true);
        maskDescription.setDocument(new InputLimitations(CHAR_LIMIT_DESCRIPTION,
                true, true, true, CHAR_EXCEPTIONS_DESCRIPTION));

        // ======== maskDescriptionScrollPane ========
        JScrollPane maskDescriptionScrollPane = new JScrollPane();
        maskDescriptionScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        maskDescriptionScrollPane.setViewportView(maskDescription);
        maskPanel1.add(maskDescriptionScrollPane, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- maskHeader ----
        JLabel maskHeader = new JLabel();
        maskHeader.setText(Const.bundle.getString("maskDesigner.maskHeader.text"));
        maskHeader.putClientProperty("FlatLaf.styleClass", "h2");
        maskPanel1.add(maskHeader, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 0), 0, 0));

        // ---- maskSectionDescription ----
        JTextArea maskSectionDescription = new JTextArea();
        maskSectionDescription.setOpaque(false);
        maskSectionDescription.setText(
                Const.bundle.getString("maskDesigner.maskSectionDescription.text"));
        maskSectionDescription.setEditable(false);
        maskSectionDescription.setEnabled(false);
        maskSectionDescription.setWrapStyleWord(true);
        maskSectionDescription.setLineWrap(true);
        maskPanel1.add(maskSectionDescription, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- widthLabel ----
        JLabel widthLabel = new JLabel();
        widthLabel.setText(Const.bundle.getString("maskDesigner.widthLabel.text"));
        widthLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(widthLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- heightLabel ----
        JLabel heightLabel = new JLabel();
        heightLabel.setText(Const.bundle.getString("maskDesigner.heightLabel.text"));
        heightLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(heightLabel, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- maskWidth ----
        maskWidth = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner maskWidthSpinner = new JSpinner(maskWidth);
        maskWidthSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) maskWidthSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel1.add(maskWidthSpinner, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- maskHeight ----
        maskHeight = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner maskHeightSpinner = new JSpinner(maskHeight);
        maskHeightSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) maskHeightSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel1.add(maskHeightSpinner, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- xInsetLabel ----
        JLabel xInsetLabel = new JLabel();
        xInsetLabel.setText(Const.bundle.getString("maskDesigner.xInsetLabel.text"));
        xInsetLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(xInsetLabel, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- yInsetLabel ----
        JLabel yInsetLabel = new JLabel();
        yInsetLabel.setText(Const.bundle.getString("maskDesigner.yInsetLabel.text"));
        yInsetLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(yInsetLabel, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- xInset ----
        xInset = new SpinnerNumberModel(1, 0, 10000, 1);
        JSpinner xInsetSpinner = new JSpinner(xInset);
        xInsetSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) xInsetSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel1.add(xInsetSpinner, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- yInset ----
        yInset = new SpinnerNumberModel(1, 0, 10000, 1);
        JSpinner yInsetSpinner = new JSpinner(yInset);
        yInsetSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) yInsetSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel1.add(yInsetSpinner, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- measureFieldHeader ----
        JLabel measureFieldHeader = new JLabel();
        measureFieldHeader.setText(Const.bundle.getString("maskDesigner.measureFieldHeader.text"));
        measureFieldHeader.putClientProperty("FlatLaf.styleClass", "h2");
        maskPanel1.add(measureFieldHeader, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 5), 0, 0));

        // ---- measureFieldDescription ----
        JTextArea measureFieldDescription = new JTextArea();
        measureFieldDescription.setOpaque(false);
        measureFieldDescription.setText(
                Const.bundle.getString("maskDesigner.measureFieldDescription.text"));
        measureFieldDescription.setEditable(false);
        measureFieldDescription.setEnabled(false);
        measureFieldDescription.setLineWrap(true);
        measureFieldDescription.setWrapStyleWord(true);
        maskPanel1.add(measureFieldDescription, new GridBagConstraints(0, 12, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- shapeLabel ----
        JLabel shapeLabel = new JLabel();
        shapeLabel.setText(Const.bundle.getString("maskDesigner.shapeLabel.text"));
        shapeLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(shapeLabel, new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- shape ----
        String[] measureFieldShapes = {
                MeasureShape.CIRCLE.toString(),
                MeasureShape.RECTANGLE.toString(),
                MeasureShape.DIAMOND.toString() };
        shape = new JComboBox<>(measureFieldShapes);
        shape.addActionListener(l -> handleMaskChanged());
        maskPanel1.add(shape, new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- shapeDescription ----
        JTextArea shapeDescription = new JTextArea();
        shapeDescription.setOpaque(false);
        shapeDescription.setText(Const.bundle.getString("maskDesigner.shapeDescription.text"));
        shapeDescription.setEditable(false);
        shapeDescription.setEnabled(false);
        shapeDescription.setLineWrap(true);
        shapeDescription.setWrapStyleWord(true);
        maskPanel1.add(shapeDescription, new GridBagConstraints(0, 15, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- spotWidthLabel ----
        JLabel spotWidthLabel = new JLabel();
        spotWidthLabel.setText(Const.bundle.getString("maskDesigner.spotWidthLabel.text"));
        spotWidthLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(spotWidthLabel, new GridBagConstraints(0, 16, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- spotHeightLabel ----
        JLabel spotHeightLabel = new JLabel();
        spotHeightLabel.setText(Const.bundle.getString("maskDesigner.spotHeightLabel.text"));
        spotHeightLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel1.add(spotHeightLabel, new GridBagConstraints(1, 16, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- spotWidth ----
        spotWidth = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        spotWidthSpinner = new JSpinner(spotWidth);
        spotWidthSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) spotWidthSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel1.add(spotWidthSpinner, new GridBagConstraints(0, 17, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- spotHeight ----
        spotHeight = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        spotHeightSpinner = new JSpinner(spotHeight);
        spotHeightSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) spotHeightSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel1.add(spotHeightSpinner, new GridBagConstraints(1, 17, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        maskPanel.add(maskPanel1);

        // ======== maskPanel2 ========
        JPanel maskPanel2 = new JPanel();
        maskPanel2.setBorder(new EmptyBorder(5, 5, 5, 5));
        maskPanel2.setLayout(new GridBagLayout());
        ((GridBagLayout) maskPanel2.getLayout()).columnWidths = new int[] { 175, 170, 0 };
        ((GridBagLayout) maskPanel2.getLayout()).rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0,
                0, 0, 0, 0, 0 };
        ((GridBagLayout) maskPanel2.getLayout()).columnWeights = new double[] { 1.0, 1.0, 1.0E-4 };
        ((GridBagLayout) maskPanel2.getLayout()).rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

        // ---- superGridHeader ----
        JLabel superGridHeader = new JLabel();
        superGridHeader.setText(Const.bundle.getString("maskDesigner.superGridHeader.text"));
        superGridHeader.putClientProperty("FlatLaf.styleClass", "h2");
        maskPanel2.add(superGridHeader, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

        // ---- superGridDescription ----
        JTextArea superGridDescription = new JTextArea();
        superGridDescription.setOpaque(false);
        superGridDescription.setText(
                Const.bundle.getString("maskDesigner.superGridDescription.text"));
        superGridDescription.setEditable(false);
        superGridDescription.setEnabled(false);
        superGridDescription.setWrapStyleWord(true);
        superGridDescription.setLineWrap(true);
        maskPanel2.add(superGridDescription, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- superGridRowsLabel ----
        JLabel superGridRowsLabel = new JLabel();
        superGridRowsLabel.setText(
                Const.bundle.getString("maskDesigner.superGridRowsLabel.text"));
        superGridRowsLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(superGridRowsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- superGridColumnsLabel ----
        JLabel superGridColumnsLabel = new JLabel();
        superGridColumnsLabel.setText(
                Const.bundle.getString("maskDesigner.superGridColumnsLabel.text"));
        superGridColumnsLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(superGridColumnsLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- rows ----
        superGridRows = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner superGridRowsSpinner = new JSpinner(superGridRows);
        superGridRowsSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) superGridRowsSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(superGridRowsSpinner, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- columns ----
        superGridColumns = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner superGridColumnsSpinner = new JSpinner(superGridColumns);
        superGridColumnsSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) superGridColumnsSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(superGridColumnsSpinner, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        maskPanel2.add(new JPanel(), new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- intersectionsLabel ----
        JLabel intersectionsLabel = new JLabel();
        intersectionsLabel.setText(Const.bundle.getString("maskDesigner.intersectionsLabel.text"));
        intersectionsLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(intersectionsLabel, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- uniformIntersections ----
        uniformIntersections.setText(
                Const.bundle.getString("maskDesigner.uniformIntersections.text"));
        uniformIntersections.addActionListener(e -> handleMaskChanged());
        uniformIntersections.setSelected(false);
        maskPanel2.add(uniformIntersections, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- uniformIntersectionsDescription ----
        JTextArea uniformIntersectionsDescription = new JTextArea(3, 1);
        uniformIntersectionsDescription.setOpaque(false);
        uniformIntersectionsDescription.setText(
                Const.bundle.getString("maskDesigner.uniformIntersectionsDescription.text"));
        uniformIntersectionsDescription.setEditable(false);
        uniformIntersectionsDescription.setEnabled(false);
        uniformIntersectionsDescription.setLineWrap(true);
        uniformIntersectionsDescription.setWrapStyleWord(true);
        maskPanel2.add(uniformIntersectionsDescription, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- intersectionLabel ----
        JLabel intersectionLabel = new JLabel();
        intersectionLabel.setText(Const.bundle.getString("maskDesigner.intersectionLabel.text"));
        intersectionLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(intersectionLabel, new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- intersections
        intersections.addActionListener(e -> handleIntersectionSelected());
        intersections.setModel(new DefaultComboBoxModel<>());
        maskPanel2.add(intersections, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- intersectionDescription ----
        JTextArea intersectionDescription = new JTextArea(4, 1);
        intersectionDescription.setOpaque(false);
        intersectionDescription.setText(
                Const.bundle.getString("maskDesigner.intersectionDescription.text"));
        intersectionDescription.setEditable(false);
        intersectionDescription.setEnabled(false);
        intersectionDescription.setLineWrap(true);
        intersectionDescription.setWrapStyleWord(true);
        maskPanel2.add(intersectionDescription, new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- verticalSpacingLabel ----
        JLabel verticalSpacingLabel = new JLabel();
        verticalSpacingLabel.setText(
                Const.bundle.getString("maskDesigner.verticalSpacingLabel.text"));
        verticalSpacingLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(verticalSpacingLabel, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- horizontalSpacingLabel ----
        JLabel horizontalSpacingLabel = new JLabel();
        horizontalSpacingLabel.setText(
                Const.bundle.getString("maskDesigner.horizontalSpacingLabel.text"));
        horizontalSpacingLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(horizontalSpacingLabel, new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- verticalSpacing ----
        verticalSpacing = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        verticalSpacingSpinner = new JSpinner(verticalSpacing);
        verticalSpacingSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) verticalSpacingSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(verticalSpacingSpinner, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- horizontalSpacing ----
        horizontalSpacing = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        horizontalSpacingSpinner = new JSpinner(horizontalSpacing);
        horizontalSpacingSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) horizontalSpacingSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(horizontalSpacingSpinner, new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- spotfieldHeader ----
        JLabel spotfieldHeader = new JLabel();
        spotfieldHeader.setText(Const.bundle.getString("maskDesigner.spotfieldHeader.text"));
        spotfieldHeader.putClientProperty("FlatLaf.styleClass", "h2");
        maskPanel2.add(spotfieldHeader, new GridBagConstraints(0, 13, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 0), 0, 0));

        // ---- spotfieldDescription ----
        JTextArea spotfieldDescription = new JTextArea();
        spotfieldDescription.setOpaque(false);
        spotfieldDescription.setText(
                Const.bundle.getString("maskDesigner.spotfieldDescription.text"));
        spotfieldDescription.setEditable(false);
        spotfieldDescription.setEnabled(false);
        spotfieldDescription.setWrapStyleWord(true);
        spotfieldDescription.setLineWrap(true);
        maskPanel2.add(spotfieldDescription, new GridBagConstraints(0, 14, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- spotfieldWidthLabel ----
        JLabel spotfieldWidthLabel = new JLabel();
        spotfieldWidthLabel.setText(
                Const.bundle.getString("maskDesigner.spotfieldWidthLabel.text"));
        spotfieldWidthLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(spotfieldWidthLabel, new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- spotfieldHeightLabel ----
        JLabel spotfieldHeightLabel = new JLabel();
        spotfieldHeightLabel.setText(
                Const.bundle.getString("maskDesigner.spotfieldHeightLabel.text"));
        spotfieldHeightLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(spotfieldHeightLabel, new GridBagConstraints(1, 15, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- spotfieldWidth ----
        spotfieldWidth = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner spotfieldWidthSpinner = new JSpinner(spotfieldWidth);
        spotfieldWidthSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) spotfieldWidthSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(spotfieldWidthSpinner, new GridBagConstraints(0, 16, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- spotfieldHeight ----
        spotfieldHeight = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner spotfieldHeightSpinner = new JSpinner(spotfieldHeight);
        spotfieldHeightSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) spotfieldHeightSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(spotfieldHeightSpinner, new GridBagConstraints(1, 16, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- spotfieldRowsLabel ----
        JLabel spotfieldRowsLabel = new JLabel();
        spotfieldRowsLabel.setText(
                Const.bundle.getString("maskDesigner.spotfieldRowsLabel.text"));
        spotfieldRowsLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(spotfieldRowsLabel, new GridBagConstraints(0, 17, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- spotfieldColumnsLabel ----
        JLabel spotfieldColumnsLabel = new JLabel();
        spotfieldColumnsLabel.setText(
                Const.bundle.getString("maskDesigner.spotfieldColumnsLabel.text"));
        spotfieldColumnsLabel.putClientProperty("FlatLaf.styleClass", "h4");
        maskPanel2.add(spotfieldColumnsLabel, new GridBagConstraints(1, 17, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- spotfieldRows ----
        spotfieldRows = new SpinnerNumberModel(1, 1, 10000, 1);
        JSpinner spotfieldRowsSpinner = new JSpinner(spotfieldRows);
        spotfieldRowsSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) spotfieldRowsSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(spotfieldRowsSpinner, new GridBagConstraints(0, 18, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

        // ---- spotfieldColumns ----
        spotfieldColumns = new SpinnerNumberModel(1, 1, 10000, 1);
        JSpinner spotfieldColumnsSpinner = new JSpinner(spotfieldColumns);
        spotfieldColumnsSpinner.addChangeListener(l -> handleMaskChanged());
        ((JSpinner.DefaultEditor) spotfieldColumnsSpinner.getEditor())
                .getTextField()
                .setColumns(2);
        maskPanel2.add(spotfieldColumnsSpinner, new GridBagConstraints(1, 18, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        maskPanel.add(maskPanel2);
        add(maskPanel, BorderLayout.CENTER);

        // ======== sidePanel ========
        sidePanel.setLayout(new BorderLayout());
        interactiveList = new InteractiveList(this);
        sidePanel.add(interactiveList, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        // ======== actionPanel ========
        actionPanel.setLayout(new FlowLayout());

        // ---- applyButton ----
        applyButton.setText(Const.bundle.getString("maskDesigner.applyButton.text"));
        applyButton.setForeground(SystemColor.text);
        applyButton.addActionListener(l -> handleApplyButtonPressed());
        actionPanel.add(applyButton);

        // ---- closeButton ----
        closeButton.setText(Const.bundle.getString("maskDesigner.closeButton.text"));
        closeButton.setForeground(SystemColor.text);
        closeButton.addActionListener(l -> closeWindow());
        actionPanel.add(closeButton);
        add(actionPanel, BorderLayout.SOUTH);

        this.activeListener = true;
        interactiveList.select(maskSettings.getLastUsedMaskIndex());

        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                SwingUtilities.invokeLater(() -> resetComponentProperties());
            }
        });
        resetComponentProperties();

        setVisible(true);
        pack();
    }

    private void handleMaskChanged() {
        if (activeListener) {
            activeListener = false;

            // Get the current selected index
            int index = interactiveList.getSelectedIndex();
            // Update the measure shape spinners based on the mask its shape
            synchronizeMeasureShapeInformation(index);
            // Finally try to validate the mask.
            if (!validateAndStoreInputsInSettings(index)) {
                // The new inputs were invalid so we reload the mask from known valid
                // parameters.
                // Also redraws the slide
                populateInputs(index);
            } else {
                // Synchronize the intersection dropdown and spinners
                synchronizeIntersectionInformation();
            }

            activeListener = true;
        }
    }

    private void synchronizeMeasureShapeInformation(int index) {
        // Disable the measure field height spinner if using circle shape
        if (shape.getSelectedItem().equals(MeasureShape.CIRCLE.toString())) {
            spotHeightSpinner.setEnabled(false);
            // If we just changed to the circle shape we have to set the width
            // to the minimum of width and height
            if (maskSettings.getMask(index).getShape() != MeasureShape.CIRCLE) {
                spotWidth.setValue(Math.min(
                        (int) spotWidth.getValue(),
                        (int) spotHeight.getValue()));
            }
            // Now synchronize the height to the width
            spotHeight.setValue(
                    spotWidth.getValue());
        } else {
            spotHeightSpinner.setEnabled(true);
        }
    }

    private void handleIntersectionSelected() {
        if (activeListener) {
            activeListener = false;
            synchronizeIntersectionInformation();
            activeListener = true;
        }
    }

    private void synchronizeIntersectionInformation() {
        int nIntersect = Math.max(horizontalSpacings.size(), verticalSpacings.size());
        int lastSelectedIndex = intersections.getSelectedIndex();
        // Remove all existing dropdown entries
        intersections.removeAllItems();
        // Don't select anything for now
        intersections.setSelectedIndex(-1);
        // Disable the intersection list and enable later if needed
        intersections.setEnabled(false);

        // Case for no intersections needed, exit
        if (nIntersect == 0) {
            horizontalSpacingSpinner.setEnabled(false);
            verticalSpacingSpinner.setEnabled(false);
            return;
        }

        // We have at least an intersection so build the intersection dropdown
        for (int i = 1; i <= nIntersect; i++) {
            intersections.addItem("Intersection " + i);
        }

        // Handle uniform intersections
        if (uniformIntersections.isSelected()) {
            // Always operate on the first intersection
            lastSelectedIndex = 0;
        } else {
            // Compute new selected index
            // Clamp selected index to [0; size[
            lastSelectedIndex = Math.max(0, Math.min(nIntersect - 1, lastSelectedIndex));
            // Enable the intersection list
            intersections.setEnabled(true);
        }
        intersections.setSelectedIndex(lastSelectedIndex);

        // Update the intersection spinners
        if (lastSelectedIndex < horizontalSpacings.size()) {
            horizontalSpacingSpinner.setEnabled(true);
            horizontalSpacing
                    .setValue(horizontalSpacings.get(lastSelectedIndex));
        } else {
            horizontalSpacingSpinner.setEnabled(false);
            horizontalSpacing.setValue(STANDARD_SPACING);
        }

        if (lastSelectedIndex < verticalSpacings.size()) {
            verticalSpacingSpinner.setEnabled(true);
            verticalSpacing
                    .setValue(verticalSpacings.get(lastSelectedIndex));
        } else {
            verticalSpacingSpinner.setEnabled(false);
            verticalSpacing.setValue(STANDARD_SPACING);
        }
    }

    /**
     * Validates the current inputs and returns an instance of slideMaskParameters,
     * if all entries are valid.
     *
     * This shows an error message, if inputs are not valid informing the user, what
     * is wrong.
     *
     * @return the validated slideMaskParameters or empty if invalid
     */
    private Optional<MaskExt> validateInputsInternal() {
        MaskExt sParams;
        try {
            sParams = getSlideMaskParametersFromInputsUnchecked();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.invalidInput.text") + " "
                            + e.getMessage(),
                    Const.bundle.getString("messageDialog.invalidInput.title"),
                    JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
        return Optional.of(sParams);
    }

    /*
     * TODO: investigate this
     * Is the messageDialog here adequate?
     * We should have resolved this in interactive list.
     */
    private boolean validateAndStoreInputsInSettings(int index) {
        Optional<MaskExt> maybeMask = validateInputsInternal();
        if (maybeMask.isEmpty()) {
            return false;
        }
        MaskExt mask = maybeMask.get();
        try {
            maskSettings.setMask(index, mask);
            maskSettings.setLastMeasurePointIndex(mask.getMaxNumberOfSpotsPerSpotfield() - 1);
        } catch (DuplicateElementException e) {
            JOptionPane.showMessageDialog(this,
                    Const.bundle.getString("messageDialog.copyDuplicatesNotAllowed.text"),
                    Const.bundle.getString("messageDialog.copyDuplicatesNotAllowed.title"),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        settingsGui.slideMaskParamsChanged(index);

        return true;
    }

    /**
     * Returns an mask based on the currently entered values.
     *
     * This method is unchecked and will throw an {@code IllegalArgumentException}
     * if the input is
     * invalid.
     *
     * Note: the horizontal and vertical spacings rely on stored state. As such it
     * must be ensured, that the {@code horizontalSpacings} and
     * {@code verticalSpacings} variables are set accordingly BEFORE validating the
     * inputs.
     *
     * @return a mask based on the input
     * @throws IllegalArgumentException if a mask could not be created
     */
    private MaskExt getSlideMaskParametersFromInputsUnchecked() throws IllegalArgumentException {
        String sParamsName = maskName.getText().trim();
        String sParamsDescription = maskDescription.getText().trim();

        int maskWidthInt = (int) maskWidth.getValue();
        int maskHeightInt = (int) maskHeight.getValue();

        int xInsetInt = (int) xInset.getValue();
        int yInsetInt = (int) yInset.getValue();

        int superGridRowsInt = (int) superGridRows.getValue();
        int superGridColumnsInt = (int) superGridColumns.getValue();

        int spotFieldHeight = (int) spotfieldHeight.getValue();
        int spotFieldWidth = (int) spotfieldWidth.getValue();

        int nRows = (int) spotfieldRows.getValue();
        int nCols = (int) spotfieldColumns.getValue();

        MeasureShape shape = MeasureShape.valueOf((String) this.shape.getSelectedItem());

        int measureFieldWidth = (int) spotWidth.getValue();
        int measureFieldHeight = (int) spotHeight.getValue();

        // Update spacing values
        int hSpace = (int) horizontalSpacingSpinner.getValue();
        int vSpace = (int) verticalSpacingSpinner.getValue();
        if (uniformIntersections.isSelected()) {
            horizontalSpacings.replaceAll(n -> hSpace);
            verticalSpacings.replaceAll(n -> vSpace);
        } else {
            int selIdx = intersections.getSelectedIndex();
            // empty dropdown is -1
            if (selIdx >= 0 && selIdx < horizontalSpacings.size()) {
                horizontalSpacings.set(selIdx, hSpace);
            }
            if (selIdx >= 0 && selIdx < verticalSpacings.size()) {
                verticalSpacings.set(selIdx, vSpace);
            }
        }
        // Must change the original vertical spacings to update the state
        if (verticalSpacings.size() > superGridColumnsInt - 1) {
            verticalSpacings = verticalSpacings.subList(0, superGridColumnsInt - 1);
        } else {
            while (verticalSpacings.size() < superGridColumnsInt - 1) {
                verticalSpacings.add(STANDARD_SPACING);
            }
        }
        if (horizontalSpacings.size() > superGridRowsInt - 1) {
            horizontalSpacings = horizontalSpacings.subList(0, superGridRowsInt - 1);
        } else {
            while (horizontalSpacings.size() < superGridRowsInt - 1) {
                horizontalSpacings.add(STANDARD_SPACING);
            }
        }

        MaskExt mask = MaskExt.of(sParamsName, sParamsDescription, maskWidthInt,
                maskHeightInt, xInsetInt, yInsetInt, superGridColumnsInt,
                superGridRowsInt, horizontalSpacings, verticalSpacings,
                spotFieldWidth, spotFieldHeight, nRows, nCols, shape, measureFieldWidth,
                measureFieldHeight);
        return mask;
    }

    private void closeWindow() {
        /*
         * This if clause prevents automatically discarding if the current input is
         * invalid. As such the user does not accidentally lose any of his progress.
         */
        if (validateAndStoreInputsInSettings(interactiveList.getSelectedIndex())) {
            if (!settings.isDirty()) {
                settingsGui.closingSlideMaskDesigner();
                dispose();
                return;
            }
        }
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to discard all changes made?",
                "Discard changes?",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            settings.abort();
            settingsGui.slideMaskParamsChanged(maskSettings.getLastUsedMaskIndex());
            settingsGui.closingSlideMaskDesigner();
            dispose();
        }
    }

    private void handleApplyButtonPressed() {
        // Ask the user if he really want's to save
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to save all changes?",
                "",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            // Store the current edits beforehand
            if (!validateAndStoreInputsInSettings(interactiveList.getSelectedIndex())) {
                // Inputs were invalid, dialogs are created by the validation so we exit
                return;
            }
            // Save all changes
            settings.store();
            try {
                settings.save();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not save settings", e);
                String msg = String.format("We could not save the settings you made: %s", e);
                GuiUtils.showErrorDialog(this, msg, "Unable to save settings");
                return;
            }
        }
    }

    @Override
    public void populateInputs(int index) {
        log.finer(String.format("Populating mask inputs with data from index %d", index));
        activeListener = false;

        MaskExt mask = maskSettings.getMask(index);
        maskSettings.setLastMeasurePointIndex(mask.getMaxNumberOfSpotsPerSpotfield() - 1);

        maskName.setText(mask.getName());
        maskDescription.setText(mask.getDescription());

        maskWidth.setValue(mask.getSlideWidth());
        maskHeight.setValue(mask.getSlideHeight());

        xInset.setValue(mask.getxInset());
        yInset.setValue(mask.getyInset());

        superGridColumns.setValue(mask.getSuperGridCols());
        superGridRows.setValue(mask.getSuperGridRows());

        horizontalSpacings = new ArrayList<>(mask.getHorizontalSpacing());
        verticalSpacings = new ArrayList<>(mask.getVerticalSpacing());

        // If the mask does not use uniform spacing we have to disable the checkbox,
        // else we enable it.
        if (horizontalSpacings.size() > 1 || verticalSpacings.size() > 1) {
            uniformIntersections.setSelected(false);
            intersections.setEnabled(true);
        } else {
            uniformIntersections.setSelected(true);
            intersections.setEnabled(false);
        }

        spotfieldHeight.setValue(mask.getSpotFieldHeight());
        spotfieldWidth.setValue(mask.getSpotFieldWidth());

        spotfieldRows.setValue(mask.getSpotFieldNRows());
        spotfieldColumns.setValue(mask.getSpotFieldNColumns());

        shape.setSelectedItem(mask.getShape().toString());

        spotWidth.setValue(mask.getMeasureFieldWidth());
        spotHeight.setValue(mask.getMeasureFieldHeight());

        // Cause a redraw of the slide
        settingsGui.slideMaskParamsChanged(index);

        // Update spinners, dropdowns and stuff
        synchronizeMeasureShapeInformation(index);
        synchronizeIntersectionInformation();

        activeListener = true;
    }

    @Override
    public boolean validateInputs(int index) {
        log.finer(String.format("Validating mask with index %d", index));
        validating = true;
        boolean validationResult = validateAndStoreInputsInSettings(index);
        validating = false;
        return validationResult;
    }

    @Override
    public void handleExport(int index, Path path) {
        log.finer(String.format("Exporting mask with index %d as path %s", index, path));
        Optional<MaskExt> maybeMask = validateInputsInternal();

        if (!maybeMask.isEmpty()) {
            control.exportMask(maskSettings.getMask(index), path);
        }
    }

    @Override
    public void handleImport(Path path) {
        log.finer(String.format("Importing a mask from path", path));

        try {
            control.importMask(path);
        } catch (IOException | JsonParseException e) {
            String msg = "Could not fully load mask file";
            log.log(Level.SEVERE, msg, e);
            GuiUtils.showWarningDialog(this, String.format(
                    "%s: %s%nThe mask could not be imported", msg, e.getMessage()), null);
            return;
        }

        log.info(String.format("Mask file '%s' has been imported", path));
    }

    @Override
    public void deleteIndex(int index) {
        log.finer(String.format("Deleting mask with index %d", index));
        maskSettings.removeMask(index);
    }

    @Override
    public void addPlaceholder() {
        log.finer("Adding placeholder mask");
        maskSettings.addMask(MaskExt.defaultSettings());
    }

    @Override
    public void addCopyOfElement(int index, String name) {
        log.finer(String.format("Copying mask with index %d and new name %s", index, name));
        MaskExt maskCopy = maskSettings.getMask(index).withName(name);
        maskSettings.addMask(maskCopy);
    }

    @Override
    public String getDefaultName() {
        return MaskExt.DEFAULT_NAME;
    }

    @Override
    public List<String> getNameList() {
        var nameList = maskSettings.getNameList();
        int index = interactiveList.getSelectedIndex();
        String maskNameText = maskName.getText().trim();
        if (index >= 0 && index < nameList.size()
                && !maskNameText.isEmpty()
                && !validating) {
            nameList.set(index, maskNameText);
        }
        return nameList;
    }

    /**
     * Resets properties such as colors that are lost on theme change.
     */
    public void resetComponentProperties() {
        maskNameLabel.setForeground(UIManager.getColor("Actions.Blue"));
        maskDescriptionLabel.setForeground(UIManager.getColor("Actions.Blue"));
        sidePanel.setBackground(UIManager.getColor("MenuBar.background"));
        actionPanel.setBackground(UIManager.getColor("MenuBar.background"));
        applyButton.setBackground(UIManager.getColor("Actions.Green"));
        closeButton.setBackground(UIManager.getColor("Actions.Red"));

        pack();
    }
}
