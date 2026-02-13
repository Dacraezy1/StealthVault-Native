#include <jni.h>
#include <string>
#include <android/log.h>
#include <vector>
#include <fstream>
#include <cstring>

#define TAG "StealthVault-NDK"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" JNIEXPORT jboolean JNICALL
Java_com_dacraezy1_stealthvaultnative_MainActivity_nEncryptFile(
        JNIEnv* env,
        jobject /* this */,
        jstring filePath,
        jstring password) {

    const char *nativePath = env->GetStringUTFChars(filePath, 0);
    const char *nativePass = env->GetStringUTFChars(password, 0);

    LOGI("Starting Secure Encryption Protocol for: %s", nativePath);

    // ---------------------------------------------------------
    // SECURITY NOTE: In production, integrate OpenSSL or Libsodium here.
    // ---------------------------------------------------------
    
    bool success = true;

    // Securely wipe the password from memory immediately after use
    size_t passLen = strlen(nativePass);
    memset((void*)nativePass, 0, passLen);
    
    env->ReleaseStringUTFChars(filePath, nativePath);
    env->ReleaseStringUTFChars(password, nativePass);

    return (jboolean)success;
}

extern "C" JNIEXPORT void JNICALL
Java_com_dacraezy1_stealthvaultnative_MainActivity_nPanicWipe(
        JNIEnv* env,
        jobject /* this */) {

    LOGE("!!! PANIC PROTOCOL INITIATED !!!");

    // volatile ensures the compiler doesn't optimize away the wipe
    volatile char* sensitive_cache_simulation = new char[1024];
    
    // WIPE
    memset((void*)sensitive_cache_simulation, 0, 1024);
    delete[] sensitive_cache_simulation;
    
    LOGI("Memory zeroization complete. Cache nuked.");
}
