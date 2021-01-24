import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_safetynet/flutter_safetynet.dart';
import 'package:flutter_safetynet/data/harmful_app.dart';
import 'package:flutter_safetynet/data/safetynet_exception.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _platformVersion = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    bool platformVersion = false;
    // Platform messages may fail, so we use a try/catch PlatformException.
    List<HarmfulApp> apps;
    try {
     await FlutterSafetynet.enableVerifyApps();
    } on PlatformException catch(ex) {
      //print(ex.message);
    }
    print(apps);

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('IS verify apps enabled: $_platformVersion\n'),
        ),
      ),
    );
  }
}
