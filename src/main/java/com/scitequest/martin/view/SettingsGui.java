package com.scitequest.martin.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.scitequest.martin.Const;
import com.scitequest.martin.settings.DisplaySettings;
import com.scitequest.martin.settings.DisplaySettings.Theme;
import com.scitequest.martin.settings.ExportSettings;
import com.scitequest.martin.settings.MaskExt;
import com.scitequest.martin.settings.MeasurementSettings;
import com.scitequest.martin.settings.Settings;

/**
 * This GUI allows the user to change slide parameters as well as save-paths,
 * load-paths and export settings.
 */
public final class SettingsGui extends JDialog {
    private static final long serialVersionUID = 1L;

    /** The logger for this class. */
    private static final Logger log = Logger.getLogger("com.scitequest.martin.view.OptionsGui");

    private Controlable control;
    private Settings settings;

    private boolean activeListener;

    // General tab
    private final JCheckBox invertLut = new JCheckBox();
    private final JCheckBox subtractBackground = new JCheckBox();
    private final JButton openMaskDesignerButton = new JButton();
    private final JComboBox<String> selectedMask = new JComboBox<>();
    private final DefaultComboBoxModel<String> selectedMaskModel = new DefaultComboBoxModel<>();
    private final JSpinner lastRowSpinner = new JSpinner();
    private final JSpinner lastColumnSpinner = new JSpinner();

    // Display tab
    private final JComboBox<String> lookAndFeel;
    private final JCheckBox showGrid = new JCheckBox();
    private final JCheckBox showMeasureField = new JCheckBox();
    private final JPanel slideColorPreview = new JPanel();
    private final JPanel spotfieldColorPreview = new JPanel();
    private final JPanel measurefieldColorPreview = new JPanel();
    private final JPanel backgroundRectangleColorPreview = new JPanel();

    // Export tab
    private final JTextField exportDirectory = new JTextField();
    private final JCheckBox saveAnnotatedImage = new JCheckBox();
    private final JCheckBox exportJsonData = new JCheckBox();
    private final JCheckBox exportTsvData = new JCheckBox();
    private final JCheckBox storeMeasuredImage = new JCheckBox();

