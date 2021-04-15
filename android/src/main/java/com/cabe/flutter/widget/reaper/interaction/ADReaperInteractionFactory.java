package com.cabe.flutter.widget.reaper.interaction;

import android.content.Context;

import com.cabe.flutter.widget.reaper.OnFactoryListener;
import com.cabe.flutter.widget.reaper.splash.ADReaperSplash;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class ADReaperInteractionFactory extends PlatformViewFactory {
    private final OnFactoryListener listener;
    private final BinaryMessenger messenger;
    public ADReaperInteractionFactory(BinaryMessenger messenger, OnFactoryListener listener) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.listener = listener;
    }
    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new ADReaperInteraction(listener.getActivity(), messenger, viewId, args);
    }
}