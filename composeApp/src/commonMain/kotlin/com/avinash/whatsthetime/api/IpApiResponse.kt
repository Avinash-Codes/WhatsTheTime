package com.avinash.whatsthetime.api

import kotlinx.serialization.Serializable

@Serializable
data class IpApiResponse(
    val query: String? = null,
    val status: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val region: String? = null,
    val regionName: String? = null,
    val city: String? = null,
    val zip: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val timezone: String? = null,
    val isp: String? = null,
    val org: String? = null,
    val `as`: String? = null
)