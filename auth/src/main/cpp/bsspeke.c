/*
 * bsspeke.c - BS-SPEKE over Curve25519
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "minimonocypher.h"
#include "bsspeke.h"

enum debug_level {
    LOG_DEBUG,
    LOG_INFO,
    LOG_WARN,
    LOG_ERROR,
    LOG_FATAL
};
typedef enum debug_level debug_level_t;

//typedef int debug_level_t;

const uint8_t null_byte = 0;

debug_level_t curr_level = LOG_DEBUG;

void debug(debug_level_t level, const char *msg)
{
    //if( level >= curr_level ) {
        puts(msg);
    //}
}

void print_point(const char *label, const uint8_t point[32])
{
    printf("%8s:\t[", label);
    int i = 31;
    for(i=31; i >= 0; i--)
        printf("%02x", point[i]);
    printf("]\n");
}

void
generate_random_bytes(uint8_t *buf, size_t len)
{
#ifdef linux
    getrandom(buf, len, 0);
#else
    arc4random_buf(buf, len);
#endif
}

int
bsspeke_client_init
    (
        bsspeke_client_ctx *ctx,
        const char* client_id, const size_t client_id_len,
        const char* server_id, const size_t server_id_len,
        const char* password, const size_t password_len
    )
{
    if( client_id_len > 255 ) {
        return -1;
    }
    crypto_wipe(ctx->client_id, 256);
    memcpy(ctx->client_id, client_id, client_id_len);
    ctx->client_id_len = client_id_len;

    if( server_id_len > 255 ) {
        return -1;
    }
    crypto_wipe(ctx->server_id, 256);
    memcpy(ctx->server_id, server_id, server_id_len);
    ctx->server_id_len = server_id_len;

    if( password_len > 255 ) {
        return -1;
    }
    crypto_wipe(ctx->password, 256);
    memcpy(ctx->password, password, password_len);
    ctx->password_len = password_len;

    // Success!
    return 0;
}

int
bsspeke_server_init
    (
        bsspeke_server_ctx *ctx,
        const char* server_id, const size_t server_id_len,
        const char* client_id, const size_t client_id_len
    )
{
    if( server_id_len > 255 ) {
        return -1;
    }
    crypto_wipe(ctx->server_id, 256);
    memcpy(ctx->server_id, server_id, server_id_len);
    ctx->server_id_len = server_id_len;

    if( client_id_len > 255 ) {
        return -1;
    }
    crypto_wipe(ctx->client_id, 256);
    memcpy(ctx->client_id, client_id, client_id_len);
    ctx->client_id_len = client_id_len;

    // Success!
    return 0;
}

void
bsspeke_client_generate_blind
    (
        uint8_t blind[32],
        bsspeke_client_ctx *client
    )
{
    debug(LOG_DEBUG, "Hashing client's password");
    // 1. Hash the client's password, client_id, server_id to a point on the curve
    uint8_t scalar_hash[32];
    uint8_t curve_point[32];
    {
        crypto_blake2b_ctx hash_ctx;
        // Give us a 256 bit (32 byte) hash; Don't use a key
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        // Add the client id, server id, and the password to the hash
        crypto_blake2b_update(&hash_ctx,
                              (const uint8_t *)(client->password),
                              client->password_len);
        crypto_blake2b_update(&hash_ctx,
                              (const uint8_t *)(client->client_id),
                              client->client_id_len);
        crypto_blake2b_update(&hash_ctx,
                              (const uint8_t *)(client->server_id),
                              client->server_id_len);
        // Write the digest value into `scalar_hash`
        crypto_blake2b_final(&hash_ctx, scalar_hash);
    }
    debug(LOG_DEBUG, "Mapping password hash onto the curve");
    // Now use Elligator to map our scalar hash to a point on the curve
    crypto_hidden_to_curve(curve_point, scalar_hash);

    print_point("H(pass)", curve_point);

    // 2. Generate random r
    //    * Actually generate 1/r first, and clamp() it
    //      That way we know it will always lead us back to a point on the curve
    //    * Then use the inverse of 1/r as `r`
    //  FIXME: On second thought, monocypher seems to handle all of this complexity for us.  Let's see what happens if we just do things the straightforward way for now...
    debug(LOG_DEBUG, "Generating random blind `r`");
    generate_random_bytes(client->r, 32);
    print_point("r", client->r);
    debug(LOG_DEBUG, "Clamping r");
    crypto_x25519_clamp(client->r);
    print_point("r", client->r);

    // 3. Multiply our curve point by r
    debug(LOG_DEBUG, "Multiplying curve point by r");
    crypto_x25519_scalarmult(blind, client->r, curve_point, 256);
    print_point("blind", blind);
    debug(LOG_DEBUG, "Done");

    return;
}

void
bsspeke_server_blind_salt
    (
        uint8_t blind_salt[32],
        const uint8_t blind[32],
        const uint8_t *salt, const size_t salt_len
    )
{
    print_point("salt", salt);

    // Hash the salt
    debug(LOG_DEBUG, "Hashing the salt");
    uint8_t H_salt[32];
    crypto_blake2b_general(H_salt, 32,
                           NULL, 0,
                           salt,
                           salt_len);
    // Use clamp() to ensure we stay on the curve in the multiply below
    crypto_x25519_clamp(H_salt);
    print_point("H_salt", H_salt);

    // Multiply H(salt) by blind, save into blind_salt
    debug(LOG_DEBUG, "Multiplying H_salt by the user's blind");
    crypto_x25519_scalarmult(blind_salt, H_salt, blind, 256);
    print_point("blndsalt", blind_salt);
}

void
bsspeke_server_generate_B
    (
        const uint8_t P[32],
        bsspeke_server_ctx *server
    )
{
    // Generate random ephemeral private key b, save it in server->b
    debug(LOG_DEBUG, "Generating ephemeral private key b");
    generate_random_bytes(server->b, 32);
    crypto_x25519_clamp(server->b);
    print_point("b", server->b);

    debug(LOG_DEBUG, "Using user's base point P");
    print_point("P", P);

    // Compute public key B = b * P, save it in B
    debug(LOG_DEBUG, "Computing ephemeral public key B = b * P");
    crypto_x25519_scalarmult(server->B, server->b, P, 256);
    print_point("B", server->B);

}

void
bsspeke_server_get_B
    (
        uint8_t B[32],
        bsspeke_server_ctx *server
    )
{
    memcpy(B, server->B, 32);
}

int
bsspeke_client_generate_master_key
    (
        const uint8_t blind_salt[32],
        uint32_t phf_blocks, uint32_t phf_iterations,
        bsspeke_client_ctx *client
    )
{
    // Sanity checks first, before we do any work
    if( phf_blocks < BSSPEKE_ARGON2_MIN_PHF_BLOCKS ) {
        debug(LOG_ERROR, "Requested PHF blocks is below the minimum");
        return -1;
    }
    if( phf_iterations < BSSPEKE_ARGON2_MIN_PHF_ITERATIONS ) {
        debug(LOG_ERROR, "Requested PHF iterations is below the minimum");
        return -1;
    }

    uint8_t oblivious_salt[32];
    // Multiply the blinded salt by 1/r to get the oblivious salt
    // Here we rely on Monocypher to do the heavy lifting for us
    debug(LOG_DEBUG, "Removing the blind from the oblivious salt");
    crypto_x25519_inverse(oblivious_salt, client->r, blind_salt);
    print_point("obv_salt", oblivious_salt);

    // Hash the oblivious salt together with the id's to create the salt for the PHF
    debug(LOG_DEBUG, "Creating the salt for the PHF");
    uint8_t phf_salt[32];
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx, oblivious_salt, 32);
        crypto_blake2b_update(&hash_ctx,
                              client->client_id,
                              client->client_id_len);
        crypto_blake2b_update(&hash_ctx, &null_byte, 1);  // Insert a NULL between the client id and the password
        crypto_blake2b_update(&hash_ctx,
                              client->server_id,
                              client->server_id_len);
        crypto_blake2b_final(&hash_ctx, phf_salt);
    }
    print_point("phf_salt", phf_salt);

    debug(LOG_DEBUG, "Running the PHF to generate K_password");
    void *work_area;
    if ((work_area = malloc(phf_blocks * 1024)) == NULL) {
        return -1;
    }
    crypto_argon2i(client->K_password, 32, work_area,
                   phf_blocks, phf_iterations,
                   client->password, client->password_len,
                   phf_salt, 32);
    free(work_area);
    return 0;
}

void
bsspeke_client_generate_hashed_key
    (
        uint8_t k[32],
        const uint8_t *msg, size_t msg_len,
        bsspeke_client_ctx *client
    )
{
    crypto_blake2b_ctx hash_ctx;
    crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
    crypto_blake2b_update(&hash_ctx, client->K_password, 32);
    crypto_blake2b_update(&hash_ctx, msg, msg_len);
    crypto_blake2b_update(&hash_ctx, &null_byte, 1);
    crypto_blake2b_final(&hash_ctx, k);
}


int
bsspeke_client_generate_keys_from_password
    (
        const uint8_t blind_salt[32],
        uint32_t phf_blocks, uint32_t phf_iterations,
        bsspeke_client_ctx *client
    )
{
   /*
    crypto_argon2i(password_hash, 64, work_area,
                   phf_blocks, phf_iterations,
                   client->password, client->password_len,
                   phf_salt, 32);
    */

    /*
    // p || v = pwKdf(password, BlindSalt, idC, idS, settings)
    uint8_t *tmp_p = &(password_hash[0]);
    uint8_t *tmp_v = &(password_hash[32]);
    // clamp() v before we do anything else with it
    crypto_x25519_clamp(tmp_v);

    memcpy(client->p, tmp_p, 32);
    memcpy(client->v, tmp_v, 32);
    //crypto_wipe(password_hash, 64);
    */

    if( bsspeke_client_generate_master_key(blind_salt, phf_blocks, phf_iterations, client) != 0) {
        return -1;
    }

    const char *p_modifier = "curve_point_p";
    const char *v_modifier = "private_key_v";

    bsspeke_client_generate_hashed_key(client->p, (const uint8_t *) p_modifier, strlen(p_modifier), client);
    bsspeke_client_generate_hashed_key(client->v, (const uint8_t *) v_modifier, strlen(v_modifier), client);
    crypto_x25519_clamp(client->v);

    print_point("p", client->p);
    print_point("v", client->v);

    return 0;
}
        

