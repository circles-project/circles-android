# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


## OkHttp start https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
## OkHttp end

## Glide start https://github.com/bumptech/glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
## Glide end

## Navigation component custom start
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class * implements android.os.Parcelable {
*;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class * extends java.io.Serializable
## Navigation component custom end

# RenderScript
-keepclasseswithmembernames class * {
native <methods>;
}
-keep class androidx.renderscript.** { *; }

-keep class org.futo.circles.core.model.CircleRoomTypeArg
-keep class org.futo.circles.auth.model.PasswordModeArg
-keep class org.futo.circles.auth.model.TermsModeArg

-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**

-keep class org.openjsse.** { *; }
-dontwarn org.openjsse.**

### Google Play Billing
-keep class com.android.vending.billing.**

### Matrix sdk
-keep class org.matrix.android.** { *; }
-keep class com.squareup.moshi.** { *; }
### Matrix sdk

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
