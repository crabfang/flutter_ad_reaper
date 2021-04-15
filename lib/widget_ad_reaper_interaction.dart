import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:widget_ad_reaper/widget_base.dart';

// ignore: must_be_immutable
class AdReaperInteraction extends AdReaperWidget {
  AdReaperInteraction(String adId, { Key key, int margin, int adSizeIndex, bool hideDislike }) : super(adId);

  @override
  AdReaperState<AdReaperWidget> onGetState(onViewCreated) {
    return _InteractionState(this.adId, onViewCreated);
  }

  @override
  Future<dynamic> onHandlerChannel(MethodCall call) {
    if(call.method == "onInteractionExpressAdLoaded") {
      render();
    } else if(call.method == "onRenderSuccess") {
      showAd();
    }
    return super.onHandlerChannel(call);
  }
}

class _InteractionState extends AdReaperState<AdReaperInteraction> {
  @override String onGetViewType() => "com.cabe.flutter.widget.AdReaperInteraction";
  _InteractionState(String adId, OnViewCreated onCreated) : super(adId, onCreated);
}