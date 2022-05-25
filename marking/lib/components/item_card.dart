import 'package:flutter/material.dart';
import 'package:marking/screens/all.dart';
import '../models/item.dart';
import 'package:flutter_slidable/flutter_slidable.dart';
import '../repostories/item_repostory.dart';

class ItemCard extends StatefulWidget {
  final bool enabled;
  Item item;
  final Function reloadPage;
  final Function(Item) move, setParentItem;
  ItemCard({
    Key? key,
    required this.item,
    required this.reloadPage,
    required this.move,
    required this.setParentItem,
    this.enabled = true,
  }) : super(key: key);

  @override
  State<ItemCard> createState() => _ItemCardState();
}

class _ItemCardState extends State<ItemCard> {
  bool editable = false;
  TextEditingController contentController = TextEditingController();
  _ItemCardState();

  @override
  Widget build(BuildContext context) {
    if (!widget.enabled) {
      return ReadOnlyItemCard(
        item: widget.item,
        reloadPage: widget.reloadPage,
        setParentItem: widget.setParentItem,
      );
    } else if (editable) {
      return EditableItemCard(
        item: widget.item,
        save: (Item item) {
          ItemRepostory.instance().update(item)
          .then( (value) {
            if (value != 0) {
              setState( () {
                editable = false;
                widget.item = item;
              });
            } else {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content:  Text("failed to save, press cancel to discard changes"))
              );
            }
          });
        },
        cancel: () => setState( () => editable = false ),
      );
    } else {
      return BasicItemCard(
        item: widget.item,
        delete: (item) {
          ItemRepostory.instance().delete(item)
          .then( (_) => widget.reloadPage() );
        },
        edit: () => setState( () => editable = true ),
        move: (item) {
          widget.move(item);
        },
        reloadPage: widget.reloadPage,
        setParentItem: widget.setParentItem,
      );
    }
  }
}

class ReadOnlyItemCard extends StatelessWidget {
  final Item item;
  final Function reloadPage;
  final Function(Item) setParentItem;

  const ReadOnlyItemCard({
    Key? key,
    required this.item,
    required this.reloadPage,
    required this.setParentItem,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => setParentItem(item),
      child: ListTile(
        title: Text(item.content),
        tileColor: Colors.grey.shade300,
      ),
    );
  }
}

class BasicItemCard extends StatelessWidget {
  final Item item;
  final Function(Item) delete, move, setParentItem;
  final Function edit, reloadPage;

  const BasicItemCard({
    Key? key,
    required this.item,
    required this.delete,
    required this.move,
    required this.setParentItem,
    required this.edit,
    required this.reloadPage
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Slidable(
      child: GestureDetector(
        onTap: () => setParentItem(item),
        child: ListTile(
          title: Text(item.content),
        ),
      ),
      startActionPane: ActionPane(
        extentRatio: 0.2,
        motion: const ScrollMotion(),
        children: [
          SlidableAction(
            onPressed: (context) => move(item),
            backgroundColor: Colors.blue,
            foregroundColor: Colors.white,
            icon: Icons.drive_file_move,
            label: 'move',
          ),
        ],
      ),
      endActionPane: ActionPane(
        extentRatio: 0.4,
        motion: const ScrollMotion(),
        children: [
          SlidableAction(
            onPressed: (context) => edit(),
            backgroundColor: Colors.yellow,
            foregroundColor: Colors.white,
            icon: Icons.edit,
            label: 'Edit'
          ),
          SlidableAction(
            onPressed: (context) => delete(item),
            backgroundColor: Colors.red,
            foregroundColor: Colors.white,
            icon: Icons.delete,
            label: 'Delete'
          ),
        ],
      ),
      
    );
  }
}

class EditableItemCard extends StatelessWidget {
  final Item item;
  final Function(Item) save;
  final Function cancel;
  final TextEditingController contentController = TextEditingController();

  EditableItemCard({
    Key? key,
    required this.item,
    required this.save,
    required this.cancel
  }) : super(key: key) {
    contentController.text = item.content;
  }

  @override
  Widget build(BuildContext context) {
    return Slidable(
      child: ListTile(
        title: TextField(controller: contentController,),
        trailing: IconButton(
          icon: const Icon(Icons.save),
          onPressed: () {
            item.content = contentController.text;
            save(item);
          },
        ),
      ),
      endActionPane: ActionPane(
        extentRatio: 0.2,
        motion: const ScrollMotion(),
        children: [
          SlidableAction(
            onPressed: (context) => cancel(),
            backgroundColor: Colors.red,
            foregroundColor: Colors.white,
            icon: Icons.cancel,
            label: 'cancel',
          )
        ],
      ),
    );
  }
}