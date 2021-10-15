package com.cabe.flutter.widget.reaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.bricks.task.model.network.entity.LoginRequest;
import com.bricks.wrapper.BKManagerSdk;
import com.bricks.wrapper.BKModule;
import com.bricks.wrapper.listener.IBKCallback;
import com.bricks.wrapper.listener.Module;
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

import java.util.Locale;

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


    private Context getActivity() {
        if(activityBinding == null) return null;
        return activityBinding.getActivity();
    }

    private Context getApplicationContext() {
        if(activityBinding == null) return null;
        return activityBinding.getActivity().getApplicationContext();
    }

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
        switch (call.method) {
            case "init":
                initSDK(call, result);
                break;
            case "initBK":
                initBkSDK(call, result);
                //解决视频流滑动时出现重复内容
                FragmentManager.enableNewStateManager(false);
                break;
            case "setUserInfo":
                setUserInfo(call);
                break;
            case "removeUserInfo":
                removeUserInfo(call);
                break;
            case "bkOption":
                bkOption(call);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void initSDK(@NonNull MethodCall call, @NonNull Result result) {
        String appId = call.argument("appId");
        String appKey = call.argument("appKey");
        String ext1 = call.argument("ext1");
        String ext2 = call.argument("ext2");
        String ext3 = call.argument("ext3");
        String ext4 = call.argument("ext4");
        String userId = call.argument("userId");
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
        boolean initResult = ReaperAdSDK.init(getApplicationContext(), appId, appKey);
        result.success(initResult ? 0 : -1);
    }

    /** 初始化Brick SDK */
    private void initBkSDK(@NonNull MethodCall call, @NonNull final Result result) {
        String appId = call.argument("appId");
        String appKey = call.argument("appKey");

        BKManagerSdk.init(getApplicationContext(), appId, appKey);
        setUserInfo(call);
        //全局设置接口回调
        BKManagerSdk.setIBKCallback(new IBKCallback() {
            @Override
            public boolean shouldUserLogin(Context context) {
                //如果媒体方未设置登陆信息（没有调用BKManagerSdk.setUserInfo），在页面使用过程中，会有登录回调
                //建议媒体方在此处拉起登录页面，或者已知登录信息，直接调用BKManagerSdk.setUserInfo。

                //已存在登录信息，直接调用登录接口，调用之后该方法返回true。
//                BKManagerSdk.setUserInfo(getApplicationContext(), "请传入用户的登录信息", LoginRequest.PLATFORM_OUT);
                result.error("1", "shouldUserLogin", null);
                return true;
            }
            @Override
            public boolean shouldPayAccountLogin(Context context) {
                //如果媒体方未设置提现账号（没有调用BKManagerSdk.setPayUserId），在点击提现之后，会有该接口回调
                //建议媒体方在此处拉起微信登录页面，或者已知微信登录openId，直接调用BKManagerSdk.setPayUserId。
                return false;
            }
            @Override
            public void onCoinReward(BKModule.TYPE type, int coins) {
                //视频流、信息流等type, 任务完成后回传金币数coins
            }
        });
    }

    private void setUserInfo(@NonNull MethodCall call) {
        String userId = call.argument("userId");
        //已存在登录信息，直接调用登录接口
        BKManagerSdk.setUserInfo(getApplicationContext(), userId, LoginRequest.PLATFORM_OUT);
    }

    private void removeUserInfo(@NonNull MethodCall call) {
        String userId = call.argument("userId");
        //已存在登录信息，直接调用登录接口
        BKManagerSdk.removeUserInfo(getApplicationContext());
    }

    private void bkOption(@NonNull MethodCall call) {
        Module module = null;

        String option = call.argument("option");
        if(option == null) option = "";
        else {

            option = option.toLowerCase(Locale.getDefault()).replace("bkoption.", "");
        }
        switch (option) {
            case "search":
                module = BKManagerSdk.getIBKModule(getApplicationContext(), BKModule.TYPE.SEARCH);
                break;
            case "game":
                module = BKManagerSdk.getIBKModule(getApplicationContext(), BKModule.TYPE.GAME);
                break;
            case "novel":
                module = BKManagerSdk.getIBKModule(getApplicationContext(), BKModule.TYPE.NOVEL);
                break;
        }
        if(module != null) {
            Intent intent = module.getActivity(getActivity());
            getActivity().startActivity(intent);
        }
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
