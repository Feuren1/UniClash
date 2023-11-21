package project.main.uniclash.JWT

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import project.main.uniclash.viewmodels.UserViewModel
import java.io.IOException
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class TokenManager(private val context: Context) {
    //private val TAG = UserViewModel::class.java.simpleName
    private val keyAlias = "jwt_key_alias"
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun saveToken(token: String) {
        try {
            val encryptedToken = encryptToken(token)
            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_shared_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val editor = sharedPreferences.edit()
            editor.putString("jwt_token", encryptedToken)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getToken(): String? {
        try {
            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_shared_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val encryptedToken = sharedPreferences.getString("jwt_token", null)
            return if (encryptedToken != null) decryptToken(encryptedToken) else null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun encryptToken(token: String): String {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(token.toByteArray())
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }

    private fun decryptToken(encryptedToken: String): String {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val encryptedBytes = android.util.Base64.decode(encryptedToken, android.util.Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias(keyAlias)) {
            Log.d("TokenManager", "Generating new key")
            generateKey()
        }
        return keyStore.getKey(keyAlias, null) as SecretKey
    }

    private fun generateKey() {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
        Log.d("TokenManager", "Key generated successfully")
    }
}
