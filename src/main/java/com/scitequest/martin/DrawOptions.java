package com.scitequest.martin;

/** Specifies what should be drawn when painting slide elements. */
public final class DrawOptions {

    /** Whether to draw the slide outline. */
    private final boolean drawSlide;
    /** Whether to draw the spotfields outline. */
    private final boolean drawSpotFields;
    /** Whether to annotate the spotfields. */
    private final boolean drawSpotFieldAnnotation;
    /** Whether to draw the line separating the spotfields. */
    private final boolean drawDivideLine;
    /** Whether to draw the deletion rectangles. */
    private final boolean drawDelRects;
    /** Whether to draw the measurement circles. */
    private final boolean drawMeasureCircles;

    /**
     * Create a new draw options.
     *
     * @param drawSlide               whether to draw the draw the slide outline
     * @param drawSpotFields          whether to draw the spot fields outline
     * @param drawSpotFieldAnnotation whether to annotate the spotfield
     * @param drawDivideLine          whether to draw the line separating the
     *                                spotfields
     * @param drawDelRects            whether to draw the deletion rectangles
     * @param drawMeasureCircles      whetehr to draw the measurement circles
     */
    public DrawOptions(boolean drawSlide, boolean drawSpotFields, boolean drawSpotFieldAnnotation,
            boolean drawDivideLine, boolean drawDelRects, boolean drawMeasureCircles) {
        this.drawSlide = drawSlide;
        this.drawSpotFields = drawSpotFields;
        this.drawSpotFieldAnnotation = drawSpotFieldAnnotation;
        this.drawDivideLine = drawDivideLine;
        this.drawDelRects = drawDelRects;
        this.drawMeasureCircles = drawMeasureCircles;
    }

    /**
     * Get if the slide should be drawn.
     *
     * @return if the slide should be drawn
     */
    public boolean isDrawSlide() {
        return drawSlide;
    }

    /**
     * Get if the spotfields should be drawn.
     *
     * @return if the spotfields should be drawn
     */
    public boolean isDrawSpotFields() {
        return drawSpotFields;
    }

    /**
     * Get if the spotfield annotations should be drawn.
     *
     * @return if the spotfields annotations should be drawn
     */
    public boolean isDrawSpotFieldAnnotation() {
        return drawSpotFieldAnnotation;
    }

    /**
     * Get if the divide line between the spotfields should be drawn.
     *
     * @return if the divide line should be drawn
     */
    public boolean isDrawDivideLine() {
        return drawDivideLine;
    }

    /**
     * Get if the deletion rects should be drawn.
     *
     * @return if the deletion rects should be drawn
     */
    public boolean isDrawDelRects() {
        return drawDelRects;
    }

    /**
     * Get if the measurement circles should be drawn.
     *
     * @return if the measurement circles should be drawn.
     */
    public boolean isDrawMeasureCircles() {
        return drawMeasureCircles;
    }
}
