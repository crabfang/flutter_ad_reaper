package com.cabe.flutter.widget.reaper.interaction;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.fighter.loader.ReaperAdSDK;
import com.fighter.loader.adspace.ReaperAdSpace;
import com.fighter.loader.listener.InteractionExpressAdCallBack;
import com.fighter.loader.listener.InteractionExpressAdListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class ADReaperInteraction implements PlatformView, MethodChannel.MethodCallHandler {
    public static String VIEW_TYPE_ID = "com.cabe.flutter.widget.AdReaperInteraction";
    private static final String TAG = "ADReaperInteraction";

    private final MethodChannel methodChannel;
    private final FrameLayout containerLayout;
    private InteractionExpressAdCallBack expressAdCallBack;

    public ADReaperInteraction(Context context, BinaryMessenger messenger, int viewID, Object args) {
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

        // 上报PV
        ReaperAdSDK.getLoadManager().reportPV(adPositionId);
        // 加载插屏广告
        ReaperAdSpace adSpace = new ReaperAdSpace(adPositionId);
        ReaperAdSDK.getLoadManager().loadInteractionAd(adSpace, new InteractionExpressAdListener() {
            @Override
            public void onInteractionExpressAdLoaded(List<InteractionExpressAdCallBack> interactionExpressAdList) {
                Log.i(TAG, "onInteractionExpressAdLoaded");
                if (interactionExpressAdList != null && interactionExpressAdList.size() > 0) {
                    //render()渲染广告
                    expressAdCallBack = interactionExpressAdList.get(0);
                }
                methodChannel.invokeMethod("onInteractionExpressAdLoaded", null);
            }

            @Override
            public void onAdClosed(InteractionExpressAdCallBack interactionExpressAd) {
                Log.i(TAG, "onAdClosed");
                methodChannel.invokeMethod("onAdClosed", null);
            }

            @Override
            public void onAdClicked(InteractionExpressAdCallBack interactionExpressAd) {
                Log.i(TAG, "onAdClicked");
                methodChannel.invokeMethod("onAdClosed", null);
            }

            @Override
            public void onAdShow(InteractionExpressAdCallBack interactionExpressAd) {
                Log.i(TAG, "onAdShow");
                methodChannel.invokeMethod("onAdShow", null);
            }

            @Override
            public void onRenderFail(InteractionExpressAdCallBack interactionExpressAd, String msg, int code) {
                Log.i(TAG, "onRenderFail msg: " + msg + " , code: " + code);
                // 渲染广告失败，销毁广告回调对象
                interactionExpressAd.destroy();
                
                Map<String, Object> params = new HashMap<>();
                params.put("code", code + "");
                params.put("msg", msg);
                methodChannel.invokeMethod("onRenderFail", params);
            }

            @Override
            public void onRenderSuccess(InteractionExpressAdCallBack interactionExpressAd) {
                Log.i(TAG, "onRenderSuccess");
                methodChannel.invokeMethod("onRenderSuccess", null);
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
        if(expressAdCallBack != null) {
            expressAdCallBack.render();
        }
    }
    
    private void showAd() {
        if (expressAdCallBack != null) {
            expressAdCallBack.showInteractionExpressAd((Activity) containerLayout.getContext());
        }
    }

    private void destroyAd() {
        if (expressAdCallBack != null) {
            expressAdCallBack.destroy();
        }
        expressAdCallBack = null;
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
