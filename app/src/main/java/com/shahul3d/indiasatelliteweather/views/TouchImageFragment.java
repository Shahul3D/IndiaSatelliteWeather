package com.shahul3d.indiasatelliteweather.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.events.TestEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;

@EFragment(R.layout.touch_image_fragment)
public class TouchImageFragment extends Fragment {
    @FragmentArg
    int pageNumber;

    @ViewById
    SubsamplingScaleImageView touchImage;

    EventBus bus = EventBus.getDefault();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @AfterViews
    void calledAfterViewInjection() {
        touchImage.setImageAsset(chooseImage(pageNumber));
    }

    String chooseImage(int pageNumber) {
        String defaultImage = "map_temprature.jpg";
        if (pageNumber == 1) {
            defaultImage = "map_infra_red.jpg";
        } else if (pageNumber == 2) {
            defaultImage = "map_color_composite.jpg";
        } else if (pageNumber == 3) {
            defaultImage = "map_water_vapor.jpg";
        }
        Log.a("Loading: %s", defaultImage);
        return defaultImage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.a("Refresh clicked..");
            bus.post(new TestEvent("Shahul"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//        touchImage.setImageResource(chooseImage(pageNumber));
//        touchImage.setImageBitmap(chooseImage(pageNumber));
//        loadImage(chooseImage(pageNumber));
//        setImageInViewPager(chooseImage(pageNumber));
//    }

//    @Background
//    void loadImage(int imageResource) {
//        Bitmap loadedImage = decodeSampledBitmapFromResource(getResources(), imageResource);
//        applyImage(loadedImage);
//    }

//    @UiThread
//    void applyImage(Bitmap image) {
//        touchImage.setImageBitmap(image);
//    }

    /*

        public void setImageInViewPager(int itemData) {
            try {
                //if image size is too large. Need to scale as below code.

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                myBitmap = BitmapFactory.decodeResource(getResources(), itemData,
                        options);
                if (options.outWidth > 3000 || options.outHeight > 2000) {
                    options.inSampleSize = 4;
                } else if (options.outWidth > 2000 || options.outHeight > 1500) {
                    options.inSampleSize = 3;
                } else if (options.outWidth > 1000 || options.outHeight > 1000) {
                    options.inSampleSize = 2;
                }
                options.inJustDecodeBounds = false;
                myBitmap = BitmapFactory.decodeResource(getResources(), itemData,
                        options);
                if (myBitmap != null) {
                    try {
                        if (touchImage != null) {
                            touchImage.setImageBitmap(myBitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                System.gc();
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (myBitmap != null) {
                myBitmap.recycle();
                myBitmap = null;
            }
        }*/
    public void onEvent(Object e) {
    }
}