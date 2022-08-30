
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

#include "bsspeke.h"


JNIEXPORT jlong JNICALL
Java_org_futo_circles_bsspeke_BSSpekeUtils_getClientContext(JNIEnv *env, jobject thiz) {
    bsspeke_client_ctx *ctx;
    ctx = malloc(sizeof(*ctx));
    return (jlong)
    ctx;
}

JNIEXPORT jlong JNICALL
Java_org_futo_circles_bsspeke_BSSpekeUtils_getServerContext(JNIEnv *env, jobject thiz) {
    bsspeke_server_ctx *ctx;
    ctx = malloc(sizeof(*ctx));
    return (jlong)
    ctx;
}

JNIEXPORT void JNICALL
Java_org_futo_circles_bsspeke_BSSpekeUtils_initClient(
        JNIEnv *env,
jobject thiz,
jlong client_context,
jstring client_id_str,
jstring server_id_str,
jstring password_str) {

const char *client_id = (*env)->GetStringUTFChars(env, client_id_str, NULL);
size_t client_id_len = (size_t)(*env)->GetStringUTFLength(env,client_id_str);

const char *server_id = (*env)->GetStringUTFChars(env, server_id_str, NULL);
size_t server_id_len = (size_t)(*env)->GetStringUTFLength(env,server_id_str);

const char *password = (*env)->GetStringUTFChars(env, password_str, NULL);
size_t password_len = (size_t)(*env)->GetStringUTFLength(env,password_str);

bsspeke_client_init((bsspeke_client_ctx*) client_context,
                    client_id,client_id_len,server_id,server_id_len,password,password_len
                    );

(*env)->ReleaseStringUTFChars(env, client_id_str, client_id);
(*env)->ReleaseStringUTFChars(env, server_id_str, server_id);
(*env)->ReleaseStringUTFChars(env, password_str, password);
}

JNIEXPORT void JNICALL
Java_org_futo_circles_bsspeke_BSSpekeUtils_initServer(JNIEnv
* env,
jobject thiz, jlong
client_context,
jstring client_id_str, jstring
server_id_str) {

const char *client_id = (*env)->GetStringUTFChars(env, client_id_str, NULL);
size_t client_id_len = (size_t)(*env)->GetStringUTFLength(env,client_id_str);

const char *server_id = (*env)->GetStringUTFChars(env, server_id_str, NULL);
size_t server_id_len = (size_t)(*env)->GetStringUTFLength(env,server_id_str);

bsspeke_server_init((bsspeke_server_ctx *) client_context,
client_id,client_id_len,server_id,server_id_len
);

(*env)->ReleaseStringUTFChars(env, client_id_str, client_id);
(*env)->ReleaseStringUTFChars(env, server_id_str, server_id);
}

