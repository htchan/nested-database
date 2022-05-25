import 'item.dart';

class Step {
  final DateTime time;
  final String event;
  final Item item;

  Step({
    required this.event,
    required this.item
  }) : time = DateTime.now();
  Step.from(Map<String, dynamic> map):
  time = DateTime.fromMillisecondsSinceEpoch(map['time'] ?? DateTime.now().millisecondsSinceEpoch),
  event = map['event'] ?? 'unknown',
  item = Item.from(map);

  Map<String, dynamic> toMap() =>
    {
      'time': time.millisecondsSinceEpoch, 
      'event': event,
      'id': item.id,
      'parentID': item.parentID,
      'content': item.content
    };
  
  @override
  String toString() =>
    'Step(time: $time, event: $event, item: $item';

  String toSql() =>
    "insert into steps (time, event, id, parentID, content) values (${time.millisecondsSinceEpoch}, '$event', ${item.id}, ${item.parentID}, '${item.content}');";
}