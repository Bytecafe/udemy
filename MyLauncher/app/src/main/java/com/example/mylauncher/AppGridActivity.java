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

import android.app.Activity;
import android.app.usage.UsageStatsManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Launcher activity that shows a grid of apps.
 */
public class AppGridActivity extends Activity  {
    private static final String TAG = "AppGridActivity";
    private static final String MODE_INTENT_EXTRA = "com.android.car.carlauncher.mode";

    private int mColumnNumber;
    private boolean mShowAllApps = true;
    private AppGridAdapter mGridAdapter;
    private PackageManager mPackageManager;
    private AppInstallUninstallReceiver mInstallUninstallReceiver;

    /**
     * enum to define the state of display area possible.
     * CONTROL_BAR state is when only control bar is visible.
     * FULL state is when display area hosting default apps  cover the screen fully.
     * DEFAULT state where maps are shown above DA for default apps.
     */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_grid_activity);

        //get packagemanager instance
        mPackageManager = getPackageManager();
        //Number of columns in grid
        mColumnNumber = 4;
        mGridAdapter = new AppGridAdapter(this);
        RecyclerView gridView = requireViewById(R.id.apps_grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mColumnNumber);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setAdapter(mGridAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Using onResume() to refresh most recently used apps because we want to refresh even if
        // the app being launched crashes/doesn't cover the entire screen.
        updateAppsLists();
    }

    /** Updates the list of all apps, and the list of the most recently used ones. */
    private void updateAppsLists() {
        AppLauncherUtils.LauncherAppsInfo appsInfo = AppLauncherUtils.getLauncherApps(
                getSystemService(LauncherApps.class),
                mPackageManager);
        mGridAdapter.setAllApps(appsInfo.getLaunchableComponentsList());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register broadcast receiver for package installation and uninstallation
        mInstallUninstallReceiver = new AppInstallUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mInstallUninstallReceiver, filter);

        // Connect to car service

    }

    @Override
    protected void onStop() {
        super.onStop();
        // disconnect from app install/uninstall receiver
        if (mInstallUninstallReceiver != null) {
            unregisterReceiver(mInstallUninstallReceiver);
            mInstallUninstallReceiver = null;
        }

    }

    private class AppInstallUninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getSchemeSpecificPart();

            if (TextUtils.isEmpty(packageName)) {
                Log.e(TAG, "System sent an empty app install/uninstall broadcast");
                return;
            }

            updateAppsLists();
        }
    }
}
