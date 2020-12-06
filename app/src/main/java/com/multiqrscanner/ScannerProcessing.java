package com.multiqrscanner;

import android.graphics.Canvas;
import android.graphics.Matrix;

import boofcv.alg.color.ColorFormat;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;


/**
 * Processes input images and visualizes the result. If the activity has been configured to
 * proccess more than one thread then this class needs to be thread safe
 * @param <T>
 */
public interface ScannerProcessing<T extends ImageBase<T>> {
    /**
     * Called before {@link #process} and once the camera has been fully initialized and
     * all these parameters are known.
     */
    void initialize(int imageWidth, int imageHeight, int sensorOrientation);

    /**
     * Invoked by Android GUI thread.  Should be run as fast as possible to avoid making the GUI
     * feel sluggish.
     *
     * Called only after {@link #initialize} has been called.
     *
     * @param canvas Use this canvas to draw results onto.
     * @param imageToView Specifies the transform from input image coordinates to the view it's
     *                    displayed in
     */
    void onDraw(Canvas canvas, Matrix imageToView);

    /**
     * Process the image. This is where heavy computations can be done.
     *
     * This will not be called unless {@link #initialize} has been called.
     */
    void process(T input);

    /**
     * Requess that all processing stop and that it release all allocated resources. This can
     * be completed after the function has been called.
     *
     * This can be called before {@link #initialize} or {@link #process} has been called.
     */
    void stop();

    /**
     * Returns true if the implementation is thread safe. Used as a sanity check to make sure
     * nasty hard to debug errors don't appear. If you don't know what you are doing return false.
     */
    boolean isThreadSafe();

    ImageType<T> getImageType();

    ColorFormat getColorFormat();
}
