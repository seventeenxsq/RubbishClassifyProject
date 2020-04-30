#include <jni.h>
#include <string>

extern "C" jstring
Java_com_xu_nativec_MainActivity_helloJNI(
        JNIEnv* env,
        jobject /* this */) {
    char* hello = reinterpret_cast<char *>((jstring) "H from C++");
    return env->NewStringUTF(hello);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_rubbishclassifywork_MainActivity_helloJNI(JNIEnv *env, jobject thiz) {
    // TODO: implement helloJNI()
    return env->NewStringUTF("hello");
}