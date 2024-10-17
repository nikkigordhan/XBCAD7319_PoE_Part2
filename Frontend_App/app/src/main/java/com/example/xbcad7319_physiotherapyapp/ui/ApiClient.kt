package com.example.xbcad7319_physiotherapyapp.ui

import android.content.Context
import android.util.Log
import com.example.xbcad7319_physiotherapyapp.R

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object ApiClient {
    private const val BASE_URL = "https://10.0.0.113:22"
    private var retrofit: Retrofit? = null

    fun getRetrofitInstance(context: Context): Retrofit {
        if (retrofit == null) {
            val client = createOkHttpClient(context)
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return try {
            // Load the PEM certificate
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val caInput = context.resources.openRawResource(R.raw.cert) // Change to the name of your cert.pem
            val ca = certificateFactory.generateCertificate(caInput)

            // Create a KeyStore containing our trusted CAs
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null) // Initialize a new empty KeyStore
                setCertificateEntry("ca", ca) // Add the CA certificate
            }

            // Create a TrustManager that trusts the CAs in our KeyStore
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }

            // Create an SSLContext that uses our TrustManager
            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            // Enable logging for better debugging (optional)
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
                .addInterceptor(loggingInterceptor) // Log requests and responses
                .build()
        } catch (e: Exception) {
            Log.e("OkHttpClient", "Error creating OkHttpClient: ${e.message}", e)
            throw e // Rethrow or handle the exception as needed
        }
    }
}




