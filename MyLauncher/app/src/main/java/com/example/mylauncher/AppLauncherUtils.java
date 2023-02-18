/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mylauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Util class that contains helper method used by app launcher classes.
 */
public class AppLauncherUtils {
    private static final String TAG = "AppLauncherUtils";

    private AppLauncherUtils() {
    }

    /**
     * Comparator for {@link AppMetaData} that sorts the list
     * by the "displayName" property in ascending order.
     */
    static final Comparator<AppMetaData> ALPHABETICAL_COMPARATOR = Comparator
            .comparing(AppMetaData::getDisplayName, String::compareToIgnoreCase);


    static void launchApp(Context context, Intent intent) {
        context.startActivity(intent);
    }

    /**
     * Bundles application and services info.
     */
    static class LauncherAppsInfo {
        private final Map<ComponentName, AppMetaData> mLaunchables;

        LauncherAppsInfo(@NonNull Map<ComponentName, AppMetaData> launchablesMap
        ) {
            mLaunchables = launchablesMap;
        }

        /**
         * Returns the {@link AppMetaData} for the given componentName.
         */
        @Nullable
        AppMetaData getAppMetaData(ComponentName componentName) {
            return mLaunchables.get(componentName);
        }

        /**
         * Returns a new list of all launchable components' {@link AppMetaData}.
         */
        @NonNull
        List<AppMetaData> getLaunchableComponentsList() {
            return new ArrayList<>(mLaunchables.values());
        }
    }

    private final static LauncherAppsInfo EMPTY_APPS_INFO = new LauncherAppsInfo(
            Collections.emptyMap());


    // get all the apps from the OS and send it to the client
    @NonNull
    static LauncherAppsInfo getLauncherApps(
            LauncherApps launcherApps,
            PackageManager packageManager
    ) {
        if (launcherApps == null || packageManager == null
        ) {
            return EMPTY_APPS_INFO;
        }
        // get the launchable activities using launcherApps class.
        // myUserHandle() Returns this process's user handle. This is the user the process is running under.
        List<LauncherActivityInfo> availableActivities =
                launcherApps.getActivityList(null, Process.myUserHandle());

        int launchablesSize = availableActivities.size();
        Map<ComponentName, AppMetaData> launchablesMap = new HashMap<>(launchablesSize);
        Set<String> mEnabledPackages = new ArraySet<>(launchablesSize);

        // Loop through the available activities and build the AppMeta Data
        for (LauncherActivityInfo info : availableActivities) {
            // Get the component object
            ComponentName componentName = info.getComponentName();
            Log.d(TAG, "getLauncherApps: package name -  " + componentName.getPackageName());
            String packageName = componentName.getPackageName();
            // Add the enabled packages
            mEnabledPackages.add(packageName);
            // Create the Launch intent for the App
            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setComponent(componentName)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Build the AppMetaData
            AppMetaData appMetaData = new AppMetaData(
                    info.getLabel(),
                    componentName,
                    info.getBadgedIcon(0),
                    context -> AppLauncherUtils.launchApp(context, intent),
                    null);
            // Put it in the Launchables Map
            launchablesMap.put(componentName, appMetaData);
        }
        return new LauncherAppsInfo(launchablesMap);
    }
}
