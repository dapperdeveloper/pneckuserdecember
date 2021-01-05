package com.callpneck.taxi.map

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.maps.DirectionsApiRequest
import com.google.maps.model.TravelMode
import com.google.maps.model.LatLng
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object GoogleMap
{


    private const val TAG = "Simulator"
    lateinit var geoApiContext: GeoApiContext
    private var nearbyCabLocations = arrayListOf<LatLng>()
    private var pickUpPath = arrayListOf<LatLng>()
    private var tripPath = arrayListOf<LatLng>()
    private val mainThread = Handler(Looper.getMainLooper())



    @JvmStatic
    fun requestCab( webSocketListener: WebSocketListener,startlattitude :Double, startlongitude : Double, destilattitude :Double, destlongitude : Double ) {

        val bookedCabCurrentLocation = LatLng(startlattitude, startlongitude)
        val dbookedCabCurrentLocations = LatLng(destilattitude, destlongitude)
        // val bookedCabCurrentLocation = LatLng(latFakeNearby, lngFakeNearby)
        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(bookedCabCurrentLocation)
        //      directionsApiRequest.destination(this.pickUpLocation)
        directionsApiRequest.destination(dbookedCabCurrentLocations)
        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
              //  val jsonObjectCabBooked = JSONObject()
              //  jsonObjectCabBooked.put("type", "cabBooked")
                /*mainThread.post {
                    webSocketListener.onMessage(jsonObjectCabBooked.toString())
                }*/
                pickUpPath.clear()

                val routeList = result.routes

                // Actually it will have zero or 1 route as we haven't asked Google API for multiple paths
                if (routeList.isEmpty()) {
                    val jsonObjectFailure = JSONObject()
                    jsonObjectFailure.put("type", "routesNotAvailable")
                    mainThread.post {
                        webSocketListener.onError(jsonObjectFailure.toString())
                    }
                } else {
                    for (route in routeList) {

                        val path = route.overviewPolyline.decodePath()
                        pickUpPath.addAll(path)
                    }
                    val jsonObject = JSONObject()
                    jsonObject.put("type", "pickUpPath")
                    val jsonArray = JSONArray()
                    for (pickUp in pickUpPath) {
                        val jsonObjectLatLng = JSONObject()
                        jsonObjectLatLng.put("lat", pickUp.lat)
                        jsonObjectLatLng.put("lng", pickUp.lng)
                        jsonArray.put(jsonObjectLatLng)
                    }
                    jsonObject.put("path", jsonArray)
                    mainThread.post {
                        webSocketListener.onMessage(jsonObject.toString())
                    }
                    //mk    startTimerForPickUp(webSocketListener)
                }
            }
            override fun onFailure(e: Throwable) {
                Log.d(TAG, "onFailure : ${e.message}")
                val jsonObjectFailure = JSONObject()
                jsonObjectFailure.put("type", "directionApiFailed")
                jsonObjectFailure.put("error", e.message)
                mainThread.post {
                    webSocketListener.onError(jsonObjectFailure.toString())
                }
            }
        })
    }

    @JvmStatic
    fun requesttimedistance( webSocketListener: WebSocketListener,startlattitude :Double, startlongitude : Double, destilattitude :Double, destlongitude : Double,position:Int ) {

        val bookedCabCurrentLocation = LatLng(startlattitude, startlongitude)
        val dbookedCabCurrentLocations = LatLng(destilattitude, destlongitude)


        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(bookedCabCurrentLocation)
        directionsApiRequest.destination(dbookedCabCurrentLocations)
        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {

                pickUpPath.clear()

                val routeList = result.routes

                // Actually it will have zero or 1 route as we haven't asked Google API for multiple paths
                if (routeList.isEmpty()) {
                    val jsonObjectFailure = JSONObject()
                    jsonObjectFailure.put("type", "routesNotAvailable")
                    mainThread.post {
                        webSocketListener.onError(jsonObjectFailure.toString())
                    }
                } else {
                    val jsonObject = JSONObject()
                    var time =""
                    var distance =""
                    for (route in routeList) {
                         time = route.legs.get(0).duration.toString()
                        distance = route.legs.get(0).distance.toString()

                        val path = route.overviewPolyline.decodePath()
                        pickUpPath.addAll(path)
                    }

                    jsonObject.put("value", position)
                    jsonObject.put("time", time.toString())
                    jsonObject.put("distance", distance.toString())

                    mainThread.post {
                        webSocketListener.onMessage(jsonObject.toString())
                    }
                }
            }
            override fun onFailure(e: Throwable) {
                Log.d(TAG, "onFailure : ${e.message}")
                val jsonObjectFailure = JSONObject()
                jsonObjectFailure.put("type", "directionApiFailed")
                jsonObjectFailure.put("error", e.message)
                mainThread.post {
                    webSocketListener.onError(jsonObjectFailure.toString())
                }
            }
        })
    }


}