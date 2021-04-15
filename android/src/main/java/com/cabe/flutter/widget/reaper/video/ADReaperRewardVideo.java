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
                // 上报PV
                ReaperAdSDK.getLoadManager().reportPV(adPositionId);
                // 请求激励视频广告
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
                        // 视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞
                        Log.i(TAG, "onRewardVideoCached");
                        methodChannel.invokeMethod("onRewardVideoCached", null);
                    }
                    @Override
                    public void onRewardVideoAdLoad(RewardeVideoCallBack rewardeVideoCallBack) {
                        Log.i(TAG, "onRewardVideoAdLoad");
                        // 视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验
                        mRewardeVideoCallBack = rewardeVideoCallBack;
                        methodChannel.invokeMethod("onRewardVideoAdLoad", null);
                    }
                    @Override
                    public void onAdShow() {
                        Log.i(TAG, "onAdShow");
                        // 视频广告曝光
                        methodChannel.invokeMethod("onAdShow", null);
                    }
                    @Override
                    public void onAdShowError(String errMsg) {
                        // 视频广告曝光失败
                        Log.e(TAG, "onAdShowError : " + errMsg);

                        Map<String, Object> params = new HashMap<>();
                        params.put("errMsg", errMsg);
                        methodChannel.invokeMethod("onAdShowError", params);
                    }
                    @Override
                    public void onAdVideoBarClick() {
                        Log.i(TAG, "onAdVideoBarClick");
                        // 视频广告点击
                        methodChannel.invokeMethod("onAdVideoBarClick", null);
                    }
                    @Override
                    public void onAdClose() {
                        Log.i(TAG, "onAdClose");
                        // 广告关闭
                        methodChannel.invokeMethod("onAdClose", null);
                    }
                    @Override
                    public void onVideoComplete() {
                        Log.i(TAG, "onVideoComplete");
                        // 视频播放完成回调
                        methodChannel.invokeMethod("onVideoComplete", null);
                    }
                    @Override
                    public void onVideoError() {
                        Log.i(TAG, "onVideoError");
                        // 视频播放出错
                        methodChannel.invokeMethod("onVideoError", null);
                    }
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Log.i(TAG, "onRewardVerify. rewardVerify: " + rewardVerify + ", rewardAmount: " + rewardAmount + ", rewardName: " + rewardName);
                        // 视频播放完成后，奖励验证回调。rewardVerify 是否有效, rewardAmount 奖励数量, rewardName   奖励名称

                        Map<String, Object> params = new HashMap<>();
                        params.put("rewardVerify", rewardVerify);
                        params.put("rewardAmount", rewardAmount);
                        params.put("rewardName", rewardName);
                        methodChannel.invokeMethod("onRewardVerify", params);
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.i(TAG, "onSkippedVideo");
                        // 跳过视频播放
                        methodChannel.invokeMethod("onSkippedVideo", null);
                    }
                });
            }
        }.start();
    }
    
    private void showAd(MethodChannel.Result result) {
        if (mRewardeVideoCallBack != null && mRewardeVideoCallBack.isRewardedVideoAdLoaded()) {
            //step6:在获取到广告后展示
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
