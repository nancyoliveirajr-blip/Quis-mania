# QUIZ MANIA - Commercial Production Proguard & R8 Obfuscation Rules

# Strip android logging calls in release builds for security
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep Data Models for JSON serialization
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
    @androidx.room.Entity *;
    @androidx.room.Dao *;
}

# Room Database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retain annotations for runtime reflection where required
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Obfuscate internal engine class and method names
-repackageclasses 'com.aistudio.quizmania.obf'
-allowaccessmodification

