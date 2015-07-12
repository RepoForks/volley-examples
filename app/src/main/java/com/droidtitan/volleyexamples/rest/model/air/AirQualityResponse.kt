package com.droidtitan.volleyexamples.rest.model.air

import com.google.gson.annotations.SerializedName

public class AirQualityResponse {

    SerializedName("consulta")
    var airQualityHolder: AirQualityHolder? = null

    public fun getAirQualityCategory(): String? {
        return airQualityHolder?.airQuality?.category
    }

    public fun getTemperature(): String? {
        return airQualityHolder?.weather?.temperature
    }

}
