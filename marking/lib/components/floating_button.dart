
import 'package:flutter/material.dart';
import 'package:marking/components/insert_item_bottom_sheet.dart';
import 'package:marking/components/move_item_bottom_sheet.dart';
import 'package:marking/models/item.dart';

class FloatingButton extends StatelessWidget {
  final IconData icon;
  final Function reload;
  final Function(BuildContext) showBottomSheet;
  const FloatingButton({
    Key? key,
    required this.icon,
    required this.reload,
    required this.showBottomSheet
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: () => showBottomSheet(context),
      backgroundColor: Colors.blue,
      child: Icon(icon),
    );
  }
}
