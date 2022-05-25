class Item {
  int parentID, id;
  String content;
  
  Item({
    required this.id,
    required this.parentID,
    required this.content
  });
  Item.from(Map<String, dynamic> map):
  id = map['id'] ?? -1,
  parentID = map['parentID'] ?? -1,
  content = map['content'] ?? '';

  Map<String, dynamic> toMap() =>
    {
      'parentID': parentID,
      'content': content
    };

  Map<String, dynamic> toFullMap() =>
    {
      'id': id,
      'parentID': parentID,
      'content': content
    };

  @override
  String toString() =>
    'Item{id: $id, parentID: $parentID, content: $content}';

  @override
  bool operator==(Object other) {
    return toString() == other.toString();
  }
}