    /**
     * Constructor of this class.
     *
     * @param owner    reference of GUI
     * @param control  reference to the same control instance as GUI
     * @param settings collection of relevant parameters
     */
    public SettingsGui(Frame owner, Controlable control, Settings settings) {
        super(owner, "Settings", false);

        this.control = control;
        this.settings = settings;
        ExportSettings exportSettings = settings.getExportSettings();
        this.activeListener = true;

        this.setBounds(50, 50, 350, 410);
        this.setResizable(false);

        // Setting up a custom close operation
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // TODO: maybe add a closing dialog here?
                // Optional save?
                close(true);
            }
        });

        // ======== tabbedPane ========
        JTabbedPane tabbedPane = new JTabbedPane();

        // ======== generalTab ========
        JPanel generalTab = new JPanel();
        generalTab.setBorder(new EmptyBorder(5, 5, 5, 5));
        generalTab.setLayout(new GridBagLayout());
        ((GridBagLayout) generalTab.getLayout()).columnWidths = new int[] { 0, 0, 0 };
        ((GridBagLayout) generalTab.getLayout()).rowHeights = new int[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ((GridBagLayout) generalTab.getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
        ((GridBagLayout) generalTab.getLayout()).rowWeights = new double[] {
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

        // ---- projectHeader ----
        JLabel projectHeader = new JLabel();
        projectHeader.setText(Const.bundle.getString("settingsGui.projectHeader.text"));
        projectHeader.putClientProperty("FlatLaf.styleClass", "h2");
        generalTab.add(projectHeader, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

        // ---- openProjectManagerButton ----
        JButton openProjectManagerButton = new JButton();
        openProjectManagerButton.setText(
                Const.bundle.getString("settingsGui.openProjectManagerButton.text"));
        openProjectManagerButton.addActionListener(e -> openProjectManager());
        generalTab.add(openProjectManagerButton, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- measurementHeader ----
        JLabel measurementHeader = new JLabel();
        measurementHeader.setText(Const.bundle.getString("settingsGui.measurementHeader.text"));
        measurementHeader.putClientProperty("FlatLaf.styleClass", "h2");
        generalTab.add(measurementHeader, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 0), 0, 0));

        // ---- invertLut ----
        invertLut.setText(Const.bundle.getString("settingsGui.invertLut.text"));
        invertLut.addActionListener(l -> settingsChanged());
        generalTab.add(invertLut, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- invertLutDescription ----
        JTextArea invertLutDescription = new JTextArea();
        invertLutDescription.setLineWrap(true);
        invertLutDescription.setWrapStyleWord(true);
        invertLutDescription.setOpaque(false);
        invertLutDescription.setText(
                Const.bundle.getString("settingsGui.invertLutDescription.text"));
        invertLutDescription.setEditable(false);
        invertLutDescription.setEnabled(false);
        generalTab.add(invertLutDescription, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- subtractBackground ----
        subtractBackground.setText(Const.bundle.getString("settingsGui.subtractBackground.text"));
        subtractBackground.addActionListener(l -> settingsChanged());
        generalTab.add(subtractBackground, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- subtractBackgroundDescription ----
        JTextArea subtractBackgroundDescription = new JTextArea();
        subtractBackgroundDescription.setLineWrap(true);
        subtractBackgroundDescription.setWrapStyleWord(true);
        subtractBackgroundDescription.setOpaque(false);
        subtractBackgroundDescription.setText(
                Const.bundle.getString("settingsGui.subtractBackgroundDescription.text"));
        subtractBackgroundDescription.setEditable(false);
        subtractBackgroundDescription.setEnabled(false);
        generalTab.add(subtractBackgroundDescription, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- maskHeader ----
        JLabel maskHeader = new JLabel();
        maskHeader.setText(Const.bundle.getString("settingsGui.maskHeader.text"));
        maskHeader.putClientProperty("FlatLaf.styleClass", "h2");
        generalTab.add(maskHeader, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 0), 0, 0));

        // ---- selectedMaskLabel ----
        JLabel selectedMaskLabel = new JLabel();
        selectedMaskLabel.setText(Const.bundle.getString("settingsGui.selectedMaskLabel.text"));
        selectedMaskLabel.putClientProperty("FlatLaf.styleClass", "h4");
        generalTab.add(selectedMaskLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- selectedMask ----
        selectedMaskModel.addAll(settings.getMaskSettings().getNameList());
        selectedMask.setModel(selectedMaskModel);
        selectedMask.setSelectedIndex(settings.getMaskSettings().getLastUsedMaskIndex());
        selectedMask.addActionListener(e -> handleMaskSelectionChanged());
        generalTab.add(selectedMask, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- openMaskDesignerButton ----
        openMaskDesignerButton.setText(
                Const.bundle.getString("settingsGui.openMaskDesignerButton.text"));
        openMaskDesignerButton.addActionListener(e -> handleOpenSlideDesignerGuiButton());
        generalTab.add(openMaskDesignerButton, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));

        // ======== lastRowColumnPanel ========
        JPanel lastRowColumnPanel = new JPanel();
        lastRowColumnPanel.setLayout(new GridBagLayout());
        ((GridBagLayout) lastRowColumnPanel.getLayout()).columnWidths = new int[] { 0, 0, 0 };
        ((GridBagLayout) lastRowColumnPanel.getLayout()).rowHeights = new int[] { 0, 0, 0 };
        ((GridBagLayout) lastRowColumnPanel.getLayout()).columnWeights = new double[] {
                0.0, 0.0, 1.0E-4 };
        ((GridBagLayout) lastRowColumnPanel.getLayout()).rowWeights = new double[] {
                0.0, 0.0, 1.0E-4 };

        // ---- lastRowLabel ----
        JLabel lastRowLabel = new JLabel();
        lastRowLabel.setText(Const.bundle.getString("settingsGui.lastRowLabel.text"));
        lastRowLabel.putClientProperty("FlatLaf.styleClass", "h4");
        lastRowColumnPanel.add(lastRowLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- lastColumnLabel ----
        JLabel lastColumnLabel = new JLabel();
        lastColumnLabel.setText(Const.bundle.getString("settingsGui.lastColumnLabel.text"));
        lastColumnLabel.putClientProperty("FlatLaf.styleClass", "h4");
        lastRowColumnPanel.add(lastColumnLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- lastRowSpinner ----
        lastRowSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        lastRowSpinner.setEditor(new AlphabeticNumberEditor(lastRowSpinner));
        lastRowSpinner.addChangeListener(c -> settingsChanged());
        lastRowColumnPanel.add(lastRowSpinner, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        // ---- lastColumnSpinner ----
        lastColumnSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        lastColumnSpinner.addChangeListener(c -> settingsChanged());
        lastRowColumnPanel.add(lastColumnSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        generalTab.add(lastRowColumnPanel, new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab(Const.bundle.getString("settingsGui.generalTab.tab.title"), generalTab);

        // ======== displayTab ========
        JPanel displayTab = new JPanel();
        displayTab.setBorder(new EmptyBorder(5, 5, 5, 5));
        displayTab.setLayout(new GridBagLayout());
        ((GridBagLayout) displayTab.getLayout()).columnWidths = new int[] { 0, 0, 0 };
        ((GridBagLayout) displayTab.getLayout()).rowHeights = new int[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ((GridBagLayout) displayTab.getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
        ((GridBagLayout) displayTab.getLayout()).rowWeights = new double[] {
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

        // ---- lookAndFeelHeader ----
        JLabel lookAndFeelHeader = new JLabel();
        lookAndFeelHeader.setText(Const.bundle.getString("settingsGui.lookAndFeelHeader.text"));
        lookAndFeelHeader.putClientProperty("FlatLaf.styleClass", "h2");
        displayTab.add(lookAndFeelHeader, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

        // ---- lookAndFeelDescription ----
        JTextArea lookAndFeelDescription = new JTextArea();
        lookAndFeelDescription.setLineWrap(true);
        lookAndFeelDescription.setWrapStyleWord(true);
        lookAndFeelDescription.setOpaque(false);
        lookAndFeelDescription.setText(
                Const.bundle.getString("settingsGui.lookAndFeelDescription.text"));
        lookAndFeelDescription.setEditable(false);
        lookAndFeelDescription.setEnabled(false);
        displayTab.add(lookAndFeelDescription, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- lookAndFeel ----
        String[] themes = Arrays.stream(Theme.values())
                .map(theme -> theme.getDisplayName())
                .toArray(String[]::new);
        lookAndFeel = new JComboBox<>(themes);
        lookAndFeel.setSelectedItem(settings.getDisplaySettings().getTheme().getDisplayName());
        lookAndFeel.addActionListener(e -> handleThemeSwitch());
        displayTab.add(lookAndFeel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- generalHeader ----
        JLabel generalHeader = new JLabel();
        generalHeader.setText(Const.bundle.getString("settingsGui.generalHeader.text"));
        generalHeader.putClientProperty("FlatLaf.styleClass", "h2");
        displayTab.add(generalHeader, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 0), 0, 0));

        // ---- showGrid ----
        showGrid.setText(Const.bundle.getString("settingsGui.showGrid.text"));
        showGrid.addActionListener(l -> settingsChanged());
        displayTab.add(showGrid, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- showGridDescription ----
        JTextArea showGridDescription = new JTextArea();
        showGridDescription.setLineWrap(true);
        showGridDescription.setWrapStyleWord(true);
        showGridDescription.setOpaque(false);
        showGridDescription.setText(Const.bundle.getString("settingsGui.showGridDescription.text"));
        showGridDescription.setEditable(false);
        showGridDescription.setEnabled(false);
        displayTab.add(showGridDescription, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- showMeasureField ----
        showMeasureField.setText(Const.bundle.getString("settingsGui.showMeasureField.text"));
        showMeasureField.addActionListener(l -> settingsChanged());

        displayTab.add(showMeasureField, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- showMeasureFieldDescription ----
        JTextArea showMeasureFieldDescription = new JTextArea();
        showMeasureFieldDescription.setLineWrap(true);
        showMeasureFieldDescription.setWrapStyleWord(true);
        showMeasureFieldDescription.setOpaque(false);
        showMeasureFieldDescription.setText(
                Const.bundle.getString("settingsGui.showMeasureFieldDescription.text"));
        showMeasureFieldDescription.setEditable(false);
        showMeasureFieldDescription.setEnabled(false);
        displayTab.add(showMeasureFieldDescription, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- colorLabel ----
        JLabel colorLabel = new JLabel();
        colorLabel.setText(Const.bundle.getString("settingsGui.colorLabel.text"));
        colorLabel.putClientProperty("FlatLaf.styleClass", "h4");
        displayTab.add(colorLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ======== slideColorPreview ========
        slideColorPreview.setLayout(new BorderLayout());
        displayTab.add(slideColorPreview, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- slideColorButton ----
        JButton slideColorButton = new JButton();
        slideColorButton.setText(
                Const.bundle.getString("settingsGui.slideColorButton.text"));
        slideColorButton.addActionListener(e -> handleSlideColorChange());
        displayTab.add(slideColorButton, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ======== spotfieldColorPreview ========
        spotfieldColorPreview.setLayout(new BorderLayout());
        displayTab.add(spotfieldColorPreview, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- spotfieldColorButton ----
        JButton spotfieldColorButton = new JButton();
        spotfieldColorButton.setText(
                Const.bundle.getString("settingsGui.spotfieldColorButton.text"));
        spotfieldColorButton.addActionListener(e -> handleSpotfieldColorChange());
        displayTab.add(spotfieldColorButton, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ======== measurefieldColorPreview ========
        measurefieldColorPreview.setLayout(new BorderLayout());
        displayTab.add(measurefieldColorPreview, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- measurefieldColorButton ----
        JButton measurefieldColorButton = new JButton();
        measurefieldColorButton.setText(
                Const.bundle.getString("settingsGui.measurefieldColorButton.text"));
        measurefieldColorButton.addActionListener(e -> handleMeasurefieldColorChange());
        displayTab.add(measurefieldColorButton, new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ======== backgroundRectangleColorPreview ========
        backgroundRectangleColorPreview.setLayout(new BorderLayout());
        displayTab.add(backgroundRectangleColorPreview, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

        // ---- backgroundRectangleColorButton ----
        JButton backgroundRectangleColorButton = new JButton();
        backgroundRectangleColorButton.setText(
                Const.bundle.getString("settingsGui.backgroundRectangleColorButton.text"));
        backgroundRectangleColorButton.addActionListener(
                e -> handleBackgroundRectangleColorChange());
        displayTab.add(backgroundRectangleColorButton, new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab(Const.bundle.getString("settingsGui.displayTab.title"), displayTab);

        // ======== exportTab ========
        JPanel exportTab = new JPanel();
        exportTab.setBorder(new EmptyBorder(5, 5, 5, 5));
        exportTab.setLayout(new GridBagLayout());
        ((GridBagLayout) exportTab.getLayout()).columnWidths = new int[] { 0, 0, 0 };
        ((GridBagLayout) exportTab.getLayout()).rowHeights = new int[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ((GridBagLayout) exportTab.getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
        ((GridBagLayout) exportTab.getLayout()).rowWeights = new double[] {
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

        // ---- exportDirectoryLabel ----
        JLabel exportDirectoryLabel = new JLabel();
        exportDirectoryLabel.setText(
                Const.bundle.getString("settingsGui.exportDirectoryLabel.text"));
        exportDirectoryLabel.putClientProperty("FlatLaf.styleClass", "h4");
        exportTab.add(exportDirectoryLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- exportDirectory ----
        exportDirectory.setEnabled(false);
        exportDirectory.setEditable(false);
        exportTab.add(exportDirectory, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- exportDirectoryButton ----
        JButton exportDirectoryButton = new JButton();
        exportDirectoryButton.setText(
                Const.bundle.getString("settingsGui.exportDirectoryButton.text"));
        exportDirectoryButton.addActionListener(l -> handleExportDirectorySelect());
        exportTab.add(exportDirectoryButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- exportDirectoryDescription ----
        JTextArea exportDirectoryDescription = new JTextArea();
        exportDirectoryDescription.setLineWrap(true);
        exportDirectoryDescription.setWrapStyleWord(true);
        exportDirectoryDescription.setOpaque(false);
        exportDirectoryDescription.setText(
                Const.bundle.getString("settingsGui.exportDirectoryDescription.text"));
        exportDirectoryDescription.setEditable(false);
        exportDirectoryDescription.setEnabled(false);
        exportTab.add(exportDirectoryDescription, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, -5, 5, 0), 0, 0));

        // ---- dataExportLabel ----
        JLabel dataExportLabel = new JLabel();
        dataExportLabel.setText(Const.bundle.getString("settingsGui.dataExportLabel.text"));
        dataExportLabel.putClientProperty("FlatLaf.styleClass", "h4");
        exportTab.add(dataExportLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- exportAnnotatedImage ----
        saveAnnotatedImage.setText(
                Const.bundle.getString("settingsGui.exportAnnotatedImage.text"));
        saveAnnotatedImage.setSelected(exportSettings.isSaveAnnotatedImage());
        exportTab.add(saveAnnotatedImage, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- exportAnnotatedImageDescription ----
        JTextArea exportAnnotatedImageDescription = new JTextArea();
        exportAnnotatedImageDescription.setLineWrap(true);
        exportAnnotatedImageDescription.setWrapStyleWord(true);
        exportAnnotatedImageDescription.setOpaque(false);
        exportAnnotatedImageDescription.setText(
                Const.bundle.getString("settingsGui.exportAnnotatedImageDescription.text"));
        exportAnnotatedImageDescription.setEditable(false);
        exportAnnotatedImageDescription.setEnabled(false);
        exportTab.add(exportAnnotatedImageDescription, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        // ---- exportJsonData ----
        exportJsonData.setText(Const.bundle.getString("settingsGui.exportJsonData.text"));
        exportJsonData.setSelected(exportSettings.isExportJSON());
        exportJsonData.addActionListener(e -> exportCheckboxChanged(exportJsonData));
        exportTab.add(exportJsonData, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- exportJsonDataDescription ----
        JTextArea exportJsonDataDescription = new JTextArea();
        exportJsonDataDescription.setLineWrap(true);
        exportJsonDataDescription.setWrapStyleWord(true);
        exportJsonDataDescription.setOpaque(false);
        exportJsonDataDescription.setText(Const.bundle.getString("settingsGui.exportJsonDataDescription.text"));
        exportJsonDataDescription.setEditable(false);
        exportJsonDataDescription.setEnabled(false);
        exportTab.add(exportJsonDataDescription, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- exportTsvData ----
        exportTsvData.setText(Const.bundle.getString("settingsGui.exportTsvData.text"));
        exportTsvData.setSelected(exportSettings.isExportTSV());
        exportTsvData.addActionListener(e -> exportCheckboxChanged(exportTsvData));
        exportTab.add(exportTsvData, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- exportTsvDataDescription ----
        JTextArea exportTsvDataDescription = new JTextArea();
        exportTsvDataDescription.setLineWrap(true);
        exportTsvDataDescription.setWrapStyleWord(true);
        exportTsvDataDescription.setOpaque(false);
        exportTsvDataDescription.setText(
                Const.bundle.getString("settingsGui.exportTsvDataDescription.text"));
        exportTsvDataDescription.setEditable(false);
        exportTsvDataDescription.setEnabled(false);
        exportTab.add(exportTsvDataDescription, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- storeMeasuredImage ----
        storeMeasuredImage.setText(Const.bundle.getString("settingsGui.storeMeasuredImage.text"));
        storeMeasuredImage.setSelected(exportSettings.isSaveWholeImage());
        exportTab.add(storeMeasuredImage, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        // ---- storeMeasuredImageDescription ----
        JTextArea storeMeasuredImageDescription = new JTextArea();
        storeMeasuredImageDescription.setLineWrap(true);
        storeMeasuredImageDescription.setWrapStyleWord(true);
        storeMeasuredImageDescription.setOpaque(false);
        storeMeasuredImageDescription.setText(
                Const.bundle.getString("settingsGui.storeMeasuredImageDescription.text"));
        storeMeasuredImageDescription.setEditable(false);
        storeMeasuredImageDescription.setEnabled(false);
        exportTab.add(storeMeasuredImageDescription, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        tabbedPane.addTab(Const.bundle.getString("settingsGui.exportTab.title"), exportTab);
        add(tabbedPane);

        loadFromSettings();

        setLocationRelativeTo(getOwner());
        setVisible(true);
        pack();
    }

    /**
     * This method prevents both export checkboxes to be deselected.
     *
     * @param changedBox last changedCheckbox
     */
    private void exportCheckboxChanged(JCheckBox changedBox) {
        if (exportJsonData.isSelected() || exportTsvData.isSelected()) {
            return;
        }
        if (changedBox.equals(exportJsonData)) {
            exportTsvData.setSelected(true);
        } else {
            exportJsonData.setSelected(true);
        }
    }

    private void handleThemeSwitch() {
        // We have to defer the theme switch, or else we crash inside the combobox
        // implementation because the layout and appearance of the JComboBox can change,
        // which could potentially cause issues with keyboard navigation if the new
        // layout is not properly handled by the BasicComboBoxUI.
        SwingUtilities.invokeLater(() -> {
            int selectedTheme = lookAndFeel.getSelectedIndex();
            Theme theme = Theme.values()[selectedTheme];
            settings.getDisplaySettings().setTheme(theme);
            settings.store();
            setLookAndFeel(theme.getClassName());
        });
    }

    /**
     * Sets the look and feel of the application.
     *
     * @param className the look and feel class path
     */
    public static void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            log.log(Level.SEVERE, String.format("Failed to set theme '%s'", className), e);
        }
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    private void handleOpenSlideDesignerGuiButton() {
        selectedMask.setEnabled(false);
        openMaskDesignerButton.setEnabled(false);
        settings.getMaskSettings().setLastUsedMaskLastMeasurePointIndexToMax();
        adjustLastRowColSpinners();
        this.setVisible(false);
        new MaskDesigner(control, settings, this);
    }

    /**
     * Loads all values of resources into this opitonsGUI.
     */
    private void loadFromSettings() {
        activeListener = false;

        DisplaySettings displaySettings = settings.getDisplaySettings();
        showGrid.setSelected(displaySettings.isShowSpotfieldGrids());
        showMeasureField.setSelected(displaySettings.isShowMeasureCircles());
        invertLut.setSelected(settings.getMeasurementSettings().isInvertLut());
        subtractBackground.setSelected(settings.getMeasurementSettings().isSubtractBackground());

        saveAnnotatedImage.setSelected(
                settings.getExportSettings().isSaveAnnotatedImage());
        exportDirectory.setText(settings.getExportSettings().getExportDirectory()
                .map(path -> path.toString()).orElse(""));

        slideColorPreview.setBackground(displaySettings.getSlideColor());
        spotfieldColorPreview.setBackground(displaySettings.getSpotfieldColor());
        measurefieldColorPreview.setBackground(displaySettings.getMeasureCircleColor());
        backgroundRectangleColorPreview.setBackground(displaySettings.getDeletionRectangleColor());

        adjustLastRowColSpinners();

        activeListener = true;
    }

    private void adjustLastRowColSpinners() {
        int lastIndex = settings.getMaskSettings().getLastMeasurePointIndex();
        int columns = settings.getMaskSettings().getLastUsedMask().getSpotFieldNColumns();
        int rows = settings.getMaskSettings().getLastUsedMask().getSpotFieldNRows();

        lastColumnSpinner.setModel(new SpinnerNumberModel(1, 1, columns, 1));
        lastColumnSpinner.setValue((lastIndex % columns) + 1);
        lastRowSpinner.setModel(new SpinnerNumberModel(0, 0, rows - 1, 1));
        lastRowSpinner.setValue(lastIndex / columns);
    }

    /**
     * Changes all values of resources into the values contained in this optionsGui.
     */
    private void storeInSettings() {
        activeListener = false;

        MaskExt maskSettings = settings.getMaskSettings().getLastUsedMask();

        int numRow = (int) lastRowSpinner.getValue();
        int lastIndex = maskSettings.getSpotFieldNColumns() * numRow
                + ((int) lastColumnSpinner.getValue() - 1);
        settings.getMaskSettings().setLastMeasurePointIndex(lastIndex);

        DisplaySettings displaySettings = settings.getDisplaySettings();
        displaySettings.setShowSpotfieldGrids(showGrid.isSelected());
        displaySettings.setShowMeasureCircles(showMeasureField.isSelected());

        settings.getExportSettings().setSaveAnnotatedImage(
                saveAnnotatedImage.isSelected());

        MeasurementSettings measurementSettings = settings.getMeasurementSettings();
        measurementSettings.setInvertLut(invertLut.isSelected());
        measurementSettings.setSubtractBackground(subtractBackground.isSelected());

        // Setting some settings may not change to the requested value.
        // By loading again we re-synchronize the GUI.
        loadFromSettings();

        activeListener = true;
    }

    /**
     * Closes this optionsGui.
     *
     * @param save will save changes if true and revert them if false
     */
    private void close(boolean save) {
        if (save) {
            ExportSettings exportSettings = settings.getExportSettings();
            exportSettings.setExportTSV(exportTsvData.isSelected());
            exportSettings.setExportJSON(exportJsonData.isSelected());
            exportSettings.setSaveAnnotatedImage(saveAnnotatedImage.isSelected());
            exportSettings.setSaveWholeImage(storeMeasuredImage.isSelected());
            settings.store();
            try {
                settings.save();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not save settings", e);
                String msg = String.format("We could not save the settings you made: %s", e);
                GuiUtils.showErrorDialog(this, msg, "Unable to save settings");
            }
        } else { // cancelled
            settings.abort();
            loadFromSettings();
        }
        control.repositionSlide();
        control.update();
        control.optionsGuiClosed();
        dispose();
    }

    private void handleExportDirectorySelect() {
        // Let user choose a directory
        Optional<Path> exportDirectoryPath = GuiUtils.chooseDirectory(this,
                "Select Export Directory");
        // Validate directory
        try {
            settings.getExportSettings().setExportDirectory(exportDirectoryPath);
        } catch (IllegalArgumentException e) {
            log.log(Level.WARNING, "User selected export directory path is not valid", e);
            String msg = String.format("The selected path is not valid: %s", e.getMessage());
            GuiUtils.showWarningDialog(this, msg, null);
            return;
        }
        // Set the textfield to the directory path
        exportDirectory.setText(exportDirectoryPath.map(path -> path.toString()).orElse(""));
    }

    private void handleSlideColorChange() {
        Color newColor = JColorChooser.showDialog(this,
                "Change Slide Color", slideColorPreview.getBackground());
        if (newColor == null) {
            return;
        }
        slideColorPreview.setBackground(newColor);
        settings.getDisplaySettings().setSlideColor(newColor);
        this.control.update();
    }

    private void handleSpotfieldColorChange() {
        Color newColor = JColorChooser.showDialog(this,
                "Change Spotfield Color", spotfieldColorPreview.getBackground());
        if (newColor == null) {
            return;
        }
        spotfieldColorPreview.setBackground(newColor);
        settings.getDisplaySettings().setSpotfieldColor(newColor);
        this.control.update();
    }

    private void handleMeasurefieldColorChange() {
        Color newColor = JColorChooser.showDialog(this,
                "Change Measurefield Color", measurefieldColorPreview.getBackground());
        if (newColor == null) {
            return;
        }
        measurefieldColorPreview.setBackground(newColor);
        settings.getDisplaySettings().setMeasureCircleColor(newColor);
        this.control.update();
    }

    private void handleBackgroundRectangleColorChange() {
        Color newColor = JColorChooser.showDialog(this,
                "Change Slide color", backgroundRectangleColorPreview.getBackground());
        if (newColor == null) {
            return;
        }
        backgroundRectangleColorPreview.setBackground(newColor);
        settings.getDisplaySettings().setDeletionRectangleColor(newColor);
        this.control.update();
    }

    public void settingsChanged() {
        if (activeListener) {
            storeInSettings();
            control.repositionSlide();
            control.update();
        }
    }

    public void slideMaskParamsChanged(int index) {
        control.setActiveMaskSettings(settings.getMaskSettings().getMask(index));
        control.repositionSlide();
        control.update();
    }

    public void closingSlideMaskDesigner() {
        // Reset mask selection dropdown since masks may have changed
        // We have to disable the listener for this or the selection changed is
        // triggered by the removeAllElements causing the last used mask index to be
        // clamped to 0.
        activeListener = false;
        selectedMaskModel.removeAllElements();
        selectedMaskModel.addAll(settings.getMaskSettings().getNameList());
        activeListener = true;
        // If the last used mask was deleted, the mask settings store a last mask index
        // of -1. Thus we clamp it to 0.
        int lastUsedMaskIndex = Math.max(0, settings.getMaskSettings().getLastUsedMaskIndex());
        // Reselect the last used mask, this causes the last used mask to load through
        // the listener on the dropdown.
        selectedMask.setSelectedIndex(lastUsedMaskIndex);

        // Save the settings
        try {
            settings.save();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        selectedMask.setEnabled(true);
        openMaskDesignerButton.setEnabled(true);
        this.setVisible(true);
    }

    private void handleMaskSelectionChanged() {
        if (activeListener) {
            int selectedIndex = Math.max(0, selectedMask.getSelectedIndex());
            settings.getMaskSettings().setLastUsedMaskIndex(selectedIndex);
            slideMaskParamsChanged(selectedIndex);
            settings.getMaskSettings().setLastUsedMaskLastMeasurePointIndexToMax();
            adjustLastRowColSpinners();
            settings.store();
        }
    }

    private void openProjectManager() {
        this.setVisible(false);
        ProjectGui.open(this, control, settings);
    }
}
