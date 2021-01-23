#import "FlutterSafetynetPlugin.h"
#if __has_include(<flutter_safetynet/flutter_safetynet-Swift.h>)
#import <flutter_safetynet/flutter_safetynet-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_safetynet-Swift.h"
#endif

@implementation FlutterSafetynetPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterSafetynetPlugin registerWithRegistrar:registrar];
}
@end
