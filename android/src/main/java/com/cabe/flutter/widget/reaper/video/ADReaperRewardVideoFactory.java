package com.cabe.flutter.widget.reaper.video;

import android.content.Context;

import com.cabe.flutter.widget.reaper.OnFactoryListener;
import com.cabe.flutter.widget.reaper.interaction.ADReaperInteraction;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class ADReaperRewardVideoFactory extends PlatformViewFactory {
    private final OnFactoryListener listener;
    private final BinaryMessenger messenger;
    public ADReaperRewardVideoFactory(BinaryMessenger messenger, OnFactoryListener listener) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.listener = listener;
    }
    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new ADReaperRewardVideo(listener.getActivity(), messenger, viewId, args);
    }
}