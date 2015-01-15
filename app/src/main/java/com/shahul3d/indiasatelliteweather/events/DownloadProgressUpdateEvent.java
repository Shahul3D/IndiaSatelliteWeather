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

package com.shahul3d.indiasatelliteweather.events;

public class DownloadProgressUpdateEvent {
    public int mapID;
    public int progress;

    public DownloadProgressUpdateEvent(int mapID, int downloadProgress) {
        this.mapID = mapID;
        progress = downloadProgress;
    }

    public int getMapType() {
        return mapID;
    }

    public int getProgress(){
        return progress;
    }
}