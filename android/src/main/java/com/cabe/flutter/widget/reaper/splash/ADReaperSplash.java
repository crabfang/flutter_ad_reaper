package com.cabe.flutter.widget.reaper.splash;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.cabe.flutter.widget.reaper.utils.DimenUtils;
import com.fighter.loader.ReaperAdSDK;
import com.fighter.loader.adspace.ReaperSplashAdSpace;
import com.fighter.loader.listener.SplashViewListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class ADReaperSplash implements PlatformView, MethodChannel.MethodCallHandler {
    public static String VIEW_TYPE_ID = "com.cabe.flutter.widget.AdReaperSplash";
    private static final String TAG = "ADReaperSplash";

    private final MethodChannel methodChannel;
    private final FrameLayout containerLayout;

    public ADReaperSplash(Context context, BinaryMessenger messenger, int viewID, Object args) {
        containerLayout = new FrameLayout(context);
        methodChannel = new MethodChannel(messenger, VIEW_TYPE_ID + "#" + viewID);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        Log.w(TAG, "onMethodCall: method: " + methodCall.method + " arguments: " + methodCall.arguments);
        if ("request".equals(methodCall.method)) {
            request((Map<String, String>) methodCall.arguments, result);
            result.success(true);
        } else {
            result.notImplemented();
        }
    }

    private void request(final Map<String, String> params, MethodChannel.Result result) {
        if (!ReaperAdSDK.isInited()) {
            String reason = "ReaperAdSDK is not init";
            Log.e(TAG, reason);
            result.error("1", reason, null);
            return;
        }

        if (containerLayout.getChildCount() > 0) {
            containerLayout.removeAllViews();
        }

        new Thread() {
            @Override
            public void run() {
                String adPositionId = params.get("adPositionId");
                int splashHeight = DimenUtils.getScreenHeight(containerLayout.getContext());
                try {
                    splashHeight = Integer.parseInt(params.get("splashHeight"));
                } catch (Exception ignored) {}
                int optimalHeightWithPX = calculateOptimalHeightWithPX(containerLayout.getContext(), splashHeight);
                int skipSecond = 5;
                try {
                    skipSecond = Integer.parseInt(params.get("skipSecond"));
                } catch (Exception ignored) {}
                int timeout = 5;
                try {
                    timeout = Integer.parseInt(params.get("timeout"));
                } catch (Exception ignored) {}

                // 上报PV
                ReaperAdSDK.getLoadManager().reportPV(adPositionId);
                // 请求开屏广告
                ReaperSplashAdSpace adSpace = new ReaperSplashAdSpace(adPositionId);
                // 设置广告容器的尺寸，方便去匹配最优尺寸素材
                adSpace.setAdViewHeight(optimalHeightWithPX);
                // 如果广告容器宽度为屏幕宽度，可以不进行设置
                adSpace.setAdViewWidth(DimenUtils.getScreenWidth(containerLayout.getContext()));
                // 不设置跳过时间默认为5秒，如果是视频广告此设置不生效，跳过时间为视频时长
                // 三方广告源也不生效，三方广告源默认为5秒
                adSpace.setSkipTime(skipSecond);
                // 设置请求超时时间，如果请求时间超过超时时间时，回调onSplashAdFailed方法，失败原因为"request time out"
                adSpace.setTimeout(timeout);
                ReaperAdSDK.getLoadManager().loadSplashAd(adSpace, (Activity) containerLayout.getContext(), containerLayout, new SplashViewListener() {
                    @Override
                    public void onSplashAdClick() {
                        Log.i(TAG, "onSplashAdClick. 点击广告");
                        methodChannel.invokeMethod("onSplashAdClick", null);
                    }
                    @Override
                    public void onJumpClicked() {
                        Log.i(TAG, "onJumpClicked");
                        methodChannel.invokeMethod("onJumpClicked", null);
                    }
                    @Override
                    public void onSplashAdDismiss() {
                        Log.i(TAG, "onSplashAdDismiss. 点击跳过或展示时间到. 跳转到应用主界面");
                        // 场景：1.倒计时结束并且广告未点击；2.类似广点通广告点击跳过按钮
                        methodChannel.invokeMethod("onSplashAdDismiss", null);
                    }
                    @Override
                    public void onSplashAdFailed(String errMsg) {
                        Log.i(TAG, "onSplashAdFailed. 广告请求失败. 跳转到应用主界面. reason:" + errMsg);
                        // 加载广告失败时移除页面超时，并调整到主界面

                        Map<String, Object> params = new HashMap<>();
                        params.put("errMsg", errMsg);
                        methodChannel.invokeMethod("onSplashAdFailed", params);
                    }
                    @Override
                    public void onSplashAdPresent() {
                        Log.i(TAG, "onSplashAdPresent. 广告成功展示");
                        // 加载到广告时移除页面超时
                        methodChannel.invokeMethod("onSplashAdPresent", null);
                    }
                    @Override
                    public void onSplashAdShow() {
                        Log.i(TAG, "onSplashAdShow. 广告在界面曝光");
                        methodChannel.invokeMethod("onSplashAdShow", null);
                    }
                    @Override
                    public void onAdInfo(JSONObject adInfo) {
                        Log.i(TAG, "onAdInfo. adInfo: " + adInfo);
                        methodChannel.invokeMethod("onAdInfo", adInfo);
                    }
                });
            }
        }.start();
    }

    private static final float MIN_HEIGHT_RATE = 0.75f; //开屏广告容器占屏幕高度最小百分比，小于0.75会认定为无效展示的（广点通）
    private static final int MIN_HEIGHT_LOGO = 80; //单位：dp，底部LOGO最小高度，此值应用根据自己需要设置，Demo中Logo高度小于80dp显示效果很差
    private static final int MAX_HEIGHT_LOGO = 200; //单位：dp，底部LOGO最大高度，此值应用根据自己需要设置，Demo中Logo高度大于200dp显示效果很差
    private static final float CONTAINER_WIDTH_HEIGHT_RATE = 2.0f / 3.0f; //最优容器宽高比，目前广告源返回的图片为720*1080
    private int calculateOptimalHeightWithPX(Context context, int activityHeight) {
        Log.i(TAG, "calculateOptimalHeightWithPX. 开始计算开屏容器尺寸，给出最优尺寸");
        int screenWidthPX = DimenUtils.getScreenWidth(context);
        int screenHeightPX = DimenUtils.getScreenHeight(context);
        int mainLayoutHeightPX = activityHeight;
        int optimalHeight = (int) ((float) screenWidthPX / CONTAINER_WIDTH_HEIGHT_RATE);
        int minHeightPX = (int) (screenHeightPX * MIN_HEIGHT_RATE);
        int MIN_HEIGHT_LOGO_PX = DimenUtils.dp2px(context, MIN_HEIGHT_LOGO);
        int MAX_HEIGHT_LOGO_PX = DimenUtils.dp2px(context, MAX_HEIGHT_LOGO);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("calculateOptimalHeightWithPX. ");
        stringBuilder.append("screenWidthPX:" + screenWidthPX).append("px");
        stringBuilder.append(", screenHeightPX:" + screenHeightPX).append("px");
        stringBuilder.append(", mainLayoutHeightPX:" + mainLayoutHeightPX).append("px");
        stringBuilder.append(", optimalHeight:" + optimalHeight).append("px");
        stringBuilder.append(", minHeightPX:" + minHeightPX).append("px");
        stringBuilder.append(", MIN_HEIGHT_LOGO_PX:" + MIN_HEIGHT_LOGO_PX).append("px");
        stringBuilder.append(", MAX_HEIGHT_LOGO_PX:" + MAX_HEIGHT_LOGO_PX).append("px");
        int finalContainerHeightPX = optimalHeight;
        int finalLogoHeightPX = mainLayoutHeightPX - optimalHeight;
        int logoHeightPX = finalLogoHeightPX;
        if (optimalHeight >= minHeightPX) {
            if (logoHeightPX < MIN_HEIGHT_LOGO_PX) {
                int containerHeight = mainLayoutHeightPX - MIN_HEIGHT_LOGO_PX;
                if (containerHeight >= minHeightPX) {
                    // 如果用最优高度logo，广告容器会小于75%，则使用最小尺寸logo
                    finalContainerHeightPX = containerHeight;
                    finalLogoHeightPX = MIN_HEIGHT_LOGO_PX;
                } else {
                    //如果用最小尺寸logo，广告容器会小于75%，则隐藏logo
                    finalContainerHeightPX = mainLayoutHeightPX;
                    finalLogoHeightPX = 0;
                }
            } else if (logoHeightPX > MAX_HEIGHT_LOGO_PX) {
                // 稍微拉伸一下广告图片
                finalContainerHeightPX = mainLayoutHeightPX - MAX_HEIGHT_LOGO_PX;
                finalLogoHeightPX = MAX_HEIGHT_LOGO_PX;
            }
        } else {
            // 保证广告容器大于等75%，图片稍微拉长一些
            finalContainerHeightPX = minHeightPX;
            logoHeightPX = mainLayoutHeightPX - minHeightPX;
            if (logoHeightPX < MIN_HEIGHT_LOGO_PX) {
                //如果用最小尺寸logo，广告容器会小于75%，则隐藏logo
                finalContainerHeightPX = mainLayoutHeightPX;
                finalLogoHeightPX = 0;
            } else if (logoHeightPX > MAX_HEIGHT_LOGO_PX) {
                // logo显示为最大尺寸，广告容器再拉长一些
                finalLogoHeightPX = MAX_HEIGHT_LOGO_PX;
                finalContainerHeightPX = mainLayoutHeightPX - MAX_HEIGHT_LOGO_PX;
            }
        }
        stringBuilder.append(", finalContainerHeightPX:" + finalContainerHeightPX).append("px");
        stringBuilder.append(", finalLogoHeightPX:" + finalLogoHeightPX).append("px");
        if (finalLogoHeightPX == 0) {
            finalContainerHeightPX = activityHeight;
        }
        Log.i(TAG, stringBuilder.toString());
        return finalContainerHeightPX;
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
