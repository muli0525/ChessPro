# 添加项目相关的 ProGuard 规则
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep OpenCV classes
-keep class org.opencv.** { *; }
-dontwarn org.opencv.**

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Compose classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep CameraX classes
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Keep our chess classes
-keep class com.chesspro.app.core.chess.** { *; }
-keep class com.chesspro.app.core.recognition.** { *; }
-keep class com.chesspro.app.ui.** { *; }

# Keep data classes
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
