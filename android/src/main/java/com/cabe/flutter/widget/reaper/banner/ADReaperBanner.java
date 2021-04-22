package com.cabe.flutter.widget.reaper.banner;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.cabe.flutter.widget.reaper.utils.AdSizeUtils;
import com.cabe.flutter.widget.reaper.utils.DimenUtils;
import com.fighter.loader.ReaperAdSDK;
import com.fighter.loader.adspace.ReaperBannerPositionAdSpace;
import com.fighter.loader.listener.BannerPositionAdCallBack;
import com.fighter.loader.listener.BannerPositionAdListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class ADReaperBanner implements PlatformView, MethodChannel.MethodCallHandler {
    public static String VIEW_TYPE_ID = "com.cabe.flutter.widget.AdReaperBanner";
    private static final String TAG = "ReaperBanner";

    private final MethodChannel methodChannel;
    private final FrameLayout containerLayout;
    private BannerPositionAdCallBack mAdCallback;

    public ADReaperBanner(Context context, BinaryMessenger messenger, int viewID, Object args) {
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
            case "render":
                render();
                result.success(true);
                break;
            case "showAd":
                showAd();
                result.success(true);
                break;
            case "destroy":
                destroyAd();
                result.success(true);
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

        destroyAd();
        if (containerLayout.getChildCount() > 0) {
            containerLayout.removeAllViews();
        }

        String adPositionId = params.get("adPositionId");
        int adSizePosition = 0;
        try {
            adSizePosition = Integer.parseInt(params.get("adSizePosition"));
        } catch (Exception ignored) {}
        boolean showDislike = !params.containsKey("hideDislike");
        int margin = 0;
        try {
            margin = Integer.parseInt(params.get("margin"));
        } catch (Exception ignored) {}

        ReaperBannerPositionAdSpace.AdSize adSize = AdSizeUtils.getAdSize(adSizePosition);

        // 上报PV
        ReaperAdSDK.getLoadManager().reportPV(adPositionId);
        // 请求Banner广告
        ReaperBannerPositionAdSpace adSpace = new ReaperBannerPositionAdSpace(adPositionId, adSize);

        // 默认是显示 Dislike 关闭图标的，可以通过下面方法设置隐藏/显示。
        // 此设置只针对自渲染广告，模版广告不生效
        adSpace.showDislikeView(showDislike);

        int screenWidthDP = DimenUtils.getScreenWidthDP(containerLayout.getContext());
        Log.i(TAG, "requestSupperAd. screenWidthDP: " + screenWidthDP);
        // 模版广告需要设置宽度,会根据广告宽度设置最优高度
        adSpace.setWidth(screenWidthDP - (margin * 2));

        ReaperAdSDK.getLoadManager().loadBannerPositionAd(adSpace, new BannerPositionAdListener() {
            @Override
            public void onBannerPositionAdLoaded(List<BannerPositionAdCallBack> list) {
                Log.i(TAG, "onBannerPositionAdLoaded size: " + list.size());
                // 请求广告成功后，对广告进行渲染
                if (!list.isEmpty()) {
                    mAdCallback = list.get(0);
                }
                methodChannel.invokeMethod("onBannerPositionAdLoaded", null);
            }
            @Override
            public void onAdClicked(BannerPositionAdCallBack bannerPositionAdCallBack) {
                Log.i(TAG, "onAdClicked uuid: " + bannerPositionAdCallBack.getUUID());

                Map<String, Object> params = new HashMap<>();
                params.put("uuid", bannerPositionAdCallBack.getUUID());
                methodChannel.invokeMethod("onAdClicked", params);
            }
            @Override
            public void onAdShow(BannerPositionAdCallBack bannerPositionAdCallBack) {
                Log.i(TAG, "onAdShow uuid: " + bannerPositionAdCallBack.getUUID());

                Map<String, Object> params = new HashMap<>();
                params.put("uuid", bannerPositionAdCallBack.getUUID());
                methodChannel.invokeMethod("onAdShow", params);
            }
            @Override
            public void onRenderFail(BannerPositionAdCallBack bannerPositionAdCallBack, String msg, int code) {
                Log.i(TAG, "onRenderFail msg: " + msg + " , code: " + code + ", uuid: " + bannerPositionAdCallBack.getUUID());
                // 渲染广告失败，销毁广告回调对象
                bannerPositionAdCallBack.destroy();

                Map<String, Object> params = new HashMap<>();
                params.put("code", code);
                params.put("msg", msg);
                params.put("uuid", bannerPositionAdCallBack.getUUID());
                methodChannel.invokeMethod("onRenderFail", params);
            }
            @Override
            public void onRenderSuccess(BannerPositionAdCallBack bannerPositionAdCallBack) {
                Log.i(TAG, "onRenderSuccess uuid: " + bannerPositionAdCallBack.getUUID());
                mAdCallback = bannerPositionAdCallBack;

                // 必须设置Dislike上下文，否则关闭广告图标不会生效
                bannerPositionAdCallBack.setDislikeContext((Activity) containerLayout.getContext());

                Map<String, Object> params = new HashMap<>();
                params.put("uuid", bannerPositionAdCallBack.getUUID());
                methodChannel.invokeMethod("onRenderSuccess", params);
            }
            @Override
            public void onDislike(BannerPositionAdCallBack bannerPositionAdCallBack, String value) {
                Log.i(TAG, "onDislike value: " + value + ", uuid: " + bannerPositionAdCallBack.getUUID());
                containerLayout.removeAllViews();

                Map<String, Object> params = new HashMap<>();
                params.put("uuid", bannerPositionAdCallBack.getUUID());
                methodChannel.invokeMethod("onDislike", params);
            }
            @Override
            public void onFailed(String requestId, String errMsg) {
                Log.e(TAG, "onFailed, requestId: " + requestId + ", errMsg: " + errMsg);

                Map<String, Object> params = new HashMap<>();
                params.put("requestId", requestId);
                params.put("errMsg", errMsg);
                methodChannel.invokeMethod("onFailed", params);
            }
        });
    }

    private void render() {
        if (mAdCallback == null) {
            return;
        }
        mAdCallback.render();
    }

    private void showAd() {
        if (mAdCallback == null) {
            return;
        }
        if (containerLayout.getChildCount() > 0) {
            containerLayout.removeAllViews();
        }
        // 添加广告View到界面容器中
        View adView = mAdCallback.getExpressAdView();
        if (adView != null) {
            containerLayout.addView(adView);
        }
    }

    private void destroyAd() {
        if (mAdCallback != null) {
            mAdCallback.destroy();
        }
        mAdCallback = null;
        containerLayout.removeAllViews();
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
