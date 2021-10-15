import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

typedef OnViewCreated = void Function(MethodChannel channel);
// ignore: must_be_immutable
abstract class AdReaperWidget extends StatefulWidget {
  String tag = "AdReaperWidget";
  String adId;

  MethodChannel channel;
  Function(MethodCall call) _channelHandle;
  AdReaperState onGetState(OnViewCreated onViewCreated);

  AdReaperWidget(String adId) {
    this.tag = runtimeType.toString();
    this.adId = adId;
  }

  void setChannelHandle(Function(MethodCall call) handle) {
    _channelHandle = handle;
  }

  @override
  State<StatefulWidget> createState() {
    return onGetState((MethodChannel channel) {
      this.channel = channel;
      channel.setMethodCallHandler(onHandlerChannel);
      onWidgetCreated();
    });
  }

  void onWidgetCreated() {}

  Future<dynamic> onHandlerChannel(MethodCall call) async {
    print("$tag: onHandlerChannel: ${call.method}: ${call.arguments}");
    if(_channelHandle != null) _channelHandle(call);
  }

  dynamic requestParams() => {
    "adPositionId": adId,
  };

  Future<Object> request() {
    return channel?.invokeMethod("request", requestParams());
  }

  Future<Object> render() {
    return channel?.invokeMethod("render");
  }

  Future<Object> showAd() {
    return channel?.invokeMethod("showAd");
  }

  Future<Object> destroy() {
    return channel?.invokeMethod("destroy");
  }
}

abstract class AdReaperState<T extends AdReaperWidget> extends State<T> with AutomaticKeepAliveClientMixin {
  String tag = "AdReaperState";

  @override bool get wantKeepAlive => true;
  OnViewCreated _onCreated;
  String onGetViewType();
  AndroidView onCreateAndroidView() => AndroidView(
    viewType: onGetViewType(),
    creationParamsCodec: const StandardMessageCodec(),
    onPlatformViewCreated: this._onViewCreated,
  );
  UiKitView onCreateUiKitView() => UiKitView(
    viewType: onGetViewType(),
    creationParamsCodec: const StandardMessageCodec(),
    onPlatformViewCreated: this._onViewCreated,
  );

  AdReaperState(String adId, OnViewCreated onCreated) {
    this.tag = runtimeType.toString();
    this._onCreated = onCreated;
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    // 这里使用 AndroidView，告知 Flutter 这是个原生控件，控件的类型是 VIEW_TYPE。
    if(defaultTargetPlatform == TargetPlatform.android) {
      return onCreateAndroidView();
    } else if(defaultTargetPlatform == TargetPlatform.iOS) {
      return onCreateUiKitView();
    } else return null;
  }

  void _onViewCreated(int id) {
    print("$tag onViewCreated: $id");
    MethodChannel channel = MethodChannel("${onGetViewType()}#$id");
    this._onCreated(channel);
  }
}

// ignore: must_be_immutable
abstract class BKReaperWidget extends StatefulWidget {
  String tag = "BKReaperWidget";

  MethodChannel channel;
  Function(MethodCall call) _channelHandle;
  BKReaperState onGetState(OnViewCreated onViewCreated);

  BKReaperWidget() {
    this.tag = runtimeType.toString();
  }

  void setChannelHandle(Function(MethodCall call) handle) {
    _channelHandle = handle;
  }

  @override
  State<StatefulWidget> createState() {
    return onGetState((MethodChannel channel) {
      this.channel = channel;
      channel.setMethodCallHandler(onHandlerChannel);
      onWidgetCreated();
    });
  }

  void onWidgetCreated() {}

  Future<dynamic> onHandlerChannel(MethodCall call) async {
    print("$tag: onHandlerChannel: ${call.method}: ${call.arguments}");
    if(_channelHandle != null) _channelHandle(call);
  }

  Future<Object> show() {
    return channel?.invokeMethod("show");
  }

  Future<Object> hide() {
    return channel?.invokeMethod("hide");
  }
}

abstract class BKReaperState<T extends BKReaperWidget> extends State<T> with AutomaticKeepAliveClientMixin {
  String tag = "BKReaperState";

  @override bool get wantKeepAlive => true;
  OnViewCreated _onCreated;
  String onGetViewType();
  AndroidView onCreateAndroidView() => AndroidView(
    viewType: onGetViewType(),
    creationParamsCodec: const StandardMessageCodec(),
    onPlatformViewCreated: this._onViewCreated,
  );
  UiKitView onCreateUiKitView() => UiKitView(
    viewType: onGetViewType(),
    creationParamsCodec: const StandardMessageCodec(),
    onPlatformViewCreated: this._onViewCreated,
  );

  BKReaperState(OnViewCreated onCreated) {
    this.tag = runtimeType.toString();
    this._onCreated = onCreated;
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    // 这里使用 AndroidView，告知 Flutter 这是个原生控件，控件的类型是 VIEW_TYPE。
    if(defaultTargetPlatform == TargetPlatform.android) {
      return onCreateAndroidView();
    } else if(defaultTargetPlatform == TargetPlatform.iOS) {
      return onCreateUiKitView();
    } else return null;
  }

  void _onViewCreated(int id) {
    print("$tag onViewCreated: $id");
    MethodChannel channel = MethodChannel("${onGetViewType()}#$id");
    this._onCreated(channel);
  }
}