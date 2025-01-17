/*
 * Copyright (C) 2021 The LineageOS Project
 * Copyright (C) 2021 Shift GmbH
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
package com.android.launcher3.p404.icon;

import static android.graphics.Paint.DITHER_FLAG;
import static android.graphics.Paint.FILTER_BITMAP_FLAG;

import static com.android.launcher3.icons.ShadowGenerator.BLUR_FACTOR;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;

import androidx.annotation.NonNull;

import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.FixedScaleDrawable;
import com.android.launcher3.icons.ShadowGenerator;

public class P404IconFactory extends BaseIconFactory {

    private final IconPackStore mIconPackStore;
    
    protected static final boolean ATLEAST_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public P404IconFactory(Context context, int fillResIconDpi, int iconBitmapSize) {
        super(context, fillResIconDpi, iconBitmapSize);
        mIconPackStore = new IconPackStore(context);
    }

    public P404IconFactory(Context context, int fillResIconDpi, int iconBitmapSize,
            boolean shapeDetection) {
        super(context, fillResIconDpi, iconBitmapSize, shapeDetection);
        mIconPackStore = new IconPackStore(context);
    }

    @Override
    protected Drawable normalizeAndWrapToAdaptiveIcon(@NonNull Drawable icon,
            boolean shrinkNonAdaptiveIcons, RectF outIconBounds, float[] outScale) {
        if (icon == null) {
            return null;
        }
        float scale = 1f;

        final boolean defaultIcons = mIconPackStore.isUsingSystemIcons();
        if (shrinkNonAdaptiveIcons && ATLEAST_OREO && defaultIcons) {
            if (mWrapperIcon == null) {
                mWrapperIcon = mContext.getDrawable(com.android.launcher3.icons.R.drawable.adaptive_icon_drawable_wrapper)
                        .mutate();
            }
            AdaptiveIconDrawable dr = (AdaptiveIconDrawable) mWrapperIcon;
            dr.setBounds(0, 0, 1, 1);
            boolean[] outShape = new boolean[1];
            scale = getNormalizer().getScale(icon, outIconBounds, dr.getIconMask(), outShape);
            if (!(icon instanceof AdaptiveIconDrawable) && !outShape[0]) {
                FixedScaleDrawable fsd = ((FixedScaleDrawable) dr.getForeground());
                fsd.setDrawable(icon);
                fsd.setScale(scale);
                icon = dr;
                scale = getNormalizer().getScale(icon, outIconBounds, null, null);

                ((ColorDrawable) dr.getBackground()).setColor(mWrapperBackgroundColor);
            }
        } else {
            scale = getNormalizer().getScale(icon, outIconBounds, null, null);
        }

        outScale[0] = scale;
        return icon;
    }
}
