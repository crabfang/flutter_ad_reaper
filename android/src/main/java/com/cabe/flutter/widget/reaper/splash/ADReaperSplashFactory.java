package com.cabe.flutter.widget.reaper.splash;

import android.content.Context;

import com.cabe.flutter.widget.reaper.OnFactoryListener;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class ADReaperSplashFactory extends PlatformViewFactory {
    private final OnFactoryListener listener;
    private final BinaryMessenger messenger;
    public ADReaperSplashFactory(BinaryMessenger messenger, OnFactoryListener listener) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.listener = listener;
    }
    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new ADReaperSplash(listener.getActivity(), messenger, viewId, args);
    }
}