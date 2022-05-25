import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:marking/components/floating_button.dart';
import 'package:marking/components/insert_item_bottom_sheet.dart';
import 'package:marking/components/item_card.dart';
import 'package:marking/components/move_item_bottom_sheet.dart';
import 'package:marking/components/title_bar.dart';
import 'package:marking/repostories/item_repostory.dart';
import 'package:marking/repostories/step_repostory.dart';
import 'package:permission_handler/permission_handler.dart';

import '../models/item.dart';

class MainScreen extends StatefulWidget {
  final Item parentItem = Item(id: 0, parentID: -1, content: "main");
  MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState(parentItem);
}

class _MainScreenState extends State<MainScreen> {
  Item parentItem;
  Item? movingItem;
  List<Item> children = [];

  _MainScreenState(this.parentItem) {
    loadChildren();
  }

  Future<bool> onWillPop() async {
    Item? item = await ItemRepostory.instance().getParent(parentItem);
    if (item == null) {
      return true;
    } else {
      setParentItem(item);
      return false;
    }
  }

  void loadChildren() {
    ItemRepostory.instance().getChildren(parentItem)
    .then( (results) => setState( () {
      if (movingItem != null) results.remove(movingItem);
      children = results;
    }));
  }

  void setParentItem(Item item) {
    setState( () {
      parentItem = item;
      loadChildren();
    } );
  }

  void setMovingItem(Item item) {
    setState( () {
      movingItem = item;
      children.remove(item);
    });
  }

  void confirmMovingItem() {
    if (movingItem == null) return;
    
    movingItem!.parentID = parentItem.id;
    ItemRepostory.instance().update(movingItem!)
    .then( (value) {
      if (value != 0) {
        Navigator.pop(context);
        setState( () {
          movingItem = null;
          loadChildren();
        });
        ScaffoldMessenger.of(GlobalKey().currentContext!).showSnackBar(
          const SnackBar(content: Text("Move item succeed"))
        );
      } else {
        ScaffoldMessenger.of(GlobalKey().currentContext!).showSnackBar(
          const SnackBar(content: Text("failed to update moving item"))
        );
      }
    });
  }

  void cancelMovingItem() {
    Navigator.pop(context);
    setState( () => movingItem = null );
  }

  void showInsertItemBottomList(BuildContext context) {
    showModalBottomSheet<void>(
      context: context,
      builder: (BuildContext context) {
        return InsertItemBottomSheet(parentItem: parentItem);
      },
      isScrollControlled: true
    ).then( (_) => loadChildren() );
  }

  void showMoveItemBottomSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return MoveItemBottomSheet(
          title: "Confirm Move Item under ${parentItem.content}",
          confirm: confirmMovingItem,
          cancel: cancelMovingItem,
        );
      }
    ).then( (_) => loadChildren() );
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: onWillPop,
      child: Scaffold(
        appBar: AppBar(
          title: const Text("marking"),
          actions: [
            IconButton(
              onPressed: () async {
                StepRepostory.instance().import()
                .then( (result) {
                  if (result) {
                    loadChildren();
                  }
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text("import ${result? "succeed" : "failed"}"))
                  );
                });
              },
              icon: const Icon(Icons.download),
            ),
            IconButton(
              onPressed: () async {
                StepRepostory.instance().export()
                .then( (result) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text("export ${result? "succeed" : "failed"}"))
                  );
                });
              },
              icon: const Icon(Icons.upload),
            ),
          ]
        ),
        body: Column(
          children: [
            TitleBar(title: parentItem.content),
            const Divider(color: Colors.black, thickness: 2,),
            Expanded(
              child: children.isEmpty ? const Text("") : ListView.builder(
                itemCount: children.length,
                itemBuilder: (context, index) => ItemCard(
                  enabled: (movingItem == null),
                  item: children[index],
                  reloadPage: loadChildren,
                  move: setMovingItem,
                  setParentItem: setParentItem,
                ),
              ),
            ),
          ],
        ),
        floatingActionButton: FloatingButton(
          reload: loadChildren,
          icon: (movingItem == null) ? Icons.add : Icons.info_outline,
          showBottomSheet:
            (movingItem == null) ?
            showInsertItemBottomList :
            showMoveItemBottomSheet,
        ),
      ),
    );
  }
}