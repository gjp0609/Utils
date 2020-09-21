package com.onysakura.utils.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

class MoshiUtils {

    companion object {
        private val moshi: Moshi = Moshi
                .Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        //普通序列化
        fun <T> fromJson(json: String, type: Type): T? = getAdapter<T>(type).fromJson(json)

        //高效序列化为list
        inline fun <reified T> listFromJson(json: String): MutableList<T>? =
                fromJson(json, Types.newParameterizedType(MutableList::class.java, T::class.java))

        //高效序列化为map
        inline fun <reified K, reified V> mapFromJson(json: String): MutableMap<K, V>? =
                fromJson(json, Types.newParameterizedType(MutableMap::class.java, K::class.java, V::class.java))

        fun toJson(any: Any): String = moshi.adapter(Any::class.java).toJson(any)

        private fun <T> getAdapter(type: Type): JsonAdapter<T> = moshi.adapter(type)

    }
}