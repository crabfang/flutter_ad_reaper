package com.cabe.flutter.widget.reaper;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cabe.flutter.widget.reaper.banner.ADReaperBanner;
import com.cabe.flutter.widget.reaper.banner.ADReaperBannerFactory;
import com.cabe.flutter.widget.reaper.interaction.ADReaperInteraction;
import com.cabe.flutter.widget.reaper.interaction.ADReaperInteractionFactory;
import com.cabe.flutter.widget.reaper.splash.ADReaperSplash;
import com.cabe.flutter.widget.reaper.splash.ADReaperSplashFactory;
import com.cabe.flutter.widget.reaper.video.ADReaperRewardVideo;
import com.cabe.flutter.widget.reaper.video.ADReaperRewardVideoFactory;
import com.fighter.loader.ExtendParamSetter;
import com.fighter.loader.ReaperAdSDK;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * WidgetAdReaperPlugin
 */
public class WidgetAdReaperPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private ActivityPluginBinding activityBinding = null;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "AdReaperPlugin");
        channel.setMethodCallHandler(this);

        BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        OnFactoryListener listener = new OnFactoryListener() {
            @Override
            public Activity getActivity() {
                return activityBinding.getActivity();
            }
        };
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(ADReaperSplash.VIEW_TYPE_ID, new ADReaperSplashFactory(messenger, listener));
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(ADReaperBanner.VIEW_TYPE_ID, new ADReaperBannerFactory(messenger, listener));
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(ADReaperInteraction.VIEW_TYPE_ID, new ADReaperInteractionFactory(messenger, listener));
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(ADReaperRewardVideo.VIEW_TYPE_ID, new ADReaperRewardVideoFactory(messenger, listener));
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String TAG = "AdReaperPlugin";
        Log.w(TAG, "onMethodCall: " + call.method);
        if (call.method.equals("init")) {
            initSDK(call, result);
        } else {
            result.notImplemented();
        }
    }

    private void initSDK(@NonNull MethodCall call, @NonNull Result result) {
        String appId = call.argument("appId");
        String appKey = call.argument("appKey");
        String ext1 = call.argument("ext1");
        String ext2 = call.argument("ext2");
        String ext3 = call.argument("ext3");
        String ext4 = call.argument("ext4");
        // 设置扩展维度参数，需要接入方与万汇SDK产品确认扩展维度参数的意义，报表数据可以根据这些扩展维度参数细分
        // 如下，第一个参数设置为地区，第二个参数设置为电话号码，第三个参数设置为是否VIP，第四个参数设置为是否新手保护
        ExtendParamSetter.setExt1(ext1);
        ExtendParamSetter.setExt2(ext2);
        ExtendParamSetter.setExt3(ext3);
        ExtendParamSetter.setExt4(ext4);

        //使用api
        // appContext 应用上下文
        // appId      广告平台申请的APP id
        // appKey     广告平台申请的APP key
        boolean initResult = ReaperAdSDK.init(activityBinding.getActivity().getApplicationContext(), appId, appKey);
        result.success(initResult ? 0 : -1);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activityBinding = binding;
    }

    @Override
    public void onDetachedFromActivity() {
        activityBinding = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }
}
