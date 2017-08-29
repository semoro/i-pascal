package me.semoro.pascal.tpu.decompiler

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.LinkedHashMap


//val dump_types = (0..255).toSet() - known_types
//var indentation: Short = 2
//var byte_array: ByteArray? = null
//var last_kind: Byte? = null


fun main(args: Array<String>) {

    val tpuFile = File("/Users/jetbrains/Downloads/GRAPH.TPU")
    val array = tpuFile.readBytes()
    val session = DecompilationSession(array).apply { decompile() }
    println(session.sb)
}

class DecompilationSession(private val buffer: ByteBuffer) {

    constructor(byteArray: ByteArray) : this(ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN))

    val sb = StringBuilder()

    lateinit var header: Header
    lateinit var thisUnit: Element

    fun decompile() {
        header = readBinaryDataClass(buffer)

        thisUnit = readBinaryDataClass(buffer, header.ofs_this_unit)

        sb.appendln("Unit ${thisUnit.name};")
        sb.appendln("Interface")

        val hashTable = readBinaryDataClass<Hash>(buffer, header.ofs_hashtable)

        val symbolList = buildList(buffer, hashTable).toList()

        var lastElement: Element? = null


        for ((offset, element) in symbolList) {
            if (element.elementType != lastElement?.elementType) {
                onKindChange?.invoke()
                onKindChange = null
            }
            readElementInfo(buffer, offset, element, lastElement)
            lastElement = element
        }

    }


    private fun readConstInfo(buffer: ByteBuffer, element: Element): ConstInfo {
        val info = readBinaryDataClass<ConstInfo>(buffer)
        return info
    }

    private var onKindChange: (() -> Unit)? = null

    private fun readElementInfo(buffer: ByteBuffer, offset: Short, element: Element, lastElement: Element?) {
        val offsetAfterHeader = offset + 3 + element.name.length
        buffer.position(offsetAfterHeader)
        when (element.elementType) {
            ElementType.UNIT -> {
                if (element != thisUnit) {
                    if (lastElement?.elementType != ElementType.UNIT || lastElement == thisUnit) {
                        sb.append(" Uses ")
                    } else {
                        sb.append(", ")
                    }
                    sb.append(element.name)
                    onKindChange = {
                        sb.appendln(";")
                    }
                }
            }
            ElementType.CONST -> {
                val constInfo = readConstInfo(buffer, element)
                if (lastElement?.elementType != ElementType.CONST)
                    sb.appendln(" Const")
                sb.appendln("  ${element.name} = ${constInfo.v};")
            }
            else -> {
            }
        }
    }


}


private fun buildList(buffer: ByteBuffer, hashTable: Hash): Map<Short, Element> {
    val map = LinkedHashMap<Short, Element>()

    fun readObj(offset: Short) {
        val rec = readBinaryDataClass<Element>(buffer, offset)
        map[offset] = rec
        if (rec.nextElementAddress.toInt() != 0)
            readObj(rec.nextElementAddress)
    }


    (0..hashTable.table.lastIndex)
            .map { hashTable.table[it] }
            .filter { it.toInt() != 0 }
            .forEach { readObj(it) }

    return map.toSortedMap()
}

val Short.hex: String
    get () = java.lang.Short.toUnsignedInt(this).toString(16)