int
bsspeke_client_generate_P_and_V
    (
        uint8_t P[32], uint8_t V[32],
        const uint8_t blind_salt[32],
        uint32_t phf_blocks, uint32_t phf_iterations,
        bsspeke_client_ctx *client
    )
{
    int rc = bsspeke_client_generate_keys_from_password(blind_salt,
                                                        phf_blocks, phf_iterations,
                                                        client);
    if( rc != 0 ) {
        debug(LOG_ERROR, "Password hashing function failed");
        return -1;
    }

    // Hash p onto the curve to get this user's base point P
    //uint8_t P[32];
    debug(LOG_DEBUG, "Hashing p onto the curve to get P");
    crypto_hidden_to_curve(P, client->p);
    print_point("P", P);

    // Generate our long-term public key V = v * P
    debug(LOG_DEBUG, "V = v * P");
    crypto_x25519_scalarmult(V, client->v, P, 256);
    print_point("V", V);

    return 0;
}


int
bsspeke_client_generate_A
    (
        const uint8_t blind_salt[32],
        uint32_t phf_blocks, uint32_t phf_iterations,
        bsspeke_client_ctx *client
    )
{
    int rc = bsspeke_client_generate_keys_from_password(blind_salt,
                                                        phf_blocks, phf_iterations,
                                                        client);
    if( rc != 0 ) {
        debug(LOG_ERROR, "Password hashing failed");
        return -1;
    }

    // Hash p onto the curve to get this user's base point P
    uint8_t P[32];
    debug(LOG_DEBUG, "Hashing p onto the curve to get P");
    crypto_hidden_to_curve(P, client->p);
    print_point("P", P);

    // Generate a random ephemeral private key a, store it in ctx->a
    debug(LOG_DEBUG, "Generating ephemeral private key a");
    //arc4random_buf(client->a, 32);
    generate_random_bytes(client->a, 32);
    crypto_x25519_clamp(client->a);
    print_point("a", client->a);
    // Generate the ephemeral public key A = a * P, store it in A
    debug(LOG_DEBUG, "Generating ephemeral public key A = a * P");
    crypto_x25519_scalarmult(client->A, client->a, P, 256);
    print_point("A", client->A);

    return 0;
}

