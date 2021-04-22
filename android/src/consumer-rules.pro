# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/zhangjg/deve/tools/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#####################################################################
#                common rules for java and android                  #
#####################################################################

-optimizationpasses 5

#混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames

#指定不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#不预校验
#-dontpreverify

#不优化输入的类文件
#-dontoptimize

-ignorewarnings

-verbose

#优化
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护内部类
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class javax.** { *; }
-keep class java.** { *; }
-keep class sun.** { *; }

-keep class org.apache.** { *; }
-dontwarn org.apache.**

-keep class com.android.** { *; }

-keepnames class * extends org.apache.http.** { *; }

# 保留序列化。
# 比如我们要向activity传递对象使用了Serializable接口的时候，这时候这个类及类里面#的所有内容都不能混淆。
# 这里如果项目有用到序列化和反序列化要加上这句。
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable { *; }
-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable { *; }

-keep public class * extends android.widget.BaseAdapter {*;}
-keep public class * extends android.widget.CusorAdapter {*;}
-keep public class * extends android.widget.Adapter {*;}

-keep public class *R$*{
    public static final int *;
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View {*;}
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep class android.support.v4.** {*;}
-keep class android.support.v7.** {*;}
-keep public class * extends android.widget.**

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    void onClick*(...);
}
-keepclasseswithmembers class * {
    *** *Callback(...);
}
-keep class android.** { *; }

-dontnote **
-dontshrink

-target 1.7

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-dontwarn okio.**
-dontwarn com.alibaba.fastjson.**
-dontwarn com.ak.**
-dontwarn io.reactivex.**
-dontwarn android.webkit.**
-dontwarn com.bytedance.sdk.**
-keep class com.alibaba.** { *; }
# si start
-keep class android.** { *; }
# si end
-keep class com.fighter.** { *; }
-keep class com.liulishuo.okdownload.** { *; }
-keep class com.alibaba.** { *; }
-keep class com.ak.** { *; }
-keep class io.reactivex.** { *; }
# Immo start
-keep class com.anyun.immo.** {*;}
# Immo end

# csj sdk start
-dontwarn com.bytedance.article.common.nativecrash.NativeCrashInit
-keep class com.bytedance.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.** {*;}
-keep class com.ss.android.** {*;}
# csj sdk end

# GDT sdk start
-dontwarn com.qq.e.**
-keep class com.qq.e.** {*;}
-dontpreverify
# GDT sdk end

#baidu start
-keep class com.baidu.mobads.** { *; }
#baidu end

#domob begin
-keep class com.dm.sdk.** {*;}
#domob end
#ks begin
-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.**{ *; }
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**
#ks end