class Header(@Len(4) var file_id: String,
             var i4: Short,
             var i6: Short,
             var ofs_this_unit: Short,
             var ofs_hashtable: Short,
             var ofs_entry_pts: Short,
             var ofs_code_blocks: Short,
             var ofs_const_blocks: Short,
             var ofs_var_blocks: Short,
             var ofs_dll_list: Short,
             var ofs_unit_list: Short,
             var ofs_src_name: Short,
             var ofs_line_count: Short,
             var ofs_line_lengths: Short,
             var sym_size: Short,
             var browser_size: Short,
             var code_size: Short,
             var const_size: Short,
             var reloc_size: Short,
             var const_reloc_size: Short,
             var var_size: Short,
             var ofs_full_hash: Short,
             var flags: Short,
             var oblect_type_list: Short,
             var br_defs_end: Short,
             var br_symbol_refxx1: Short,
             @Len(18) var other: ByteArray

) {
    override fun toString(): String {
        return "Header(file_id='$file_id', i4=${i4.hex}, i6=${i6.hex}, ofs_this_unit=${ofs_this_unit.hex}, ofs_hashtable=${ofs_hashtable.hex}, ofs_entry_pts=${ofs_entry_pts.hex}, ofs_code_blocks=${ofs_code_blocks.hex}, ofs_const_blocks=${ofs_const_blocks.hex}, ofs_var_blocks=${ofs_var_blocks.hex}, ofs_dll_list=${ofs_dll_list.hex}, ofs_unit_list=${ofs_unit_list.hex}, ofs_src_name=${ofs_src_name.hex}, ofs_line_count=${ofs_line_count.hex}, ofs_line_lengths=${ofs_line_lengths.hex}, sym_size=${sym_size.hex}, browser_size=${browser_size.hex}, code_size=${code_size.hex}, const_size=${const_size.hex}, reloc_size=${reloc_size.hex}, const_reloc_size=${const_reloc_size.hex}, var_size=${var_size.hex}, ofs_full_hash=${ofs_full_hash.hex}, flags=${flags.hex}, oblect_type_list=${oblect_type_list.hex}, br_defs_end=${br_defs_end.hex}, br_symbol_refxx1=${br_symbol_refxx1.hex}, other=${Arrays.toString(other)})"
    }
}
//
//data class type_def_rec(
//        var type_type: Byte,
//        var other_byte: Byte,
//        var size: Short,
//        var base_type: Short,
//        var type_int: Short
//)
//
//data class type_def_rec_1(
//        var element_ofs: Short,
//        var element_unit: Short,
//        var index_ofs: Short,
//        var index_unit: Short
//)
//
//data class type_def_rec_2(
//        var hash_ofs: Short,
//        var first_ofs: Short,
//        var parent_ofs: Short,
//        var parent_unit: Short,
//        var vmt_size: Short,
//        var handle: Short,
//        var w10: Short,
//        var self_type_ofs: Short,
//        var previous_object_def: Short
//)
//
//data class type_def_rec_7(
//        var base_ofs: Short,
//        var base_unit: Short
//)
//
//data class type_def_rec_6(
//        var return_ofs: Short,
//        var return_unit: Short,
//        var num_args: Short
//)
//
//data class type_def_rec_8(
//        var target_ofs: Short,
//        var target_unit: Short
//)
//
//data class type_def_rec_15(
//        var lower: Long,
//        var upper: Long,
//        var type_ofs: Short,
//        var type_unit: Short
//)
//
//data class type_def_rec_minus_1(
//        var who_knows: ShortArray
//)
//
//data class List_rec(
//        var offset: Short,
//        var hash: Short
//)


data class Element(
        var nextElementAddress: Short,
        var elementType: ElementType?, // Byte
        @PrefixLen var name: String // Byte + Len
) {
    override fun toString() = "Element(type = $elementType, name = $name)"
}

data class Pointer(val value: Int)
data class TPReal(val data: ByteArray)

data class ConstInfo(
        var typeDefOffset: Short,
        var typeUnit: Short,
        var v: Any
) {

    companion object : CustomReader<ConstInfo> {
        override fun read(buffer: ByteBuffer): ConstInfo {
            val typeDefOffset = buffer.short
            val typeUnit = buffer.short
            val type = buffer.get().toInt()
            return ConstInfo(
                    typeDefOffset,
                    typeUnit,
                    when (type) {
                        -1 -> {
                            buffer.get()
                        }
                        0 -> {
                            buffer.int
                        }
                        1 -> {
                            //error("Real not supported")
                            TPReal(ByteArray(6).also { buffer.get(it) })
                        }
                        2 -> {
                            val len = buffer.get().toInt()
                            String(ByteArray(len).also { buffer.get(it) })
                        }
                        3 -> {
                            error("Extended not supported")
                        }
                        4 -> {
                            0x0.toByte() == buffer.get()
                        }
                        5 -> {
                            buffer.get().toChar()
                        }
                        6 -> {
                            Pointer(buffer.int)
                        }
                        7 -> {
                            error("Byte set not supported")
                        }
                        else -> {
                        }
                    }
            )

        }
    }
}


data class Hash(
        var len: Short,
        var table: ShortArray
) {
    companion object : CustomReader<Hash> {
        override fun read(buffer: ByteBuffer): Hash {
            val len = buffer.short
            val table = ShortArray(len / 2) { buffer.short }
            return Hash(len, table)
        }
    }
}

val record_id = 2
val object_id = 3
val objpriv_id = 4
val const_id = 0x4f
val type_id = 0x50
val var_id = 0x51
val proc_id = 0x52
val unit_id = 0x53
val sys_proc_id = 0x56
val sys_fn_id = 0x57
val sys_new_id = 0x58
val sys_port_id = 0x59
val sys_mem_id = 0x5a
val sys_openstr_id = 0x5b
val init_id = 128
val uses_id = 129
val local_id = 130
val referenced_id = 131
val refconst_id = 132


