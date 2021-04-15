import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:widget_ad_reaper/widget_base.dart';

// ignore: must_be_immutable
class AdReaperRewardVideo extends AdReaperWidget {
  AdReaperRewardVideo(String adId, { Key key, int margin, int adSizeIndex, bool hideDislike }) : super(adId);

  @override
  AdReaperState<AdReaperWidget> onGetState(onViewCreated) {
    return _RewardVideoState(this.adId, onViewCreated);
  }

  @override
  Future<dynamic> onHandlerChannel(MethodCall call) {
    if(call.method == "onRewardVideoAdLoad") {
      showAd();
    }
    return super.onHandlerChannel(call);
  }
}

class _RewardVideoState extends AdReaperState<AdReaperRewardVideo> {
  @override String onGetViewType() => "com.cabe.flutter.widget.AdReaperRewardVideo";
  _RewardVideoState(String adId, OnViewCreated onCreated) : super(adId, onCreated);
}