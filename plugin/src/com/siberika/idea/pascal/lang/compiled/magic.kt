package me.semoro.pascal.tpu.decompiler

import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.jvm.isAccessible

/**
 * Created by olya on 19.05.17.
 */


annotation class Len(val len: Int)

annotation class PrefixLen

interface CustomReader<T> {
    fun read(buffer: ByteBuffer): T
}


fun <T : Any> readCustomDataClass(kclass: KClass<T>, byteBuffer: ByteBuffer): T? {
    val reader = kclass.companionObjectInstance as? CustomReader<T>
    return reader?.read(byteBuffer)
}

inline fun <reified T : Any> readBinaryDataClass(byteBuffer: ByteBuffer, offset: Short): T
        = readBinaryDataClass(byteBuffer, offset.toInt())

inline fun <reified T : Any> readBinaryDataClass(byteBuffer: ByteBuffer, offset: Int): T {
    byteBuffer.position(offset)
    return readBinaryDataClass(byteBuffer)
}

inline fun <reified T : Any> readBinaryDataClass(byteBuffer: ByteBuffer): T {
    readCustomDataClass(T::class, byteBuffer)?.let { return it }
    val constructor = T::class.constructors.first()
    val arguments: List<Any?> = constructor.parameters.map {
        val classifier = it.type.classifier
        when (classifier) {
            Byte::class -> byteBuffer.get()
            Short::class -> byteBuffer.short
            Int::class -> byteBuffer.int
            ByteArray::class -> {
                val len = it.annotations.filterIsInstance<Len>().single().len
                val array = ByteArray(len)
                byteBuffer.get(array)
                array
            }
            String::class -> {
                val annLen = it.annotations.filterIsInstance<Len>().singleOrNull()
                val annPrefixLen = it.annotations.filterIsInstance<PrefixLen>().singleOrNull()
                val array = when {
                    annLen != null -> {
                        val array = ByteArray(annLen.len)
                        byteBuffer.get(array)
                        array
                    }
                    annPrefixLen != null -> {
                        val length = byteBuffer.get().toInt()
                        val array = ByteArray(length)
                        byteBuffer.get(array)
                        array
                    }
                    else -> generateSequence { byteBuffer.get() }
                            .takeWhile { it.toInt() != 0 }
                            .toList().toByteArray()
                }
                String(array)
            }
            else -> {
                val kclass = classifier as KClass<*>
                readCustomDataClass(kclass, byteBuffer)
            }
        }
    }

    constructor.isAccessible = true
    return constructor.call(*arguments.toTypedArray())
}