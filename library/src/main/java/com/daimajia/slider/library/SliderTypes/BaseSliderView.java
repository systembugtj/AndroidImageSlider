package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.slider.library.R;
import com.google.common.base.Optional;

import java.io.File;

import io.reactivex.Observable;

/**
 * When you want to make your own slider view, you must extends from this class.
 * BaseSliderView provides some useful methods.
 * I provide two example: {@link com.daimajia.slider.library.SliderTypes.DefaultSliderView} and
 * {@link com.daimajia.slider.library.SliderTypes.TextSliderView}
 * if you want to show progressbar, you just need to set a progressbar id as @+id/loading_bar.
 */
public abstract class BaseSliderView {
    protected Context mContext;

    private Bundle mBundle;

    private String mUrl;
    private File mFile;
    private int mRes;
    private Optional<SlideImageLoader> mImageLoader = Optional.absent();

    protected OnSliderClickListener mOnSliderClickListener;

    private boolean mErrorDisappear;

    private ImageLoadListener mLoadListener;

    private String mDescription;

    /**
     * Scale type of the image.
     */
    private SlideImageLoader.ScaleType mScaleType = SlideImageLoader.ScaleType.Fit;

    protected BaseSliderView(Context context) {
        mContext = context;
    }

    /*
     * set imager loader
     * @param loader Image loading support
     */
    public BaseSliderView loader(SlideImageLoader loader){
        mImageLoader = Optional.of(loader);
        return this;
    }

    /**
     * determine whether remove the image which failed to download or load from file
     * @param disappear
     * @return
     */
    public BaseSliderView errorDisappear(boolean disappear){
        mErrorDisappear = disappear;
        return this;
    }

    /**
     * the description of a slider image.
     * @param description
     * @return
     */
    public BaseSliderView description(String description){
        mDescription = description;
        return this;
    }

    /**
     * set a url as a image that preparing to load
     * @param url
     * @return
     */
    public BaseSliderView image(String url){
        if(mFile != null || mRes != 0){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**
     * set a file as a image that will to load
     * @param file
     * @return
     */
    public BaseSliderView image(File file){
        if(mUrl != null || mRes != 0){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mFile = file;
        return this;
    }

    public BaseSliderView image(int res){
        if(mUrl != null || mFile != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    /**
     * lets users add a bundle of additional information
     * @param bundle
     * @return
     */
    public BaseSliderView bundle(Bundle bundle){
        mBundle = bundle;
        return this;
    }

    public String getUrl(){
        return mUrl;
    }

    public boolean isErrorDisappear(){
        return mErrorDisappear;
    }

    public String getDescription(){
        return mDescription;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * set a slider image click listener
     * @param l
     * @return
     */
    public BaseSliderView setOnSliderClickListener(OnSliderClickListener l){
        mOnSliderClickListener = l;
        return this;
    }

    /**
     * When you want to implement your own slider view, please call this method in the end in `getView()` method
     * @param v the whole view
     * @param targetImageView where to place image
     */
    protected void bindEventAndShow(final View v, ImageView targetImageView){
        final BaseSliderView me = this;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mOnSliderClickListener != null){
                mOnSliderClickListener.onSliderClick(me);
            }
            }
        });

        if (targetImageView == null)
            return;

        if (mLoadListener != null) {
            mLoadListener.onStart(me);
        }

        if (mImageLoader.isPresent()) {
            SlideImageLoader loader = mImageLoader.get();
            Observable ob;
            if(mUrl!=null){
                ob = loader.load(targetImageView, mScaleType, mUrl);
            }else if(mFile != null){
                ob = loader.load(targetImageView, mScaleType, mFile);
            }else if(mRes != 0){
                ob = loader.load(targetImageView, mScaleType, mRes);
            }else{
                return;
            }
            ob.subscribe(aVoid -> {
                if (v.findViewById(R.id.loading_bar) != null) {
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
            }, error -> {
                if(mLoadListener != null){
                    mLoadListener.onEnd(false,me);
                }
                if(v.findViewById(R.id.loading_bar) != null){
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
            });
        }
   }



    public BaseSliderView setScaleType(SlideImageLoader.ScaleType type){
        mScaleType = type;
        return this;
    }

    public SlideImageLoader.ScaleType getScaleType(){
        return mScaleType;
    }

    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     * @return
     */
    public abstract View getView();

    /**
     * set a listener to get a message , if load error.
     * @param l
     */
    public void setOnImageLoadListener(ImageLoadListener l){
        mLoadListener = l;
    }

    public interface OnSliderClickListener {
        public void onSliderClick(BaseSliderView slider);
    }

    /**
     * when you have some extra information, please put it in this bundle.
     * @return
     */
    public Bundle getBundle(){
        return mBundle;
    }

    public interface ImageLoadListener{
        public void onStart(BaseSliderView target);
        public void onEnd(boolean result,BaseSliderView target);
    }

    /*
     * Allow slider to free resource.
     */
    public void onDestroyView() {

    }
}