val known_types = setOf(var_id, unit_id, const_id, type_id, proc_id,
        sys_proc_id, sys_fn_id, sys_mem_id, sys_port_id,
        sys_new_id, sys_openstr_id)

enum class ElementType(val id: Byte) {
    CONST(0x4f),
    TYPE(0x50),
    VAR(0x51),
    PROC(0x52),
    UNIT(0x53),
    SYS_PROC(0x56),
    SYS_FN(0x57),
    SYS_NEW(0x58),
    SYS_PORT(0x5a),
    SYS_MEM(0x5b),
    SYS_OPENSTR(0x5b);

    companion object : CustomReader<ElementType?> {
        private val reverseMap = ElementType.values().map { it.id to it }.toMap()

        override fun read(buffer: ByteBuffer): ElementType? {
            val id = buffer.get()
            return reverseMap[id]
        }
    }
}
/*
fun add_only_offset(p: Int, add: Short): Int {
    add_only_offset = seg(p), ofs(p)+add
}

fun write_type_def(def: type_def_rec) {
    var i: Integer
    var l: Long
    var save_kind: Byte
    var field_list: List_rec
    var current: List_rec
    var obj: Element
    var type_obj: type_def_rec
    var no_name: String
    var save_in_array: Boolean
    var bt: Byte
    with(def) {
        if (base_type.toInt() in setOf(1, 2, 4, 6, 8, 0xa, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x15, 0x18, 0x19, 0x1a, 0x1b,
                0x21, 0x22, 0x23)) {
            var bt = 255
            when (base_type.toInt()) {
                1 -> {
                    println("untyped")
                    bt = 0
                }
                2 -> {
                    println("shorting")
                    bt = 12
                }
                4 -> {
                    println("integer")
                    bt = 12
                }
                6 -> {
                    println("longint")
                    bt = 12
                }
                8 -> {
                    println("byte")
                    bt = 12
                }
                0xa -> {
                    println("word")
                    bt = 12
                }
                0xe -> {
                    println("single")
                    bt = 10
                }
                0xf -> {
                    println("double")
                    bt = 10
                }
                0x10 -> {
                    println("extended")
                    bt = 10
                }
                0x11 -> {
                    println("real")
                    bt = 11
                }
                0x12 -> {
                    println("boolean")
                    bt = 13
                }
                0x13 -> {
                    println("char")
                    bt = 14
                }
                0x15 -> {
                    println("comp")
                    bt = 10
                }
                0x18 -> {
                    println("text")
                    bt = 5
                }
                0x19 -> {
                    println("file")
                    bt = 4
                }
                0x1a -> {
                    println("pointer")
                    bt = 8
                }
                0x1b -> {
                    println("string")
                    bt = 10
                }
                0x21 -> {
                    println("wordbool")
                    bt = 13
                }
                0x22 -> {
                    println("longbool")
                    bt = 13
                }
                0x23 -> {
                    println("pchar")
                    bt = 8
                }
            }
            if (type_type.toInt() != bt) {
                println("{base type <-> type_type error}")
            }
        } else {
            if (base_type.toInt() != 0) {
                println("{ unrecognized base type '+hexbyte(base_type)+'}")
            }
            when (type_type.toInt()) {
                0 -> println("untyped")
                1 -> {
                    println("array[" + def.index_unit + def.index_ofs)
                    println("] of" + def.element_unit + def.element_ofs)
                }
                2 -> {
                    save_kind = last_kind
                    last_kind = record_id
                    println("Record ")
                    build_list(field_list, byte_array, add_only_offset(byte_array, hash_ofs));
                    current = field_list
                    ++indentation
                    ++indentation
                    while (current.offset < 0xffff) {
                        obj = add_only_offset(buffer, current.offset)
                    }
                }
            }
        }
    }
}
*/
/*
data class tpl_item_rec(//buffer : byte_array_ptr;
        var size: Int
        //next : tpl_item_ptr;
)

fun LoadTpl() {
    var total: Long
    var header: Header
    var i: Integer
}


fun InsertToList(offset: Long, size: Int) {
    var Aux: tpl_item_rec
    var path = tpl_name
    var first = null
    var total = 0
}

fun ReadPathFile(path: String, Header: Header/*replace header_ptr to Header*/) {
    var dir: String
    var unit_dirs: String
    var i: Integer
}

*/