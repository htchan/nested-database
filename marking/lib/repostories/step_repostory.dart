import 'dart:convert';
import 'dart:io';

import 'package:marking/repostories/item_repostory.dart';
import 'package:marking/repostories/schema.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path_provider/path_provider.dart';

import '../models/item.dart';
import '../models/step.dart';

import 'dart:async';

import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:http/http.dart' as http;

class StepRepostory {
  Future<Database> database = getDatabasesPath()
  .then( (path) => openDatabase(
    join(path, 'marking.db'),
    onCreate: schema,
    version: 1
  ));
  static final StepRepostory _self = StepRepostory();

  StepRepostory();

  static StepRepostory instance() {
    return _self;
  }

  Future<int> log(String event, Item item) async {
    final db = await database;
    return db.insert(
      'steps',
      Step(event: event, item: item).toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace
    );
  }

  Future<int> execute(String sql) async {
    final regex = RegExp(r"insert into steps \(time, event, id, parentID, content\) values \((.*?), '(.*?)', (.*?), (.*?), '(.*?)'\)");
    final match = regex.firstMatch(sql);
    if (match == null) return 0;
    print(match.group(1));
    final step = Step.from({
      'time': int.parse(match.group(1)??""),
      'event': match.group(2)??"",
      'id': int.parse(match.group(3)??""),
      'parentID': int.parse(match.group(4)??""),
      'content': match.group(5)??"",
    });
    step.item.content = step.item.content.replaceAll("\\n", "\n");
    print(step);
    ItemRepostory itemRepo = ItemRepostory.instance();
    switch (step.event) {
      case 'created':
      itemRepo.internalCreate(step.item);
      break;
      case 'updated':
      itemRepo.internalUpdate(step.item);
      break;
      case 'deleted':
      itemRepo.internalDelete(step.item);
      break;
      case 'delete all':
      itemRepo.internalDeleteAll();
      break;
    }
    return 1;
  }

  Future<bool> export() async {
    if (!await Permission.storage.request().isGranted) {
      return false;
    }
    final db = await database;
    String content = (await db.query(
      'steps',
      orderBy: 'time'
    ))
    .map( (map) => Step.from(map).toSql() )
    .join("\n");
    print(content);
    await http.post(
      Uri.parse("http://192.168.128.146/api/marking/save"),
      body: { "data": content },
    );
    return true;
  }
  
  Future<bool> import() async {
    if (!await Permission.storage.request().isGranted) {
      return false;
    }
    final content = (await http.get(
      Uri.parse("http://192.168.128.146/api/marking/load"),
    )).body;
    ItemRepostory.instance().deleteAll();
    (await database).delete('steps');
    final db = await database;
    for (final statement in content.split("\n")) {
      await execute(statement);
      db.execute(statement);
    }
    return true;
  }
}