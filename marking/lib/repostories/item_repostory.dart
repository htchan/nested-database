import 'package:marking/repostories/schema.dart';

import '../models/item.dart';
import './step_repostory.dart';

import 'dart:async';

import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

class ItemRepostory {
  Future<Database> database = getDatabasesPath()
  .then( (path) => openDatabase(
    join(path, 'marking.db'),
    onCreate: schema,
    version: 1
  ));
  static final ItemRepostory _self = ItemRepostory();
  static final StepRepostory _steps = StepRepostory.instance();

  ItemRepostory();

  static ItemRepostory instance() {
    return _self;
  }

  Future<int>internalCreate(Item item) async {
    final db = await database;
    return await db.insert(
      'items',
      item.toFullMap(),
      conflictAlgorithm: ConflictAlgorithm.replace
    );
  }

  Future<int>internalUpdate(Item item) async {
    final db = await database;
    return await db.update(
      'items',
      item.toMap(),
      where: 'id = ?',
      whereArgs: [item.id],
    );
  }

  Future<int>internalDelete(Item item) async {
    final db = await database;
    return await db.delete(
      'items',
      where: 'id = ?',
      whereArgs: [item.id],
    )
    .then( (value) => 
      db.update(
        'items',
        { 'parentID': item.parentID },
        where: 'parentID = ?',
        whereArgs: [item.id],
      )
    );
  }

  Future<int>internalDeleteAll() async {
    final db = await database;
    return await db.delete('items');
  }

  Future<int> create(Item item) async {
    print("create item: $item");
    final db = await database;
    final value = await db.insert(
      'items',
      item.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace
    );
    final items = await db.query(
      'items',
      where: 'parentID=? and content=?',
      whereArgs: [item.parentID, item.content],
      orderBy: 'id desc',
      limit: 1,
    );
    await _steps.log('created', Item.from(items[0]));
    return value;
  }

  Future<int> update(Item item) async {
    final db = await database;
    final value = await db.update(
      'items',
      item.toMap(),
      where: 'id = ?',
      whereArgs: [item.id],
    );
    await _steps.log('updated', item);
    return value;
  }

  Future<int> delete(Item item) async {
    final db = await database;
    final value = await db.delete(
      'items',
      where: 'id = ?',
      whereArgs: [item.id],
    )
    .then( (value) => 
      db.update(
        'items',
        { 'parentID': item.parentID },
        where: 'parentID = ?',
        whereArgs: [item.id],
      )
    );
    await _steps.log('delete', item);
    return value;
  }

  Future<int> deleteAll() async {
    final db = await database;
    final value = await db.delete('items');
    await _steps.log('delete all', Item(id: 0, parentID: -1, content: 'main'));
    return value;
  }

  Future<Item?> getItem(int id) async {
    final db = await database;
    List<Map<String, dynamic>> results = await db.query(
      'items',
      where: 'id = ?',
      whereArgs: [id]
    );
    if (results.isEmpty) return null;
    return Item.from(results[0]);
  }

  Future<List<Item>> getAllItems() async {
    final db = await database;
    List<Map<String, dynamic>> results = await db.query(
      'items'
    );
    return results.map( (map) => Item.from(map) ).toList();
  }

  Future<Item?> getParent(Item item) async {
    return await getItem(item.parentID);
  }

  Future<List<Item>> getChildren(Item item) async {
    final db = await database;
    List<Map<String, dynamic>> results = await db.query(
      'items',
      where: 'parentID = ?',
      whereArgs: [item.id]
    );
    return results.map( (map) => Item.from(map) ).toList();
  }
}