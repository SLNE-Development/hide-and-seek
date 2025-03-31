package dev.slne.hideandnseek.storage

import com.mojang.datafixers.DSL
import com.mojang.datafixers.DataFixer
import com.mojang.datafixers.DataFixerBuilder
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import dev.slne.hideandnseek.HASSettings
import dev.slne.hideandnseek.util.safeReplaceOrMoveFile
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableByteListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.bytes.ByteArrayList
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.longs.LongArrayList
import net.querz.nbt.io.NBTDeserializer
import net.querz.nbt.io.NBTSerializer
import net.querz.nbt.io.NamedTag
import net.querz.nbt.tag.*
import java.nio.ByteBuffer
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.io.path.*
import kotlin.streams.asStream

data class HASData(val settings: HASSettings) {
    fun createTag() = CompoundTag().apply {
        settings.writeToTag(this)
    }
}

class HASStorageSource(val dataDirectory: Path, val dataFixer: DataFixer) {
    val dataFile: Path = dataDirectory / "data.dat"
    val oldDataFile: Path = dataDirectory / "old_data.dat"

    init {
        dataDirectory.createDirectories()
    }

    fun corruptedDataFile(dateTime: LocalDateTime): Path {
        val dateTimeString = dateTime.format(formatter)
        return dataDirectory / "data.dat_corrupted_$dateTimeString"
    }

    fun hasHASData(): Boolean {
        return dataFile.exists() || oldDataFile.exists()
    }

    fun getDataTag() = getDataTag(false)
    fun getOldDataTag() = getDataTag(true)

    private fun getDataTag(old: Boolean): Dynamic<*> {
        return readDataTagFixed(if (!old) dataFile else oldDataFile, dataFixer)
    }

    fun restoreDataFromOld(): Boolean {
        return safeReplaceOrMoveFile(
            dataFile,
            oldDataFile,
            corruptedDataFile(LocalDateTime.now()),
            true
        )
    }

    fun saveDataTag(data: HASData) {
        val tag = CompoundTag()
        tag.put("Data", data.createTag())
        tag.putInt("DataVersion", DATA_VERSION)

        saveDataTag(tag)
    }

    private fun saveDataTag(nbt: CompoundTag) {
        val path = this.dataDirectory
        val tempFile = createTempFile(path, "level", ".dat")
        try {
            tempFile.outputStream().use { NBTSerializer().toStream(NamedTag("", nbt), it) }
            val backupFile = path / "old_data.dat"
            val currentFile = path / "data.dat"
            safeReplaceOrMoveFile(currentFile, tempFile, backupFile, false)
        } catch (e: Exception) {
            log.atSevere()
                .withCause(e)
                .log("Failed to save HAS data {}", path)
        } finally {
            tempFile.deleteIfExists()
        }
    }


    companion object {
        private val log = logger()
        const val DATA_VERSION = 1

        private val formatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('_')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter()

        private val fixType = object : DSL.TypeReference {
            override fun typeName() = "HASStorage"
            override fun toString() = "@${typeName()}"
        }

        fun getHASData(dynamic: Dynamic<*>): HASData {
            val settings = HASSettings.parse(dynamic)
            return HASData(settings)
        }

        private fun <T> updateToCurrentVersion(
            fixer: DataFixer,
            input: Dynamic<T>,
            version: Int,
            newVersion: Int = DATA_VERSION
        ): Dynamic<T> {
            return fixer.update(fixType, input, version, newVersion)
        }

        private fun readDataTagRaw(path: Path): CompoundTag {
            return path.inputStream()
                .use { NBTDeserializer(true).fromStream(it).tag as CompoundTag }
        }

        private fun readDataTagFixed(path: Path, dataFixer: DataFixer): Dynamic<*> {
            val nbtTag = readDataTagRaw(path)
            val dataTag = nbtTag.getCompoundTag("Data") ?: CompoundTag()
            val version = nbtTag.getNumber("DataVersion")?.toInt() ?: -1
            val dynamic = updateToCurrentVersion(dataFixer, Dynamic(NBTOps, dataTag), version)

            return dynamic
        }
    }
}

