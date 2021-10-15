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
  static Future<Object> initBK(String appId, String appKey, { Key key, String userId }) async {
    Map<String, Object> params = {
      "appId": appId,
      "appKey": appKey,
      "userId": userId,
    };
    return await _channel.invokeMethod("initBK", params);
  }
  static Future<Object> setUserInfo(String userId) async {
    Map<String, Object> params = {
      "userId": userId,
    };
    return await _channel.invokeMethod("setUserInfo", params);
  }
  static Future<Object> removeUserInfo() async {
    return await _channel.invokeMethod("removeUserInfo");
  }
}

class ReaperBK {
  static const MethodChannel _channel = const MethodChannel('AdReaperPlugin');

  static Future<Object> bkOption(BKOption option) async {
    Map<String, Object> params = {
      "option": "$option",
    };
    return await _channel.invokeMethod("bkOption", params);
  }
}

enum BKOption {
  SEARCH,GAME,NOVEL
}