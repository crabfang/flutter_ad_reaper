import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class WidgetAdReaper {
  static const MethodChannel _channel = const MethodChannel('AdReaperPlugin');

  static Future<Object> init(String appId, String appKey, { Key key, String ext1, String ext2, String ext3, String ext4 }) async {
    Map<String, Object> params = {
      "appId": appId,
      "appKey": appKey,
      "ext1": ext1,
      "ext2": ext2,
      "ext3": ext3,
      "ext4": ext4
    };
    return await _channel.invokeMethod("init", params);
  }
}
