package com.scitequest.martin.settings;

import java.time.Duration;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;

import com.scitequest.martin.export.JsonExportable;

public final class ImageDefaults implements JsonExportable {

    /** The imager device used to take the image. */
    private final String imager;
    /**
     * Pixel binning.
     *
     * A value of 2 represents 2x2 binning.
     * If set to 1, this means no binning is applied sinc 1x1 binning don't alters
     * the input image.
     */
    private final int pixelBinning;
    /** The exposure time during the image scan. */
    private final Duration exposureTime;

    private ImageDefaults(String imager, int pixelBinning, Duration exposureTime) {
        this.imager = imager;
        this.pixelBinning = pixelBinning;
        this.exposureTime = exposureTime;
    }

    /**
     * Get a new image metadata.
     *
     * @param imager       the imager used to take the image, cannot be empty
     * @param pixelBinning the pixel binning
     * @param exposureTime the exposure time of the image, cannot be negative
     * @throws IllegalArgumentException if any of the above contracts is violated
     * @return a new image metadata object
     */
    public static ImageDefaults of(String imager, int pixelBinning, Duration exposureTime) {
        if (imager.isBlank()) {
            throw new IllegalArgumentException("Imager cannot be empty");
        }
        if (pixelBinning < 1) {
            throw new IllegalArgumentException("Pixel binning must be at least 1x1");
        }
        if (exposureTime.isNegative()) {
            throw new IllegalArgumentException("Exposure time cannot be negative");
        }

        return new ImageDefaults(imager, pixelBinning, exposureTime);
    }

    /**
     * Get the imager.
     *
     * @return the imager
     */
    public String getImager() {
        return imager;
    }

    /**
     * Get the pixel binning.
     *
     * @return the pixel binning
     */
    public int getPixelBinning() {
        return pixelBinning;
    }

    /**
     * Get the exposure time.
     *
     * @return the exposure time
     */
    public Duration getExposureTime() {
        return exposureTime;
    }

    @Override
    public JsonStructure asJson() {
        return Json.createObjectBuilder()
                .add("imager", this.imager)
                .add("pixel_binning", this.pixelBinning)
                .add("exposure_time", this.exposureTime.toString())
                .build();
    }

    public static ImageDefaults fromJson(JsonObject json) {
        String imager = json.getString("imager");
        int pixelBinning = json.getInt("pixel_binning");
        Duration exposureTime = Duration.parse(json.getString("exposure_time"));
        return ImageDefaults.of(imager, pixelBinning, exposureTime);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imager == null) ? 0 : imager.hashCode());
        result = prime * result + pixelBinning;
        result = prime * result + ((exposureTime == null) ? 0 : exposureTime.hashCode());
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
        ImageDefaults other = (ImageDefaults) obj;
        if (imager == null) {
            if (other.imager != null)
                return false;
        } else if (!imager.equals(other.imager))
            return false;
        if (pixelBinning != other.pixelBinning)
            return false;
        if (exposureTime == null) {
            if (other.exposureTime != null)
                return false;
        } else if (!exposureTime.equals(other.exposureTime))
            return false;
        return true;
    }
}
