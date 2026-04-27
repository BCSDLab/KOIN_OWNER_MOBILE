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

# Koin
-keep class org.koin.** { *; }

# Ktor (consumer rules included in AAR, but explicit for safety)
-keep class io.ktor.** { *; }

# Ktor references JVM-only classes not present on Android
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
