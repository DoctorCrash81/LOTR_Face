package com.wizl.beautyscanner.model

import android.graphics.Point
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
{
    "meta":{
        "face_rect":{
            "x":858,
            "y":1746,
            "w":1331,
            "h":1331
        },
        "face_landmarks":[[943,2053],[947,2237],[970,2416],[1007,2587],[1063,2743],[1149,2887],[1256,3009],[1393,3092],[1541,3117],[1704,3103],[1845,3023],[1947,2900],[2024,2756],[2081,2599],[2116,2434],[2151,2262],[2171,2087],[1086,1953],[1156,1879],[1264,1869],[1373,1897],[1479,1939],[1720,1940],[1824,1892],[1931,1864],[2038,1875],[2105,1945],[1588,2064],[1584,2183],[1583,2298],[1583,2417],[1446,2483],[1507,2508],[1576,2525],[1640,2506],[1701,2481],[1178,2075],[1249,2029],[1340,2028],[1411,2086],[1334,2107],[1244,2110],[1756,2079],[1826,2018],[1913,2016],[1978,2057],[1921,2100],[1835,2102],[1385,2739],[1446,2682],[1519,2654],[1577,2667],[1637,2649],[1718,2670],[1788,2721],[1720,2784],[1641,2807],[1579,2814],[1520,2812],[1447,2788],[1421,2735],[1518,2717],[1575,2719],[1636,2715],[1756,2721],[1641,2722],[1580,2731],[1523,2725]]
    },
    "result":{
        "score":3.1332254530861974
    }
}
*/

class BeautyModelSerializable(
    val meta: MetaBeautyModelSerializable,
    val result: ResultSerializable
) : Serializable {
    fun getPoint(index:Int): Point{
        return Point(meta.faceLandmarks[index][0],meta.faceLandmarks[index][1])
    }
}

class ResultSerializable(val score:Float) : Serializable

class MetaBeautyModelSerializable(
    @SerializedName("face_rect")
    val faceRect: FaceRectSerializable,
    @SerializedName("face_landmarks")
    val faceLandmarks:ArrayList<ArrayList<Int>>
) : Serializable

class FaceRectSerializable(
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int
) : Serializable