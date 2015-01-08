package com.shahul3d.indiasatelliteweather.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;

public class AnimationUtil {
    private static void refreshAnimation(Context mContext, MenuItem refreshMenuItem, boolean refreshAnimation) {
        if (refreshMenuItem != null) {
            if (refreshAnimation) {
                LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView mImageView = (ImageView) mInflater.inflate(R.layout.refresh_action_view, null);

                Animation rotation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
                rotation.setRepeatCount(Animation.INFINITE);
                mImageView.startAnimation(rotation);

                refreshMenuItem.setActionView(mImageView);

            } else {

                // Stop refresh animation
                if (refreshMenuItem != null) {
                    if (refreshMenuItem.getActionView() != null) {
                        refreshMenuItem.getActionView().clearAnimation();
                        refreshMenuItem.setActionView(null);
                    }
                }
            }
        } else {
            Log.d("refreshMenuItem was null!");
        }
    }

    public static void startRefreshAnimation(Context mContext, MenuItem refreshMenuItem) {
        refreshAnimation(mContext, refreshMenuItem, true);
    }

    public static void stopRefreshAnimation(Context mContext, MenuItem refreshMenuItem) {
        refreshAnimation(mContext, refreshMenuItem, false);
    }

}
