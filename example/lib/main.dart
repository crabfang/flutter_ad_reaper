
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:widget_ad_reaper/plugin_ad_reaper.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_banner.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_interaction.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_video_reward.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_splash.dart';

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

  AdReaperInteraction interaction;
  AdReaperRewardVideo video;
  AdReaperSplash splash;
  @override
  Widget build(BuildContext context) {
    WidgetAdReaper.init("100015", "3353bea731f341775a015ef3515864c2", ext1: "北京", ext2: "13767768888", ext3: "vip", ext4: "protect").then((value) => () {
      print("ad reaper sdk init: $value");
    });

    final physicalWidth = window.physicalSize.width;
    final physicalHeight = window.physicalSize.height;
    interaction = AdReaperInteraction("1579");
    video = AdReaperRewardVideo("1537");
    splash = AdReaperSplash("1559", splashHeight: physicalHeight.toInt(), timeout: 10,);
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('ReaperSDKApp'),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: [
              Container(
                width: 360,
                height: 200,
                child: AdReaperBanner("1310"),
              ),
              Row(
                children: [
                  new RaisedButton(
                    child: new Text('LoadInteraction'),
                    onPressed: () {
                      interaction.request();
                    },
                  ),
                  new RaisedButton(
                    child: new Text('LoadVideo'),
                    onPressed: () {
                      video.request();
                    },
                  ),
                  new RaisedButton(
                    child: new Text('LoadSplash'),
                    onPressed: () {
                      splash.request();
                    },
                  ),
                ],
              ),
              Container(
                width: physicalWidth,
                height: physicalHeight,
                color: Color(0xFF00FF00),
                child: splash,
              ),
              Container(
                width: 1,
                height: 1,
                child: interaction,
              ),
              Container(
                width: 1,
                height: 1,
                child: video,
              )
            ],
          ),
        ),
      ),
    );
  }
}
