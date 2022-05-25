import 'package:flutter/material.dart';

class MoveItemBottomSheet extends StatelessWidget {
  final String title;
  final Function confirm, cancel;
  static TextEditingController contentController = TextEditingController();

  const MoveItemBottomSheet({
    Key? key,
    required this.title,
    required this.confirm,
    required this.cancel,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: MediaQuery.of(context).viewInsets,
      child:
      Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(title),
          Row(
            children: [
              Expanded(
                child: ElevatedButton(
                  child: const Text("confirm"),
                  onPressed: () => confirm(),
                  style: ElevatedButton.styleFrom(
                    primary: Colors.green
                  ),
                ),
              ),
              Expanded(
                child: ElevatedButton(
                  child: const Text("Cancel"),
                  onPressed: () => cancel(),
                  style: ElevatedButton.styleFrom(
                    primary: Colors.red
                  ),
                ),
              ),
            ],
            
          ),
        ],
      )
    );
  }
}