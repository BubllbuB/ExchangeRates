package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.enums.IngotRes
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.text.SimpleDateFormat
import java.util.*

class APIIngotModel {
    fun getIngotsList(): Observable<ArrayList<Ingot>> {

        val jSONApi = APIService.instance.getJSONApi()

        val metalApi = jSONApi.getMetals()
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val ingotApi = jSONApi.getIngots(dateString)

        return metalApi.zipWith(ingotApi, BiFunction { metal, ingot ->
            metal.removeAt(metal.size-1)
            metal.forEach {
                val ingotOneNominal = ingot.find { ingot -> ingot.metalIdApi==it.ingotId && ingot.nominal==10 }
                ingotOneNominal?.let { nominalIngot->
                    it.date = nominalIngot.date
                    it.ingotCertificateRubles = nominalIngot.ingotCertificateRubles
                    it.ingotEntitiesRubles = nominalIngot.ingotEntitiesRubles
                    it.symbol = IngotRes.valueOf(it.ingotNameEng).getSymbolRes()
                    it.nominal = nominalIngot.nominal
                }

            }
            metal
        })
    }
}