import SwiftUI
import KoinOwnerData

@main
struct iOSApp: App {
    init() {
        DataStoreModule_iosKt.setIOSKeyChainProvider(provider: IOSNativeKeyChainProvider())
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
