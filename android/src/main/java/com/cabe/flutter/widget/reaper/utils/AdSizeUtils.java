package com.cabe.flutter.widget.reaper.utils;

import com.fighter.loader.adspace.ReaperBannerPositionAdSpace;

public class AdSizeUtils {
    private static final String[] AD_SIZE_STRING_ITEMS = new String[]{
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W690xH388.toString(),
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH400.toString(),
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH300.toString(),
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH260.toString(),
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH150.toString(),
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W640xH100.toString(),
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH90.toString()};

    private static final ReaperBannerPositionAdSpace.AdSize[] AD_SIZE_ITEMS = new ReaperBannerPositionAdSpace.AdSize[]{
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W690xH388,
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH400,
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH300,
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH260,
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH150,
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W640xH100,
            ReaperBannerPositionAdSpace.AdSize.AD_SIZE_W600xH90};

    public static String[] getAdSizeItems() {
        return AD_SIZE_STRING_ITEMS;
    }

    public static ReaperBannerPositionAdSpace.AdSize getAdSize(int position) {
        return AD_SIZE_ITEMS[position];
    }
}