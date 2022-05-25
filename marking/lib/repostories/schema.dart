import 'package:sqflite/sqflite.dart';

final _statements = [
      """CREATE TABLE items (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        parentID INTEGER,
        content TEXT
      );""",
      """CREATE TABLE steps (
        time INTEGER,
        event TEXT,
        id INTEGER,
        parentID INTEGER,
        content TEXT
      );""",
];

Future<void> schema(Database db, int version) async {
  if (version <= 1) {
    for (final statement in _statements) {
      await db.execute(
        statement
      );
    }
    await db.execute(
      "INSERT INTO items (id, parentID, content) values (?, ?, ?);",
      [0, -1, 'main']
    );
    await db.execute(
      "INSERT INTO steps (time, event, id, parentID, content) values (?, ?, ?, ?, ?);",
      [DateTime.now().millisecondsSinceEpoch, 'created', 0, -1, 'main']
    );
  }
}