import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:widget_ad_reaper/widget_base.dart';

// ignore: must_be_immutable
class AdReaperBanner extends AdReaperWidget {
  int adSizePosition;
  bool hideDislike;
  int margin;
  AdReaperBanner(String adId, { Key key, this.margin, this.adSizePosition, this.hideDislike }) : super(adId);

  @override
  AdReaperState<AdReaperWidget> onGetState(onViewCreated) {
    return _BannerState(this.adId, onViewCreated);
  }

  @override
  dynamic requestParams() {
    Map<String, Object> params = {};
    if(adSizePosition != null) {
      params["adSizePosition"] = adSizePosition.toString();
    }
    if(hideDislike != null) {
      params["hideDislike"] = hideDislike.toString();
    }
    if(margin != null) {
      params["margin"] = margin.toString();
    }
    params.addAll(super.requestParams());
    return params;
  }

  @override
  void onWidgetCreated() {
    request();
  }

  @override
  Future<dynamic> onHandlerChannel(MethodCall call) {
    if(call.method == "onBannerPositionAdLoaded") {
      render();
    } else if(call.method == "onRenderSuccess") {
      showAd();
    }
    return super.onHandlerChannel(call);
  }
}

class _BannerState extends AdReaperState<AdReaperBanner> {
  @override String onGetViewType() => "com.cabe.flutter.widget.AdReaperBanner";
  _BannerState(String adId, OnViewCreated onCreated) : super(adId, onCreated);
}