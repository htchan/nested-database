import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:marking/models/item.dart';
import 'package:marking/repostories/item_repostory.dart';

class InsertItemBottomSheet extends StatelessWidget {
  final Item parentItem;
  static TextEditingController contentController = TextEditingController();

  InsertItemBottomSheet({Key? key, required this.parentItem}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: MediaQuery.of(context).viewInsets,
      child:
      Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text("Insert new item"),
          TextField(
            controller: contentController,
            maxLines: null,
          ),
          ElevatedButton(
            onPressed: () {
              Item item = Item(id: -1, parentID: parentItem.id, content: contentController.text.trim());
              ItemRepostory.instance().create(item)
              .then( (value) {
                if (value != 0) {
                  contentController.text = "";
                  Navigator.pop(context);
                } else {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text("fail to create item"))
                  );
                }
              });
            },
            child: const Text("Confirm"),
          ),
        ],
      )
    );
  }
}