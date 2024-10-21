package com.example.xbcad7319_physiotherapyapp.ui

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.Date

interface ApiService {
    @POST("api/auth/register")
    fun registerPatient(@Body user: User): Call<ResponseBody>

    @POST("api/auth/login")
    fun loginPatient(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST("api/auth/login")
    fun loginStaff(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST("api/auth/forget-password")
    fun updatePassword(@Body request: PasswordUpdateRequest): Call<ResponseBody>

    @POST("api/form2/createForm2")
    fun submitForm2Data(
        @Body form2Request: Form2Request
    ): Call<ResponseBody>

    @POST("api/form2/createForm1")
    fun submitForm1Data(
        @Body form1Request: Form1Request
    ): Call<ResponseBody>

    @POST("api/appointments/")
    fun bookAppointment(
        @Header("Authorization") token: String, // Add the authorization header
        @Body appointmentRequest: BookAppointmentRequest
    ): Call<ResponseBody>

    @PUT("ap/appointments/{appointmentId}/reschedule")
    fun rescheduleAppointment(
        @Path("appointmentId") appointmentId: String,
        @Body rescheduleRequest: RescheduleAppointmentRequest
    ): Call<RescheduleAppointmentResponse>

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
    val email: String? = null,
    val newPassword: String
)

data class Form2Request(
    val name: String,
    val areasConcernedForNeedling: String,
    val date: Date,
    val signature: String
)

data class Form2(
    val name: String,
    val areasConsented: String,
    val date: Date,
    val signature: String
)


data class Form1Request(
    val firstNameP: String,
    val surnameP: String,
    val titleP: String?,
    val idP: String,
    val ageP: Int,
    val addressP: String,
    val codeP: String,
    val cellNumberP: String,
    val workNumberP: String?,
    val homeNumberP: String?,
    val emailP: String,
    val medicalAidNameP: String? = null,
    val medicalAidNumberP: String? = null,
    val firstNameR: String? = null,
    val surnameR: String? = null,
    val titleR: String? = null,
    val idR: String? = null,
    val ageR: String? = null,
    val addressR: String? = null,
    val codeR: String? = null,
    val cellNumberR: String? = null,
    val workNumberR: String? = null,
    val homeNumberR: String? = null,
    val emailR: String? = null,
    val firstNameK: String,
    val addressK: String,
    val codeK: String,
    val nameS: String,
    val typeS: String,
    val signature: String?, // Assuming signature is a String, change if it's different
    val placeS: String,
    val date: Date // The formatted date from the CalendarView
)