void
bsspeke_client_get_A
    (
        uint8_t A[32],
        bsspeke_client_ctx *client
    )
{
    memcpy(A, client->A, 32);
}

void
bsspeke_client_derive_shared_key
    (
        const uint8_t B[32],
        bsspeke_client_ctx *client
    )
{
    // Compute the two Diffie-Hellman shared secrets 
    debug(LOG_DEBUG, "Computing Diffie-Hellman shared secrets");
    printf("%8s:\t[%s]\n",  "client", client->client_id);
    printf("%8s:\t[%zu]\n", "length", client->client_id_len);
    printf("%8s:\t[%s]\n",  "server", client->server_id);
    printf("%8s:\t[%zu]\n", "length", client->server_id_len);
    print_point("A", client->A);
    print_point("B", B);
    // DH shared secret from a * B
    uint8_t a_B[32];
    crypto_x25519(a_B, client->a, B);
    print_point("a * B", a_B);
    // DH shared secret from v * B
    uint8_t v_B[32];
    crypto_x25519(v_B, client->v, B);
    print_point("v * B", v_B);

    // Hash everything we know so far to generate our key, save it in ctx->K_c
    debug(LOG_DEBUG, "Hashing current state to get key K_c");
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx,
                              client->client_id,
                              client->client_id_len);
        crypto_blake2b_update(&hash_ctx, &null_byte, 1);  // Insert a NULL after the client id
        crypto_blake2b_update(&hash_ctx,
                              client->server_id,
                              client->server_id_len);
        crypto_blake2b_update(&hash_ctx, &null_byte, 1);  // Insert a NULL after the server id
        crypto_blake2b_update(&hash_ctx, client->A, 32);
        crypto_blake2b_update(&hash_ctx, B, 32);
        crypto_blake2b_update(&hash_ctx, a_B, 32);
        crypto_blake2b_update(&hash_ctx, v_B, 32);
        crypto_blake2b_final(&hash_ctx, client->K_c);
    }
    print_point("K_c", client->K_c);
}

