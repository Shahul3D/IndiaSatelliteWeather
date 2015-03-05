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

import com.noveogroup.android.log.Log;
//TODO: Remove redundant import.
import com.shahul3d.indiasatelliteweather.views.MapViewFragment;
import com.shahul3d.indiasatelliteweather.views.MapViewFragment_;


public class TouchImagePageAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 5;
    private String titles[];

    public TouchImagePageAdapter(FragmentManager fm, String[] titles2) {
        super(fm);
        titles = titles2;
    }

    @Override
    public Fragment getItem(int position) {
        MapViewFragment fragment = MapViewFragment_.builder()
                .pageNumber(position)
                .build();
        return fragment;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

}