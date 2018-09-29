#include <jni.h>
#include <string>

int fib(int x) {
    if (x == 0)
        return 0;

    if (x == 1)
        return 1;

    return fib(x - 1) + fib(x - 2);
}

int fastFib(const int &n) {
    int first = 0;
    int second = 1;
    int counter = 2;
    while (counter < n) {
        int temp = second;
        second = first + second;
        first = temp;
        ++counter;
    }
    if (n == 0)
        return 0;
    else
        return first + second;
}

// com.io.extended.profilerexample.view
extern "C" JNIEXPORT jint JNICALL
Java_com_io_extended_profilerexample_AlarmBroadcastReceiver_nativeFibonacci(
        JNIEnv *env,
        jobject /* this */,
        jint number

) {
    return fib(number);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_io_extended_profilerexample_AlarmBroadcastReceiver_nativeFastFibonacci(
        JNIEnv *env,
        jobject /* this */,
        jint number

) {
    return fastFib(number);
}
