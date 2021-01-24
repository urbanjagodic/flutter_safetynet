class SafetynetException implements Exception {
  SafetynetException(this.name, this.message);

  String message;
  String name;

  @override
  String toString() => "SafetynetException: $name, message: $message";
}