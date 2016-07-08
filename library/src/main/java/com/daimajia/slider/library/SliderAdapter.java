package com.daimajia.slider.library;

import android.content.Context;
import android.provider.SyncStateContract;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import java.util.ArrayList;

/**
 * A slider adapter
 */
public class SliderAdapter extends PagerAdapter implements BaseSliderView.ImageLoadListener{

    private Context mContext;
    private ArrayList<BaseSliderView> mImageContents;

    public SliderAdapter(Context context){
        mContext = context;
        mImageContents = new ArrayList<BaseSliderView>();
    }

    public <T extends BaseSliderView> void addSlider(T slider){
        slider.setOnImageLoadListener(this);
        mImageContents.add(slider);
        notifyDataSetChanged();
    }

    public BaseSliderView getSliderView(int position){
        if(position < 0 || position >= mImageContents.size()){
            return null;
        }else{
            return mImageContents.get(position);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        // Consider it only called when notifyDataSetChange, so added item will not change in this case.
        // http://stackoverflow.com/questions/10728076/when-should-getitemposition-consider-changes-of-an-items-position
        return POSITION_NONE;
    }

    public <T extends BaseSliderView> void removeSlider(T slider){
        if(mImageContents.contains(slider)){
            mImageContents.remove(slider);
            notifyDataSetChanged();
        }
    }

    public void removeSliderAt(int position){
        if(mImageContents.size() > position){
            mImageContents.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeAllSliders(){
        mImageContents.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageContents.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        BaseSliderView b = mImageContents.get(position);
        // Give the slider to free resource;
        b.onDestroyView();
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseSliderView b = mImageContents.get(position);
        View v = b.getView();
        container.addView(v);
        return v;
    }

    @Override
    public void onStart(BaseSliderView target) {

    }

    /**
     * When image download error, then remove.
     * @param result
     * @param target
     */
    @Override
    public void onEnd(boolean result, BaseSliderView target) {
        if(target.isErrorDisappear() == false || result == true){
            return;
        }
        for (BaseSliderView slider: mImageContents){
            if(slider.equals(target)){
                removeSlider(target);
                break;
            }
        }
    }

}
