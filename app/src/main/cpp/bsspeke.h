/*
 * bsspeke.h - BS-SPEKE over Curve25519
 *
 * Author: Charles V. Wright <cvwright@futo.org>
 * 
 * Copyright (c) 2022 FUTO Holdings, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef BSSPEKE_H
#define BSSPEKE_H

#include <stddef.h>
#include <sys/types.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

#define BSSPEKE_VERIFY_CLIENT_MODIFIER "client"
#define BSSPEKE_VERIFY_CLIENT_MODIFIER_LEN 6

#define BSSPEKE_VERIFY_SERVER_MODIFIER "server"
#define BSSPEKE_VERIFY_SERVER_MODIFIER_LEN 6

#define BSSPEKE_ARGON2_MIN_PHF_BLOCKS 100000
#define BSSPEKE_ARGON2_MIN_PHF_ITERATIONS 3

typedef struct {

    uint8_t client_id[256];  // Client's identifier (eg Matrix user_id)
    size_t client_id_len;

    uint8_t password[256];   // User's password
    size_t password_len;

    uint8_t K_password[32];  // Password-derived secret key

    uint8_t server_id[256];  // Server's identifier (eg domain name)
    size_t server_id_len;

    uint8_t r[32];           // Random number to blind the password in the OPRF

    uint8_t B[32];           // Server's ephemeral public key

    uint8_t a[32];           // Ephemeral private key
    uint8_t A[32];           // Ephemeral public key

    uint8_t v[32];           // Long term keys
    uint8_t p[32];

    uint8_t K_c[32];         // Session key

} bsspeke_client_ctx;


typedef struct {

    uint8_t server_id[256];  // Server's identifier (eg domain name)
    size_t server_id_len;

    uint8_t client_id[256];  // Client's identifier (eg Matrix user_id)
    size_t client_id_len;

    uint8_t b[32];           // Ephemeral private key
    uint8_t B[32];           // Ephemeral public key
    
    uint8_t A[32];           // Client's ephemeral public key

    uint8_t K_s[32];         // Session key

} bsspeke_server_ctx;



int
bsspeke_client_init(bsspeke_client_ctx *ctx,
                    const char* client_id, const size_t client_id_len,
                    const char* server_id, const size_t server_id_len,
                    const char* password, const size_t password_len);

int
bsspeke_server_init(bsspeke_server_ctx *ctx,
                    const char* server_id, const size_t server_id_len,
                    const char* client_id, const size_t client_id_len);

void
bsspeke_client_generate_blind(uint8_t blind[32],
                              bsspeke_client_ctx *client);

void
bsspeke_server_blind_salt(uint8_t blind_salt[32],
                          const uint8_t blind[32],
                          const uint8_t *salt, const size_t salt_len);

void
bsspeke_server_generate_B(const uint8_t P[32],
                          bsspeke_server_ctx *server);

void
bsspeke_server_get_B(uint8_t B[32],
                     bsspeke_server_ctx *server);

void
bsspeke_client_generate_hashed_key(uint8_t k[32],
                                   const uint8_t *msg, size_t msg_len,
                                   bsspeke_client_ctx *client);

int
bsspeke_client_generate_P_and_V(uint8_t P[32], uint8_t V[32],
                                const uint8_t blind_salt[32],
                                uint32_t phf_blocks, uint32_t phf_iterations,
                                bsspeke_client_ctx *client);

int
bsspeke_client_generate_A(const uint8_t blind_salt[32],
                          uint32_t phf_blocks, uint32_t phf_iterations,
                          bsspeke_client_ctx *client);

void
bsspeke_client_get_A(uint8_t A[32],
                     bsspeke_client_ctx *client);

void
bsspeke_client_derive_shared_key(const uint8_t B[32],
                                 bsspeke_client_ctx *client);

void
bsspeke_client_generate_verifier(uint8_t client_verifier[32],
                                 bsspeke_client_ctx *client);


void
bsspeke_server_derive_shared_key(const uint8_t A[32],
                                 const uint8_t V[32],
                                 bsspeke_server_ctx *server);

int
bsspeke_server_verify_client(const uint8_t client_verifier[32],
                             bsspeke_server_ctx *server);

void
bsspeke_server_generate_verifier(uint8_t server_verifier[32],
                                 bsspeke_server_ctx *server);

int
bsspeke_client_verify_server(const uint8_t server_verifier[32],
                             const bsspeke_client_ctx *client);

#ifdef __cplusplus
}
#endif

#endif