void
bsspeke_client_generate_verifier
    (
        uint8_t client_verifier[32],
        bsspeke_client_ctx *client
    )
{
    // Hash k and the client modifier to get our verifier, save it in client_verifier
    debug(LOG_DEBUG, "Hashing K_c and modifier to get our verifier");
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx, client->K_c, 32);
        crypto_blake2b_update(&hash_ctx,
                              (uint8_t *)BSSPEKE_VERIFY_CLIENT_MODIFIER,
                              BSSPEKE_VERIFY_CLIENT_MODIFIER_LEN);
        crypto_blake2b_final(&hash_ctx, client_verifier);
    }
    print_point("client_v", client_verifier);
}

void
bsspeke_server_derive_shared_key
    (
        const uint8_t A[32],
        const uint8_t V[32],
        bsspeke_server_ctx *server
    )
{
    // Compute the two Diffie-Hellman shared secrets
    debug(LOG_DEBUG, "Computing Diffie-Hellman shared secrets");
    // DH shared secret from b * A
    uint8_t b_A[32];
    printf("%8s:\t[%s]\n", "client", server->client_id);
    printf("%8s:\t[%zu]\n", "length", server->client_id_len);
    printf("%8s:\t[%s]\n", "server", server->server_id);
    printf("%8s:\t[%zu]\n", "length", server->server_id_len);
    print_point("A", A);
    print_point("B", server->B);
    crypto_x25519(b_A, server->b, A);
    print_point("b * A", b_A);
    // DH shared secret from b * V
    uint8_t b_V[32];
    crypto_x25519(b_V, server->b, V);
    print_point("b * V", b_V);

    // Hash everything we've learned so far to generate k, save it in ctx->k
    debug(LOG_DEBUG, "Hashing state so far to generate K_s");
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx,
                              server->client_id,
                              server->client_id_len);
        crypto_blake2b_update(&hash_ctx, &null_byte, 1);    // Insert a NULL after the client id
        crypto_blake2b_update(&hash_ctx,
                              server->server_id,
                              server->server_id_len);
        crypto_blake2b_update(&hash_ctx, &null_byte, 1);    // Insert a NULL after the server id
        crypto_blake2b_update(&hash_ctx, A, 32);
        crypto_blake2b_update(&hash_ctx, server->B, 32);
        crypto_blake2b_update(&hash_ctx, b_A, 32);
        crypto_blake2b_update(&hash_ctx, b_V, 32);
        crypto_blake2b_final(&hash_ctx, server->K_s);
    }
    print_point("K_s", server->K_s);
}

