class BrowsingThreat {

  BrowsingThreat(this.threat);
  final BrowsingThreatType threat;

  int getType() {
    switch (threat) {
      case BrowsingThreatType.POTENTIALLY_HARMFUL_APPLICATION:
        return 4;
      case BrowsingThreatType.SOCIAL_ENGINEERING:
        return 5;
      default:
        return null;
    }
  }
}

enum BrowsingThreatType {
  POTENTIALLY_HARMFUL_APPLICATION,
  SOCIAL_ENGINEERING
}