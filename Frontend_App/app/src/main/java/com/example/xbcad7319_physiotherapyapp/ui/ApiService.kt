package com.example.xbcad7319_physiotherapyapp.ui

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    fun registerPatient(@Body user: User): Call<ResponseBody>

    @POST("api/auth/login")
    fun loginPatient(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST("api/auth/login")
    fun loginStaff(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST("api/auth/forget-password")
    fun updatePassword(@Body request: PasswordUpdateRequest): Call<ResponseBody>

    @POST("/api/auth/register")
   fun registerUser(@Body user: User): Call<ResponseBody>

    @POST("/api/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<ResponseBody>


    @POST("api/appointments/")
    fun bookAppointment(
        @Header("Authorization") token: String, // Add the authorization header
        @Body appointmentRequest: BookAppointmentRequest
    ): Call<ResponseBody>

    @PUT("api/appointments/{appointmentId}")
    fun rescheduleAppointment(
        @Path("appointmentId") appointmentId: String,
        @Body rescheduleRequest: RescheduleAppointmentRequest
    ): Call<RescheduleAppointmentResponse>

    @DELETE("api/appointments/{appointmentId}")
    fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("appointmentId") appointmentId: String
    ): Call<ResponseBody>

    @GET("appointments/confirmed")
    fun getConfirmedAppointmentsForPatient(): Call<List<AppointmentDetails>>
}


// Request model for booking an appointment
data class BookAppointmentRequest(
    val patient: String,
    val date: String,
    val time: String,
    val description: String
)

data class RescheduleAppointmentRequest(
    val date: Long,  // Timestamp of the selected date
    val time: String,  // Selected time
    val description: String  // Appointment description
)

data class RescheduleAppointmentResponse(
    val message: String,
    val appointment: AppointmentDetails
)


data class AppointmentDetails(
    val id: String,
    val patientName: String,
    val patientEmail: String,
    val date: String,
    val time: String,
    val description: String?,
    val notes: String?,
    val status: String
)

data class User(
    var username: String,
    var password: String,
    var role: String,
    var name: String,
    var surname: String,
    var email: String,
    var phoneNumber: String,
    var medicalAid: String? = null,
    var medicalAidNumber: String? = null
)

data class LoginRequest(
    var username: String,
    var password: String
)

data class PasswordUpdateRequest(
    val username: String,
    val email: String,
    val newPassword: String
)
