package com.cabe.flutter.widget.reaper.video;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.fighter.loader.ReaperAdSDK;
import com.fighter.loader.adspace.ReaperRewardedVideoAdSpace;
import com.fighter.loader.listener.RewardeVideoCallBack;
import com.fighter.loader.listener.RewardedVideoAdListener;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class ADReaperRewardVideo implements PlatformView, MethodChannel.MethodCallHandler {
    public static String VIEW_TYPE_ID = "com.cabe.flutter.widget.AdReaperRewardVideo";
    private static final String TAG = "ADReaperInteraction";

    private final MethodChannel methodChannel;
    private final FrameLayout containerLayout;
    private RewardeVideoCallBack mRewardeVideoCallBack;

    public ADReaperRewardVideo(Context context, BinaryMessenger messenger, int viewID, Object args) {
        containerLayout = new FrameLayout(context);
        methodChannel = new MethodChannel(messenger, VIEW_TYPE_ID + "#" + viewID);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        Log.w(TAG, "onMethodCall: method: " + methodCall.method + " arguments: " + methodCall.arguments);
        switch (methodCall.method) {
            case "request":
                request((Map<String, String>) methodCall.arguments, result);
                result.success(true);
                break;
            case "showAd":
                showAd(result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void request(Map<String, String> params, MethodChannel.Result result) {
        if (!ReaperAdSDK.isInited()) {
            String reason = "ReaperAdSDK is not init";
            Log.e(TAG, reason);
            result.error("1", reason, null);
            return;
        }

        if (containerLayout.getChildCount() > 0) {
            containerLayout.removeAllViews();
        }

        final String adPositionId = params.get("adPositionId");
        int orientation;
        try {
            orientation = Integer.parseInt(params.get("orientation"));
        } catch (Exception e) {
            orientation = 1;
        }
        final int direction = orientation;

        new Thread() {
            @Override
            public void run() {
                // ??????PV
                ReaperAdSDK.getLoadManager().reportPV(adPositionId);
                // ????????????????????????
                ReaperRewardedVideoAdSpace adSpace = new ReaperRewardedVideoAdSpace(adPositionId);
                adSpace.setOrientation(direction);
                ReaperAdSDK.getLoadManager().loadRewardedVideoAd(adSpace, new RewardedVideoAdListener() {
                    @Override
                    public void onFailed(String requestId, String errMsg) {
                        Log.e(TAG, "onFailed, requestId: " + requestId + ", errMsg: " + errMsg);

                        Map<String, Object> params = new HashMap<>();
                        params.put("requestId", requestId);
                        params.put("errMsg", errMsg);
                        methodChannel.invokeMethod("onFailed", params);
                    }
                    @Override
                    public void onRewardVideoCached() {
                        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        Log.i(TAG, "onRewardVideoCached");
                        methodChannel.invokeMethod("onRewardVideoCached", null);
                    }
                    @Override
                    public void onRewardVideoAdLoad(RewardeVideoCallBack rewardeVideoCallBack) {
                        Log.i(TAG, "onRewardVideoAdLoad");
                        // ????????????????????????????????????????????????url??????????????????????????????????????????????????????????????????????????????????????????????????????
                        mRewardeVideoCallBack = rewardeVideoCallBack;
                        methodChannel.invokeMethod("onRewardVideoAdLoad", null);
                    }
                    @Override
                    public void onAdShow() {
                        Log.i(TAG, "onAdShow");
                        // ??????????????????
                        methodChannel.invokeMethod("onAdShow", null);
                    }
                    @Override
                    public void onAdShowError(String errMsg) {
                        // ????????????????????????
                        Log.e(TAG, "onAdShowError : " + errMsg);

                        Map<String, Object> params = new HashMap<>();
                        params.put("errMsg", errMsg);
                        methodChannel.invokeMethod("onAdShowError", params);
                    }
                    @Override
                    public void onAdVideoBarClick() {
                        Log.i(TAG, "onAdVideoBarClick");
                        // ??????????????????
                        methodChannel.invokeMethod("onAdVideoBarClick", null);
                    }
                    @Override
                    public void onAdClose() {
                        Log.i(TAG, "onAdClose");
                        // ????????????
                        methodChannel.invokeMethod("onAdClose", null);
                    }
                    @Override
                    public void onVideoComplete() {
                        Log.i(TAG, "onVideoComplete");
                        // ????????????????????????
                        methodChannel.invokeMethod("onVideoComplete", null);
                    }
                    @Override
                    public void onVideoError() {
                        Log.i(TAG, "onVideoError");
                        // ??????????????????
                        methodChannel.invokeMethod("onVideoError", null);
                    }
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Log.i(TAG, "onRewardVerify. rewardVerify: " + rewardVerify + ", rewardAmount: " + rewardAmount + ", rewardName: " + rewardName);
                        // ?????????????????????????????????????????????rewardVerify ????????????, rewardAmount ????????????, rewardName   ????????????

                        Map<String, Object> params = new HashMap<>();
                        params.put("rewardVerify", rewardVerify);
                        params.put("rewardAmount", rewardAmount);
                        params.put("rewardName", rewardName);
                        methodChannel.invokeMethod("onRewardVerify", params);
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.i(TAG, "onSkippedVideo");
                        // ??????????????????
                        methodChannel.invokeMethod("onSkippedVideo", null);
                    }
                });
            }
        }.start();
    }
    
    private void showAd(MethodChannel.Result result) {
        if (mRewardeVideoCallBack != null && mRewardeVideoCallBack.isRewardedVideoAdLoaded()) {
            //step6:???????????????????????????
            mRewardeVideoCallBack.showRewardedVideoAd((Activity) containerLayout.getContext());
            result.success(0);
        } else {
            String errInfo = "showVideoAd mTtRewardVideoAd IS NULL";
            Log.i(TAG, errInfo);
            result.error("0", errInfo, null);
        }
    }

    @Override
    public View getView() {
        return containerLayout;
    }

    @Override public void onFlutterViewAttached(@NonNull View flutterView) {}
    @Override public void onFlutterViewDetached() {}
    @Override public void dispose() {}
    @Override public void onInputConnectionLocked() {}
    @Override public void onInputConnectionUnlocked() {}
}
