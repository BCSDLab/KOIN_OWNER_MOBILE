//
//  iOSKeyChainProvider.swift
//  iosApp
//
//  Created by WooJin Kong on 9/22/25.
//

import KoinOwnerData

class IOSNativeKeyChainProvider: IOSKeyChainProvider {
    func createData(key: String, value: String) {
        let query: NSDictionary = [
                    kSecClass: kSecClassGenericPassword,
                    kSecAttrAccount: key,
                    kSecValueData: value.data(using: .utf8, allowLossyConversion: false) as Any
                ]
                SecItemDelete(query)
                
                let status = SecItemAdd(query, nil)
                if status != errSecSuccess {
                    print("Failed to save token, status code: \(status)")
                }
    }
    
    func deleteData(key: String) {
        let query: NSDictionary = [
                    kSecClass: kSecClassGenericPassword,
                    kSecAttrAccount: key
                ]
                let status = SecItemDelete(query)
                  if status == errSecSuccess {
                      print("Item successfully deleted")
                  } else if status == errSecItemNotFound {
                      print("Item not found")
                  } else {
                      print("Error deleting the item, status code: \(status)")
                  }
    }
    
    func readData(key: String) -> String? {
        let query: NSDictionary = [
                    kSecClass: kSecClassGenericPassword,
                    kSecAttrAccount: key,
                    kSecReturnData: kCFBooleanTrue as Any,
                    kSecMatchLimit: kSecMatchLimitOne
                ]
                
                var dataTypeRef: AnyObject?
                let status = SecItemCopyMatching(query, &dataTypeRef)
                
                if status == errSecSuccess {
                    if let retrievedData: Data = dataTypeRef as? Data {
                        let value = String(data: retrievedData, encoding: String.Encoding.utf8)
                        return value
                    } else { return nil }
                } else {
                    print("failed to loading, status code = \(status)")
                    return nil
                }
    }
}
