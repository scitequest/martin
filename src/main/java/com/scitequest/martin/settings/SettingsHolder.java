package com.scitequest.martin.settings;

final class SettingsHolder {

    public final DisplaySettings displaySettings = DisplaySettings.defaultSettings();
    public final ExportSettings exportSettings = ExportSettings.defaultSettings();
    public final MaskSettings maskSettings = MaskSettings.defaultSettings();
    public final MeasurementSettings measurementSettings = MeasurementSettings.defaultSettings();
    public final ProjectSettings projectSettings = ProjectSettings.defaultSettings();

    private SettingsHolder() {
    }

    private SettingsHolder(DisplaySettings displaySettings, ExportSettings exportSettings,
            MaskSettings maskSettings, MeasurementSettings measurementSettings,
            ProjectSettings projectSettings) {
        this.displaySettings.copyInto(displaySettings);
        this.exportSettings.copyInto(exportSettings);
        this.maskSettings.copyInto(maskSettings);
        this.measurementSettings.copyInto(measurementSettings);
        this.projectSettings.copyInto(projectSettings);
    }

    static SettingsHolder defaultSettings() {
        return new SettingsHolder();
    }

    static SettingsHolder of(DisplaySettings displaySettings, ExportSettings exportSettings,
            MaskSettings maskSettings, MeasurementSettings measurementSettings,
            ProjectSettings projectSettings) {
        return new SettingsHolder(displaySettings, exportSettings, maskSettings,
                measurementSettings, projectSettings);
    }

    void copyInto(SettingsHolder other) {
        this.displaySettings.copyInto(other.displaySettings);
        this.exportSettings.copyInto(other.exportSettings);
        this.maskSettings.copyInto(other.maskSettings);
        this.measurementSettings.copyInto(other.measurementSettings);
        this.projectSettings.copyInto(other.projectSettings);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displaySettings == null) ? 0 : displaySettings.hashCode());
        result = prime * result + ((exportSettings == null) ? 0 : exportSettings.hashCode());
        result = prime * result + ((maskSettings == null) ? 0 : maskSettings.hashCode());
        result = prime * result + ((measurementSettings == null) ? 0 : measurementSettings.hashCode());
        result = prime * result + ((projectSettings == null) ? 0 : projectSettings.hashCode());
        return result;
    }

    @SuppressWarnings("checkstyle:NeedBraces")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SettingsHolder other = (SettingsHolder) obj;
        if (displaySettings == null) {
            if (other.displaySettings != null)
                return false;
        } else if (!displaySettings.equals(other.displaySettings))
            return false;
        if (exportSettings == null) {
            if (other.exportSettings != null)
                return false;
        } else if (!exportSettings.equals(other.exportSettings))
            return false;
        if (maskSettings == null) {
            if (other.maskSettings != null)
                return false;
        } else if (!maskSettings.equals(other.maskSettings))
            return false;
        if (measurementSettings == null) {
            if (other.measurementSettings != null)
                return false;
        } else if (!measurementSettings.equals(other.measurementSettings))
            return false;
        if (projectSettings == null) {
            if (other.projectSettings != null)
                return false;
        } else if (!projectSettings.equals(other.projectSettings))
            return false;
        return true;
    }
}
