package com.avinash.whatsthetime.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result

class LocationClient(
    private val httpClient: HttpClient
) {
    suspend fun getUserLocation(location: String):Result<Pair<String?,String?>, NetworkError> {
        val response = try {
            httpClient.get(
                urlString = "http://ip-api.com/json"
            ){
                parameter("IP_Address", location)
            }

        }catch (e:UnresolvedAddressException){
            return Result.Error(NetworkError.NO_INTERNET)
        }catch (e:SerializationException){
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value){
            in 200..299 -> {
                val location = response.body<IpApiResponse>()
                Result.Success(location.city to location.timezone)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            404 -> Result.Error(NetworkError.UNKNOWN)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)

            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}