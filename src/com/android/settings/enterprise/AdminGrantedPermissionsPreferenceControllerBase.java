/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settings.enterprise;

import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.Preference;

import com.android.settings.R;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.core.PreferenceController;
import com.android.settings.overlay.FeatureFactory;

public abstract class AdminGrantedPermissionsPreferenceControllerBase extends PreferenceController {

    private final String[] mPermissions;
    private final String mPermissionGroup;
    private final ApplicationFeatureProvider mFeatureProvider;

    public AdminGrantedPermissionsPreferenceControllerBase(Context context,
                                                           String[] permissions,
                                                           String permissionGroup) {
        super(context);
        mPermissions = permissions;
        mPermissionGroup = permissionGroup;
        mFeatureProvider = FeatureFactory.getFactory(context)
                .getApplicationFeatureProvider(context);
    }

    @Override
    public void updateState(Preference preference) {
        mFeatureProvider.calculateNumberOfAppsWithAdminGrantedPermissions(mPermissions,
                (num) -> {
                    if (num == 0) {
                        preference.setVisible(false);
                    } else {
                        preference.setVisible(true);
                        preference.setSummary(mContext.getResources().getQuantityString(
                                R.plurals.enterprise_privacy_number_packages_actionable, num, num));
                    }
                });
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!getPreferenceKey().equals(preference.getKey())) {
            return false;
        }
        final Intent intent = new Intent(Intent.ACTION_MANAGE_PERMISSION_APPS)
                .putExtra(Intent.EXTRA_PERMISSION_NAME, mPermissionGroup);
        mContext.startActivity(intent);
        return true;
    }
}