object NBTOps : DynamicOps<Tag<*>> {
    override fun empty(): EndTag = EndTag.INSTANCE


    override fun <U : Any> convertTo(
        outOps: DynamicOps<U>,
        input: Tag<*>
    ): U = when (input.id) {
        EndTag.ID -> outOps.empty()
        ByteTag.ID -> outOps.createByte((input as ByteTag).asByte())
        ShortTag.ID -> outOps.createShort((input as ShortTag).asShort())
        IntTag.ID -> outOps.createInt((input as IntTag).asInt())
        LongTag.ID -> outOps.createLong((input as LongTag).asLong())
        FloatTag.ID -> outOps.createFloat((input as FloatTag).asFloat())
        DoubleTag.ID -> outOps.createDouble((input as DoubleTag).asDouble())
        ByteArrayTag.ID -> outOps.createByteList(ByteBuffer.wrap((input as ByteArrayTag).value))
        StringTag.ID -> outOps.createString((input as StringTag).value)
        ListTag.ID -> convertList(outOps, input)
        CompoundTag.ID -> convertMap(outOps, input)
        IntArrayTag.ID -> outOps.createIntList(Arrays.stream((input as IntArrayTag).value))
        LongArrayTag.ID -> outOps.createLongList(Arrays.stream((input as LongArrayTag).value))
        else -> error("Unknown tag type: ${input.id}")
    }


    override fun getNumberValue(input: Tag<*>): DataResult<Number> {
        return if (input is NumberTag) DataResult.success(input.asDouble()) else DataResult.error { "Not a number" }
    }

    override fun createNumeric(i: Number): Tag<*> = DoubleTag(i.toDouble())

    override fun createByte(value: Byte): Tag<*> = ByteTag(value)
    override fun createShort(value: Short): Tag<*> = ShortTag(value)
    override fun createInt(value: Int): Tag<*> = IntTag(value)
    override fun createLong(value: Long): Tag<*> = LongTag(value)
    override fun createFloat(value: Float): Tag<*> = FloatTag(value)
    override fun createDouble(value: Double): Tag<*> = DoubleTag(value)
    override fun createBoolean(value: Boolean): Tag<*> = ByteTag(if (value) 1 else 0)

    override fun getStringValue(input: Tag<*>): DataResult<String> {
        return if (input is StringTag) DataResult.success(input.value) else DataResult.error { "Not a string" }
    }

    override fun createString(value: String): Tag<*> {
        return StringTag(value)
    }

    override fun mergeToList(
        list: Tag<*>,
        value: Tag<*>
    ): DataResult<Tag<*>> {
        return createCollector(list)
            ?.let { DataResult.success(it.accept(value).result) }
            ?: DataResult.error({ "mergeToList called with not a list: $list" }, list)
    }

    override fun mergeToList(list: Tag<*>, values: List<Tag<*>>): DataResult<Tag<*>> {
        return createCollector(list)
            ?.let { DataResult.success(it.acceptAll(values).result) }
            ?: DataResult.error({ "mergeToList called with not a list: $list" }, list)
    }

