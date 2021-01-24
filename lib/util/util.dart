import '../data/method_action.dart';

class Util {

  static String parseActionName(MethodAction action) {
    return action.toString().split(".")[1];
  }
}