# Keep Kotlin serialization metadata used by Supabase models.
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature

# Keep kotlinx.serialization generated serializers discoverable at runtime.
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers,includedescriptorclasses class ** {
    *** Companion;
}

# Supabase/Ktor may use reflective service loading.
-keepnames class io.ktor.**

# Do not preserve debug-only source metadata in the release artifact.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
