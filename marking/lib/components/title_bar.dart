import 'package:flutter/widgets.dart';

class TitleBar extends StatelessWidget {
  final String title;
  TitleBar({Key? key, required this.title});

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    return Container(
      padding: const EdgeInsets.fromLTRB(5, 5, 5, 0),
      constraints: BoxConstraints(
        maxHeight: size.height * 0.3,
        minWidth: size.width
      ),
      child: SingleChildScrollView(
        child: Text(
          title,
          style: const TextStyle(
            fontSize: 24,
          ),
          textAlign: TextAlign.left,
        ),
      ),
    );
  }
}