    override fun mergeToMap(
        map: Tag<*>,
        key: Tag<*>,
        value: Tag<*>
    ): DataResult<Tag<*>> {
        if (map !is CompoundTag && map !is EndTag) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        } else if (key !is StringTag) {
            return DataResult.error({ "key is not a string: $key" }, map)
        } else {
            val compoundTag1 = if (map is CompoundTag) map.clone() else CompoundTag()
            compoundTag1.put(key.valueToString(), value)
            return DataResult.success(compoundTag1)
        }
    }

    override fun mergeToMap(map: Tag<*>, values: MapLike<Tag<*>>): DataResult<Tag<*>> {
        if (map !is CompoundTag && map !is EndTag) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        } else {
            val compoundTag = if (map is CompoundTag) map.clone() else CompoundTag()
            val list = mutableObjectListOf<Tag<*>>()
            values.entries().forEach { pair ->
                val tag = pair.getFirst()
                if (tag !is StringTag) {
                    list.add(tag)
                } else {
                    compoundTag.put(tag.valueToString(), pair.getSecond())
                }
            }
            return if (!list.isEmpty()) DataResult.error(
                { "some keys are not strings: $list" },
                compoundTag
            ) else DataResult.success(compoundTag)
        }
    }

    override fun mergeToMap(tag: Tag<*>, map: Map<Tag<*>, Tag<*>>): DataResult<Tag<*>> {
        if (tag !is CompoundTag && tag !is EndTag) {
            return DataResult.error({ "mergeToMap called with not a map: $tag" }, tag)
        } else {
            val compoundTag = if (tag is CompoundTag) tag.clone() else CompoundTag()
            val list = mutableObjectListOf<Tag<*>>()

            for ((key, value) in map.entries) {
                if (key is StringTag) {
                    compoundTag.put(key.valueToString(), value)
                } else {
                    list.add(key)
                }
            }

            return if (!list.isEmpty()) DataResult.error(
                { "some keys are not strings: $list" },
                compoundTag
            ) else DataResult.success(compoundTag)
        }
    }

    override fun getMapValues(map: Tag<*>): DataResult<Stream<Pair<Tag<*>, Tag<*>>>> {
        return if (map is CompoundTag) DataResult.success(
            map.entrySet().stream().map { (key, value) -> Pair.of(this.createString(key), value) })
        else DataResult.error { "Not a map: $map" }
    }

    override fun getMapEntries(map: Tag<*>): DataResult<Consumer<BiConsumer<Tag<*>, Tag<*>>>> {
        return if (map is CompoundTag) DataResult.success(
            Consumer { biConsumer ->
                for (entry in map.entrySet()) {
                    biConsumer.accept(this.createString(entry.key), entry.value)
                }
            }
        ) else DataResult.error(Supplier { "Not a map: $map" })
    }

    override fun getMap(map: Tag<*>): DataResult<MapLike<Tag<*>>> {
        return if (map is CompoundTag) DataResult.success(object : MapLike<Tag<*>> {
            override fun get(tag: Tag<*>): Tag<*>? {
                return map.get(tag.valueToString())
            }

            override fun get(string: String?): Tag<*>? {
                return map.get(string)
            }

            override fun entries(): Stream<Pair<Tag<*>, Tag<*>>> {
                return map.entrySet().stream().map { (key, value) ->
                    Pair.of(createString(key), value)
                }
            }

            override fun toString(): String {
                return "MapLike[$map]"
            }
        }) else DataResult.error { "Not a map: $map" }
    }

    override fun createMap(data: Stream<Pair<Tag<*>, Tag<*>>>): Tag<*> {
        val compoundTag = CompoundTag()
        data.forEach { compoundTag.put(it.first.valueToString(), it.second) }
        return compoundTag
    }

    private fun tryUnwrap(tag: CompoundTag): Tag<*> {
        if (tag.size() == 1) {
            val tag1 = tag.get("")
            if (tag1 != null) {
                return tag1
            }
        }

        return tag
    }

    override fun getStream(tag: Tag<*>?): DataResult<Stream<Tag<*>>> {
        return when (tag) {
            is ListTag<*> -> {
                if (tag.typeClass == CompoundTag::class.java)
                    DataResult.success(
                        tag.asSequence()
                            .map { tryUnwrap(it as CompoundTag) }
                            .asStream()
                    )
                else DataResult.success(tag.asSequence().asStream())
            }

            is ArrayTag -> DataResult.success(tag.asTagList().stream())

            else -> DataResult.error { "Not a list: $tag" }
        }

    }

    override fun getList(tag: Tag<*>): DataResult<Consumer<Consumer<Tag<*>>>> {
        return when (tag) {
            is ListTag<*> -> {
                if (tag.typeClass == CompoundTag::class.java) {
                    DataResult.success(Consumer { consumer ->
                        tag.forEach { consumer.accept(tryUnwrap(it as CompoundTag)) }
                    })
                } else {
                    DataResult.success(Consumer { tag.forEach(it) })
                }
            }

            is ArrayTag -> DataResult.success(Consumer { tag.asTagList().forEach(it) })
            else -> DataResult.error { "Not a list: $tag" }
        }
    }

    override fun getByteBuffer(input: Tag<*>): DataResult<ByteBuffer> {
        return if (input is ByteArrayTag) DataResult.success(ByteBuffer.wrap(input.value))
        else super.getByteBuffer(input)
    }

    override fun createByteList(input: ByteBuffer): Tag<*> {
        val buffer = input.duplicate().clear()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(0, bytes, 0, bytes.size)
        return ByteArrayTag(bytes)
    }

    override fun getIntStream(input: Tag<*>): DataResult<IntStream> {
        return if (input is IntArrayTag) DataResult.success(IntStream.of(*input.value))
        else super.getIntStream(input)
    }

    override fun createIntList(input: IntStream): Tag<*> {
        return IntArrayTag(input.toArray())
    }

    override fun getLongStream(input: Tag<*>): DataResult<LongStream> {
        return if (input is LongArrayTag) DataResult.success(LongStream.of(*input.value))
        else super.getLongStream(input)
    }

    override fun createLongList(input: LongStream): Tag<*> {
        return LongArrayTag(input.toArray())
    }

    override fun createList(input: Stream<Tag<*>>): Tag<*> {
        return InitialListCollector.acceptAll(input.toList()).result
    }

    override fun remove(
        map: Tag<*>,
        removeKey: String
    ): Tag<*> {
        if (map is CompoundTag) {
            val copy = map.clone()
            copy.remove(removeKey)
            return copy
        } else {
            return map
        }
    }

    override fun toString(): String {
        return "NBTOps"
    }

    override fun mapBuilder(): RecordBuilder<Tag<*>> {
        return NbtRecordBuilder()
    }

    private fun createCollector(tag: Tag<*>): ListCollector? {
        if (tag is EndTag) {
            return InitialListCollector
        } else {
            if (tag is ArrayTag) {
                if (tag.length() == 0) {
                    return InitialListCollector
                }

                if (tag is ByteArrayTag) {
                    return ByteListCollector(tag.value)
                }

                if (tag is IntArrayTag) {
                    return IntListCollector(tag.value)
                }

                if (tag is LongArrayTag) {
                    return LongListCollector(tag.value)
                }
            }

            if (tag is ListTag<*>) {
                return when (tag.typeClass) {
                    EndTag::class.java -> InitialListCollector
                    CompoundTag::class.java -> HeterogenousListCollector(tag.toList())
                    else -> HomogenousListCollector(tag)
                }
            }

            return null
        }
    }

    private class HeterogenousListCollector : ListCollector {
        override val result = ListTag(Tag::class.java)

        constructor()
        constructor(tags: Collection<Tag<*>>) {
            result.addAll(tags)
        }

        constructor(data: IntArrayList) {
            data.forEach { result.add(wrapElement(IntTag(it))) }
        }

        constructor(data: ByteArrayList) {
            data.forEach { result.add(wrapElement(ByteTag(it))) }
        }

        constructor(data: LongArrayList) {
            data.forEach { result.add(wrapElement(LongTag(it))) }
        }

        override fun accept(tag: Tag<*>): ListCollector {
            this.result.add(wrapIfNeeded(tag))
            return this
        }

        private fun wrapIfNeeded(tag: Tag<*>): Tag<*> {
            return if (tag is CompoundTag && !isWrapper(tag)) tag else wrapElement(tag)
        }

        private fun isWrapper(tag: CompoundTag) = tag.size() == 1 && tag.containsKey("")

        private fun wrapElement(tag: Tag<*>): CompoundTag {
            val compoundTag = CompoundTag()
            compoundTag.put("", tag)
            return compoundTag
        }
    }

    private class ByteListCollector : ListCollector {
        private val values = mutableByteListOf()

        override val result: Tag<*>
            get() = ByteArrayTag(values.toByteArray())


        constructor(value: Byte) {
            values.add(value)
        }

        constructor(values: ByteArray) {
            this.values.addElements(0, values)
        }

        override fun accept(tag: Tag<*>): ListCollector {
            if (tag is ByteTag) {
                values.add(tag.asByte())
                return this
            } else {
                return HeterogenousListCollector(values).accept(tag)
            }
        }
    }

    private class IntListCollector : ListCollector {
        private val values = IntArrayList()

        override val result: Tag<*>
            get() = IntArrayTag(values.toIntArray())

        constructor(value: Int) {
            values.add(value)
        }

        constructor(values: IntArray) {
            this.values.addElements(0, values)
        }

        override fun accept(tag: Tag<*>): ListCollector {
            if (tag is IntTag) {
                values.add(tag.asInt())
                return this
            } else {
                return HeterogenousListCollector(values).accept(tag)
            }
        }
    }

    private class LongListCollector : ListCollector {
        private val values = LongArrayList()

        override val result: Tag<*>
            get() = LongArrayTag(values.toLongArray())

        constructor(value: Long) {
            values.add(value)
        }

        constructor(values: LongArray) {
            this.values.addElements(0, values)
        }

        override fun accept(tag: Tag<*>): ListCollector {
            if (tag is LongTag) {
                values.add(tag.asLong())
                return this
            } else {
                return HeterogenousListCollector(values).accept(tag)
            }
        }
    }

    private class HomogenousListCollector : ListCollector {
        override val result = ListTag(Tag::class.java)

        constructor(value: Tag<*>) {
            result.add(value)
        }

        constructor(values: ListTag<*>) {
            result.addAll(values.toList())
        }

        override fun accept(tag: Tag<*>): ListCollector {
            if (tag.javaClass != result.typeClass) {
                return HeterogenousListCollector().acceptAll(result.toList()).accept(tag)
            } else {
                result.add(tag)
                return this
            }
        }
    }

    private object InitialListCollector : ListCollector {
        override val result: Tag<*>
            get() = ListTag(Tag::class.java)

        override fun accept(tag: Tag<*>): ListCollector {
            return when (tag) {
                is CompoundTag -> HeterogenousListCollector().accept(tag)
                is ByteTag -> ByteListCollector(tag.asByte())
                is IntTag -> IntListCollector(tag.asInt())
                is LongTag -> LongListCollector(tag.asLong())
                else -> HomogenousListCollector(tag)
            }
        }
    }

    interface ListCollector {
        val result: Tag<*>
        fun accept(tag: Tag<*>): ListCollector
        fun acceptAll(tags: Iterable<Tag<*>>): ListCollector {
            var listCollector = this
            for (tag in tags) {
                listCollector = listCollector.accept(tag)
            }
            return listCollector
        }
    }

    private class NbtRecordBuilder :
        RecordBuilder.AbstractStringBuilder<Tag<*>, CompoundTag>(NBTOps) {
        override fun initBuilder() = CompoundTag()

        override fun append(
            key: String,
            value: Tag<*>,
            builder: CompoundTag
        ) = builder.apply { put(key, value) }

        override fun build(
            builder: CompoundTag,
            prefix: Tag<*>?
        ): DataResult<Tag<*>> {
            return when (prefix) {
                null, EndTag.INSTANCE -> DataResult.success(builder)
                is CompoundTag -> prefix.clone().run {
                    builder.forEach { (key, value) -> put(key, value) }
                    DataResult.success(this)
                }

                else -> DataResult.error({ "mergeToMap called with not a map: $prefix" }, prefix)
            }
        }
    }
}

fun ArrayTag<*>.asTagList(): List<Tag<*>> = when (this) {
    is ByteArrayTag -> value.map { ByteTag(it) }
    is IntArrayTag -> value.map { IntTag(it) }
    is LongArrayTag -> value.map { LongTag(it) }
    else -> throw IllegalArgumentException("Unknown ArrayTag subtype: ${this::class}")
}

object HASDataFixers {

    private val DATA_FIXER = createFixerUpper()

    fun getDataFixer(): DataFixer {
        return DATA_FIXER.fixer()
    }

    private fun createFixerUpper(): DataFixerBuilder.Result {
        val builder = DataFixerBuilder(HASStorageSource.DATA_VERSION)
        addFixers(builder)
        return builder.build()
    }

    private fun addFixers(fixer: DataFixerBuilder) {
    }
}