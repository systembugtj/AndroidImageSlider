package com.daimajia.slider.library.SliderTypes;

import android.widget.ImageView;

import java.io.File;

import io.reactivex.Observable;

/**
 * Created by albert.li on 5/1/17.
 */

public interface SlideImageLoader {
    enum ScaleType{
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    Observable load(ImageView view, ScaleType scaleType, String url);
    Observable load(ImageView view, ScaleType scaleType, File file);
    Observable load(ImageView view, ScaleType scaleType, int res);
}
