import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:widget_ad_reaper/widget_base.dart';

// ignore: must_be_immutable
class AdReaperSplash extends AdReaperWidget {
  int splashHeight;
  bool skipSecond;
  int timeout;
  AdReaperSplash(String adId, { Key key, this.splashHeight, this.skipSecond, this.timeout }) : super(adId);

  @override
  AdReaperState<AdReaperWidget> onGetState(onViewCreated) {
    return _SplashState(this.adId, onViewCreated);
  }

  @override
  dynamic requestParams() {
    Map<String, Object> params = {};
    if(splashHeight != null) {
      params["splashHeight"] = splashHeight.toString();
    }
    if(skipSecond != null) {
      params["skipSecond"] = skipSecond.toString();
    }
    if(timeout != null) {
      params["timeout"] = timeout.toString();
    }
    params.addAll(super.requestParams());
    return params;
  }

  @override
  void onWidgetCreated() {
    request();
  }
}

class _SplashState extends AdReaperState<AdReaperSplash> {
  @override String onGetViewType() => "com.cabe.flutter.widget.AdReaperSplash";
  _SplashState(String adId, OnViewCreated onCreated) : super(adId, onCreated);
}