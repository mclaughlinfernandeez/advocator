package com.example.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is empty or placeholder! Please configure GEMINI_API_KEY in secrets.")
            return@withContext "API_KEY_MISSING"
        }

        try {
            val jsonRequest = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()

            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            jsonRequest.put("contents", contentsArray)

            // Dynamic configurations
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7f)
            jsonRequest.put("generationConfig", generationConfig)

            // Add system instruction if provided
            if (systemInstruction != null) {
                val sysInstObj = JSONObject()
                val sysInstParts = JSONArray()
                val sysInstPart = JSONObject()
                sysInstPart.put("text", systemInstruction)
                sysInstParts.put(sysInstPart)
                sysInstObj.put("parts", sysInstParts)
                jsonRequest.put("systemInstruction", sysInstObj)
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API Request failed: Code ${response.code}, Message: $errBody")
                    return@withContext "Error calling Gemini API: HTTP ${response.code}\n$errBody"
                }

                val responseBodyStr = response.body?.string() ?: return@withContext "Empty response from Gemini API"
                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No text in response")
                        }
                    }
                }
                return@withContext "No response candidates generated"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini details API Call", e)
            return@withContext "Connection error: ${e.localizedMessage}"
        }
    }
}
