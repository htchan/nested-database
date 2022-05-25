import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:marking/components/floating_button.dart';

import '../models/item.dart';
import '../models/step.dart';

import '../repostories/item_repostory.dart';
import '../repostories/step_repostory.dart';

import '../components/item_card.dart';

class TestScreen extends StatefulWidget {
  const TestScreen({Key? key}) : super(key: key);

  @override
  State<TestScreen> createState() => _TestScreenState();
}

class _TestScreenState extends State<TestScreen> {
  _TestScreenState();

  @override
  Widget build(BuildContext context) {
    Item item = Item(parentID: 0, id: 0, content: "hello");
    return Scaffold(
      appBar: AppBar( title: const Text("Testing") ),
      body: Center(
        child: Text("hello")
      ),
    );
  }
}