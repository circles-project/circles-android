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
-keep class org.futo.circles.core.model.ShareUrlTypeArg
-keep class org.futo.circles.model.PeopleCategoryTypeArg
-keep class org.futo.circles.core.model.SelectRoomTypeArg
-keep class org.futo.circles.core.model.RoomRequestTypeArg

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

 #### Gson
 -keepattributes Signature

 -keepattributes *Annotation*

 -dontwarn sun.misc.**
 -keep class com.google.gson.stream.** { *; }

 -keep class com.google.gson.examples.android.model.** { <fields>; }

 # Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
 # JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
 -keep class * extends com.google.gson.TypeAdapter
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer

 -keep class * {
   @com.google.gson.annotations.SerializedName <fields>;
 }

 # Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
 -keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
 -keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

 ####Gson

 -dontwarn java.awt.**
 -dontwarn org.slf4j.**

 ###JNA
 -keep class com.sun.jna.** { *; }
 -keep class * implements com.sun.jna.** { *; }

 ###Passkeys
 -if class androidx.credentials.CredentialManager
 -keep class androidx.credentials.playservices.** {
   *;
 }

 # For TagSoup (io.element.android:wysiwyg)
 -keep class org.ccil.cowan.tagsoup.** { *; }
