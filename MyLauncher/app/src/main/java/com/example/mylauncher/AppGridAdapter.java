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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * The adapter that populates the grid view with apps.
 */
final class AppGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AppGridAdapter";
    private final Context mContext;
    private final LayoutInflater mInflater;

    private List<AppMetaData> mApps;

    AppGridAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    void setAllApps(@Nullable List<AppMetaData> apps) {
        mApps = apps;
      //  sortAllApps();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.app_item, parent, /* attachToRoot= */ false);
            return new AppItemViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppMetaData app = mApps.get(position);
        ((AppItemViewHolder) holder).bind(app);
    }

    @Override
    public int getItemCount() {
        return (mApps == null ? 0 : mApps.size()) ;
    }

    /*private void sortAllApps() {
        if (mApps != null) {
            Collections.sort(mApps, AppLauncherUtils.ALPHABETICAL_COMPARATOR);
        }
    }*/
}
