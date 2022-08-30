
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

#include "bsspeke.h"
#include <jni.h>


JNIEXPORT jlong JNICALL
Java_org_futo_circles_bsspeke_BSSpekeUtils_getClientContext(JNIEnv *env, jobject thiz) {
    bsspeke_client_ctx *ctx;
    ctx = malloc(sizeof(*ctx));
    return (jlong) ctx;
}

JNIEXPORT jlong JNICALL
Java_org_futo_circles_bsspeke_BSSpekeUtils_getServerContext(JNIEnv *env, jobject thiz) {
    bsspeke_server_ctx *ctx;
    ctx = malloc(sizeof(*ctx));
    return (jlong) ctx;
}

