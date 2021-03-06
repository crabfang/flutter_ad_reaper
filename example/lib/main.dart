
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:widget_ad_reaper/plugin_ad_reaper.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_splash.dart';
import 'package:widget_ad_reaper_example/ad_page.dart';

void main() {
  runApp(MaterialApp(
    title: 'Navigation Basics',
    home: MyApp(),
  ));
}
class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  AdReaperSplash splash;
  @override
  Widget build(BuildContext context) {
    WidgetAdReaper.init("100210", "cbd1964efb89c14aac00c09c84866371", ext1: "杭州", ext2: "13767768888", ext3: "vip", ext4: "protect").then((value) => print("ad reaper sdk init: $value"));
    WidgetAdReaper.initBK("100210", "cbd1964efb89c14aac00c09c84866371", userId: "1023").then((value) => print("initBK"));
    WidgetAdReaper.setUserInfo("1023");

    final physicalWidth = window.physicalSize.width;
    final physicalHeight = window.physicalSize.height * 0.28;
    splash = AdReaperSplash("2712", splashHeight: physicalHeight.toInt(), timeout: 3000,);
    splash.setChannelHandle(onHandleSplash);
    return MaterialApp(
      home: Scaffold(
        body: SingleChildScrollView(
          child: Container(
            width: physicalWidth,
            height: physicalHeight,
            child: splash,
          ),
        ),
      ),
    );
  }

  Future<dynamic> onHandleSplash(MethodCall call) async {
    var enter = { "onSplashAdDismiss", "onJumpClicked", "onSplashAdFailed" };
    print("onHandleSplash: ${call.method}: ${call.arguments} -> ${enter.contains(call.method)}");
    if(enter.contains(call.method)) {
      splash.destroy();
      Navigator.push(context, new MaterialPageRoute(builder: (context) => new ADPage()),);
    }
  }
}
