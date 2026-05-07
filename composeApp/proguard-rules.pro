# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class in.koreatech.business.**$$serializer { *; }
-keepclassmembers class in.koreatech.business.** {
    *** Companion;
}
-keepclasseswithmembers class in.koreatech.business.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Koin runtime resolves bindings by qualified class name; keep names but
# allow member shrinking and obfuscation of unused internals.
-keepnames class org.koin.**

# Ktor 3.x ships consumer ProGuard rules in its AARs; we trust those.
# Keep only the JVM-only references that Ktor pulls in but Android does not have.
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
