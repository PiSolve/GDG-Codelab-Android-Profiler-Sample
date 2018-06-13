#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_io_extended_profilerexample_MainActivity_stringFromJNIStopWatch(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "StopWatch";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_io_extended_profilerexample_MainActivity_stringFromJNITimer(
        JNIEnv* env,
jobject /* this */) {
std::string hello = "Timer";
return env->NewStringUTF(hello.c_str());
}