int
bsspeke_server_verify_client
    (
        const uint8_t client_verifier[32],
        bsspeke_server_ctx *server
    )
{

    // Check that the client's hash is correct
    // Compute H( k || VERIFY_CLIENT_MODIFIER )
    debug(LOG_DEBUG, "Checking client's verifier hash");
    uint8_t my_client_verifier[32];
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx, server->K_s, 32);
        crypto_blake2b_update(&hash_ctx,
                              (uint8_t *)BSSPEKE_VERIFY_CLIENT_MODIFIER,
                              BSSPEKE_VERIFY_CLIENT_MODIFIER_LEN);
        crypto_blake2b_final(&hash_ctx, my_client_verifier);
    }
    print_point("client's", client_verifier);
    print_point("mine", my_client_verifier);

    // Compare vs client_verifier
    if( crypto_verify32(client_verifier, my_client_verifier) != 0 ) {
        debug(LOG_ERROR, "Client's verifier doesn't match!");
        return -1;
    }
    debug(LOG_DEBUG, "Client's verifier checks out");

    return 0;
}

void
bsspeke_server_generate_verifier
    (
        uint8_t server_verifier[32],
        bsspeke_server_ctx *server
    )
{
    // Compute our own verifier H( k || VERIFY_SERVER_MODIFIER ), save it in server_verifier
    debug(LOG_DEBUG, "Computing server verifier hash");
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx, server->K_s, 32);
        crypto_blake2b_update(&hash_ctx,
                              (uint8_t *)BSSPEKE_VERIFY_SERVER_MODIFIER,
                              BSSPEKE_VERIFY_SERVER_MODIFIER_LEN);
        crypto_blake2b_final(&hash_ctx, server_verifier);
    }
    print_point("server_v", server_verifier);
}

int
bsspeke_client_verify_server
    (
        const uint8_t server_verifier[32],
        const bsspeke_client_ctx *client
    )
{
    // Compute our own version of the server's verifier hash
    debug(LOG_DEBUG, "Verifying hash from the server");
    uint8_t my_server_verifier[32];
    {
        crypto_blake2b_ctx hash_ctx;
        crypto_blake2b_general_init(&hash_ctx, 32, NULL, 0);
        crypto_blake2b_update(&hash_ctx, client->K_c, 32);
        crypto_blake2b_update(&hash_ctx,
                              (uint8_t *)BSSPEKE_VERIFY_SERVER_MODIFIER,
                              BSSPEKE_VERIFY_SERVER_MODIFIER_LEN);
        crypto_blake2b_final(&hash_ctx, my_server_verifier);
    }
    print_point("mine", my_server_verifier);
    print_point("server's", server_verifier);

    // If the hashes don't match, return failure
    if( crypto_verify32(server_verifier, my_server_verifier) != 0 ) {
        debug(LOG_WARN, "Server's hash doesn't match.  Aborting.");
        return -1;
    }
    debug(LOG_DEBUG, "Server's hash checks out.  SUCCESS!");

    // Otherwise, return success
    return 0;
}

