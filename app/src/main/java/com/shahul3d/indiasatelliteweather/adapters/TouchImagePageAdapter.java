/*
 * ******************************************************************************
 *  * Copyright (c) 2015.  Shahul Hameed.
 *  *
 *  * Licensed under GNU GENERAL PUBLIC LICENSE;
 *  * you may not use this file except in compliance with the License.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  ******************************************************************************
 */

package com.shahul3d.indiasatelliteweather.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.views.MapViewFragment;
import com.shahul3d.indiasatelliteweather.views.MapViewFragment_;
//TODO: Remove redundant import.


public class TouchImagePageAdapter extends FragmentStatePagerAdapter {

    int totalPageCount;
    private String titles[];
    AppConstants.MapType mapType;

    public TouchImagePageAdapter(FragmentManager fm, String[] titles, AppConstants.MapType mapType) {
        super(fm);
        this.titles = titles;
        totalPageCount = titles.length;
        this.mapType = mapType;
    }

    @Override
    public Fragment getItem(int position) {
        MapViewFragment fragment = MapViewFragment_.builder()
                .pageNumber(position)
                .mapType(mapType)
                .build();
        return fragment;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return totalPageCount;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

}