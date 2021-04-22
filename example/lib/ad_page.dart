import 'package:flutter/material.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_banner.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_interaction.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_splash.dart';
import 'package:widget_ad_reaper/widget_ad_reaper_video_reward.dart';

class ADPage extends StatefulWidget {
  @override
  _ADPageState createState() => _ADPageState();
}

class _ADPageState extends State<ADPage> {
  @override
  void initState() {
    super.initState();
  }

  AdReaperBanner banner;
  AdReaperInteraction interaction;
  AdReaperRewardVideo video;
  AdReaperSplash splash;
  @override
  Widget build(BuildContext context) {
    banner = AdReaperBanner("2713", adSizePosition: 6,);
    interaction = AdReaperInteraction("2715");
    video = AdReaperRewardVideo("2716");
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
                height: 60,
                child: banner,
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
                    child: new Text('RefreshBanner'),
                    onPressed: () {
                      banner.request();
                    },
                  ),
                ],
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
