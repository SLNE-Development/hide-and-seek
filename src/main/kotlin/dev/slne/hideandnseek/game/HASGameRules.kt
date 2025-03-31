package dev.slne.hideandnseek.game

import com.mojang.serialization.DynamicLike
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandArguments
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import io.papermc.paper.util.Tick
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import net.querz.nbt.tag.CompoundTag
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

class HASGameRules {
    companion object {
        private val GAME_RULE_TYPES =
            Object2ObjectAVLTreeMap<Key<*>, Type<*>>(Comparator.comparing { it.id })

        // @formatter:off
        val RULE_LOBBY_TIME = register("lobbyTime", DurationValue.create(60.seconds), "Wartezeit bis das Spiel beginnt")
        val RULE_PREPARATION_TIME = register("preparationTime", DurationValue.create(60.seconds), "Vorbereitungszeit für die Versteckenden")
        val RULE_GAME_TIME = register("gameTime", DurationValue.create(60.seconds), "Spielzeit für die Suchenden")
        val RULE_CELEBRATION_TIME_SECONDS = register("celebrationTime", DurationValue.create(60.seconds), "Feierzeit für die Suchenden bevor das Spiel endet")
        val RULE_GAME_START_RADIUS = register("gameStartRadius", IntegerValue.create(1000, 1, Int.MAX_VALUE), "Startradius für das Spiel")
        val RULE_GAME_END_RADIUS = register("gameEndRadius", IntegerValue.create(25, 1, Int.MAX_VALUE), "Endradius für das Spiel")
        val RULE_DO_HIDERS_BECOME_SEEKERS = register("doHidersBecomeSeekers", BooleanValue.create(false), "Ob die Versteckenden zu Suchenden werden")
        val RULE_BORDER_DAMAGE = register("borderDamage", DoubleValue.create(1.0, 0.0, Double.MAX_VALUE), "Schaden pro Tick der Grenze")
        val RULE_BORDER_BUFFER = register("borderBuffer", DoubleValue.create(0.0, 0.0, Double.MAX_VALUE), "Buffer der Grenze")
        val RULE_IS_ONE_HIT_KNOCK_OUT = register("isOneHitKnockOut", BooleanValue.create(false), "Ob die Spieler mit einem Schlag rausfliegen")
        val RULE_LOBBY_BORDER_RADIUS = register("lobbyBorderRadius", IntegerValue.create(512, 1, Int.MAX_VALUE), "Wartezeitradius")
        val RULE_SEEKER_AMOUNT = register("seekerAmount", IntegerValue.create(1, 1, Int.MAX_VALUE), "Anzahl der Suchenden")
        // @formatter:on

        private fun<T: Value<T>> register(name: String, type: Type<T>, description: String): Key<T> {
            val key = Key<T>(name, description)
            val prevKey = GAME_RULE_TYPES.put(key, type)

            if (prevKey != null) {
                throw IllegalStateException("Game rule $name is already registered")
            }

            return key
        }
    }

    private val rules: Object2ObjectMap<Key<*>, Value<*>>
    private val gameruleArray: Array<Value<*>>

    constructor() : this(GAME_RULE_TYPES.mapValuesTo(mutableObject2ObjectMapOf()) { it.value.createRule() })

    constructor(values: DynamicLike<*>) : this() {
        loadFromTag(values)
    }

    private constructor(rules: Object2ObjectMap<Key<*>, Value<*>>) {
        this.rules = rules
        val arraySize = Key.lastGameRuleIndex

        val values = arrayOfNulls<Value<*>>(arraySize)
        for ((key, value) in rules) {
            values[key.gameRuleIndex] = value
        }
        gameruleArray = values.requireNoNulls()
    }

    fun <T : Value<T>> getRule(key: Key<T>): T {
        @Suppress("UNCHECKED_CAST")
        return gameruleArray[key.gameRuleIndex] as? T
            ?: error("Tried to access invalid game rule: ${key.id}")
    }

    fun createTag() = CompoundTag().apply {
        rules.forEach { key, value ->
            putString(key.id, value.serialize())
        }
    }

    private fun loadFromTag(values: DynamicLike<*>) {
        rules.forEach { key, value ->
            values.get(key.id).asString().ifSuccess(value::deserialize)
        }
    }

    fun visitGameRuleTypes(visitor: GameRuleTypeVisitor) {
        @Suppress("UNCHECKED_CAST")
        fun <T : Value<T>> callVisitorCap(
            visitor: GameRuleTypeVisitor,
            key: Key<*>,
            type: Type<*>
        ) {
            val type = type as Type<T>
            val key = key as Key<T>
            visitor.visit(key, type)
            type.callVisitor(visitor, key)
        }

        GAME_RULE_TYPES.forEach { key, type ->
            callVisitorCap(visitor, key, type)
        }
    }

    fun getBoolean(key: Key<BooleanValue>): Boolean {
        return getRule(key).get()
    }

    fun getInteger(key: Key<IntegerValue>): Int {
        return getRule(key).get()
    }

    fun getDouble(key: Key<DoubleValue>): Double {
        return getRule(key).get()
    }

    fun getLong(key: Key<LongValue>): Long {
        return getRule(key).get()
    }

    fun getDuration(key: Key<DurationValue>): Duration {
        return getRule(key).get()
    }

    class Key<T : Value<T>>(val id: String, val description: String) {
        companion object {
            var lastGameRuleIndex = 0
        }

        val gameRuleIndex = lastGameRuleIndex++
    }

    class Type<T : Value<T>> internal constructor(
        private val argument: (name: String) -> Argument<*>,
        private val constructor: (Type<T>) -> T,
        val callback: (HASGame, T) -> Unit,
        private val visitorCaller: VisitorCaller<T>,
    ) {
        fun createArgument(name: String): Argument<*> {
            return argument(name)
        }

        fun createRule(): T {
            return constructor(this)
        }

        fun callVisitor(visitor: GameRuleTypeVisitor, key: Key<T>) {
            visitorCaller(visitor, key, this)
        }
    }

    abstract class Value<T : Value<T>>(protected val type: Type<T>) {
        abstract fun updateFromArgument(args: CommandArguments, name: String, gameRuleKey: Key<T>)

        fun setFromArgument(args: CommandArguments, name: String, gameRuleKey: Key<T>) {
            updateFromArgument(args, name, gameRuleKey)
        }

        fun onChanged(game: HASGame?) {
            if (game != null) {
                type.callback(game, self())
            }
        }

        abstract fun deserialize(value: String)
        abstract fun serialize(): String
        override fun toString() = serialize()

        abstract fun self(): T
        abstract fun copy(): T
        abstract fun setFrom(value: T, game: HASGame?)
    }

    class BooleanValue(type: Type<BooleanValue>, initialValue: Boolean) :
        Value<BooleanValue>(type) {
        private var value = initialValue

        override fun updateFromArgument(
            args: CommandArguments,
            name: String,
            gameRuleKey: Key<BooleanValue>
        ) {
            value = args.get(name) as Boolean
        }

        fun get() = value

        fun set(value: Boolean, game: HASGame?) {
            this.value = value
            onChanged(game)
        }

        override fun serialize(): String {
            return value.toString()
        }

        override fun deserialize(value: String) {
            this.value = value.toBoolean()
        }

        override fun self() = this

        override fun copy(): BooleanValue {
            return BooleanValue(type, value)
        }

        override fun setFrom(
            value: BooleanValue,
            game: HASGame?
        ) {
            this.value = value.value
            onChanged(game)
        }

        companion object {
            fun create(
                initialValue: Boolean,
                changeCallback: (HASGame, BooleanValue) -> Unit
            ): Type<BooleanValue> {
                return Type(
                    { BooleanArgument(it) },
                    { BooleanValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitBoolean
                )
            }

            fun create(initialValue: Boolean) = create(initialValue) { _, _ -> }
        }
    }

    class IntegerValue(type: Type<IntegerValue>, initialValue: Int) : Value<IntegerValue>(type) {
        private var value = initialValue
        override fun updateFromArgument(
            args: CommandArguments,
            name: String,
            gameRuleKey: Key<IntegerValue>
        ) {
            value = args.get(name) as Int
        }

        fun get() = value
        fun set(value: Int, game: HASGame?) {
            this.value = value
            onChanged(game)
        }

        override fun deserialize(value: String) {
            this.value = value.toInt()
        }

        override fun serialize(): String {
            return value.toString()
        }

        override fun self() = this

        override fun copy(): IntegerValue {
            return IntegerValue(type, value)
        }

        override fun setFrom(
            value: IntegerValue,
            game: HASGame?
        ) {
            this.value = value.value
            onChanged(game)
        }

        companion object {
            fun create(
                initialValue: Int,
                changeCallback: (HASGame, IntegerValue) -> Unit
            ): Type<IntegerValue> {
                return Type(
                    { IntegerArgument(it) },
                    { IntegerValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitInteger
                )
            }

            fun create(
                initialValue: Int,
                min: Int,
                max: Int,
                changeCallback: (HASGame, IntegerValue) -> Unit
            ): Type<IntegerValue> {
                return Type(
                    { IntegerArgument(it, min, max) },
                    { IntegerValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitInteger
                )
            }

            fun create(initialValue: Int) = create(initialValue) { _, _ -> }
            fun create(
                initialValue: Int,
                min: Int,
                max: Int
            ) = create(initialValue, min, max) { _, _ -> }
        }
    }

    class DoubleValue(type: Type<DoubleValue>, initialValue: Double) :
        Value<DoubleValue>(type) {
        private var value = initialValue

        companion object {
            fun create(
                initialValue: Double,
                changeCallback: (HASGame, DoubleValue) -> Unit
            ): Type<DoubleValue> {
                return Type(
                    { IntegerArgument(it) },
                    { DoubleValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitDouble
                )
            }

            fun create(
                initialValue: Double,
                min: Double,
                max: Double,
                changeCallback: (HASGame, DoubleValue) -> Unit
            ): Type<DoubleValue> {
                return Type(
                    { IntegerArgument(it, min.toInt(), max.toInt()) },
                    { DoubleValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitDouble
                )
            }

            fun create(initialValue: Double) = create(initialValue) { _, _ -> }
            fun create(
                initialValue: Double,
                min: Double,
                max: Double
            ) = create(initialValue, min, max) { _, _ -> }
        }

        override fun updateFromArgument(
            args: CommandArguments,
            name: String,
            gameRuleKey: Key<DoubleValue>
        ) {
            value = args.get(name) as Double
        }

        fun get() = value

        fun set(value: Double, game: HASGame?) {
            this.value = value
            onChanged(game)
        }

        override fun serialize(): String {
            return value.toString()
        }

        override fun deserialize(value: String) {
            this.value = value.toDouble()
        }

        override fun self() = this

        override fun copy(): DoubleValue {
            return DoubleValue(type, value)
        }

        override fun setFrom(
            value: DoubleValue,
            game: HASGame?
        ) {
            this.value = value.value
            onChanged(game)
        }
    }

    class LongValue(type: Type<LongValue>, initialValue: Long) :
        Value<LongValue>(type) {
        private var value = initialValue

        companion object{
            fun create(
                initialValue: Long,
                changeCallback: (HASGame, LongValue) -> Unit
            ): Type<LongValue> {
                return Type(
                    { LongArgument(it) },
                    { LongValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitLong
                )
            }

            fun create(initialValue: Long) = create(initialValue) { _, _ -> }

            fun create(
                initialValue: Long,
                min: Long,
                max: Long,
                changeCallback: (HASGame, LongValue) -> Unit
            ): Type<LongValue> {
                return Type(
                    { LongArgument(it, min, max) },
                    { LongValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitLong
                )
            }

            fun create(
                initialValue: Long,
                min: Long,
                max: Long
            ) = create(initialValue, min, max) { _, _ -> }
        }

        override fun updateFromArgument(
            args: CommandArguments,
            name: String,
            gameRuleKey: Key<LongValue>
        ) {
            value = args.get(name) as Long
        }

        fun get() = value

        fun set(value: Long, game: HASGame?) {
            this.value = value
            onChanged(game)
        }

        override fun serialize(): String {
            return value.toString()
        }

        override fun deserialize(value: String) {
            this.value = value.toLong()
        }

        override fun self() = this

        override fun copy(): LongValue {
            return LongValue(type, value)
        }

        override fun setFrom(
            value: LongValue,
            game: HASGame?
        ) {
            this.value = value.value
            onChanged(game)
        }
    }

    class DurationValue(type: Type<DurationValue>, initialValue: Duration) :
        Value<DurationValue>(type) {
        private var value = initialValue

        companion object {
            fun create(
                initialValue: Duration,
                changeCallback: (HASGame, DurationValue) -> Unit
            ): Type<DurationValue> {
                return Type(
                    { TimeArgument(it) },
                    { DurationValue(it, initialValue) },
                    changeCallback,
                    GameRuleTypeVisitor::visitDuration
                )
            }

            fun create(initialValue: Duration) = create(initialValue) { _, _ -> }
        }

        override fun updateFromArgument(
            args: CommandArguments,
            name: String,
            gameRuleKey: Key<DurationValue>
        ) {
            val ticks = args.get(name) as Int
            value = Tick.of(ticks.toLong()).toKotlinDuration()
        }

        fun get() = value
        fun set(value: Duration, game: HASGame?) {
            this.value = value
            onChanged(game)
        }

        override fun serialize(): String {
            return value.inWholeMilliseconds.toString()
        }

        override fun deserialize(value: String) {
            this.value = value.toLong().milliseconds
        }

        override fun self() = this

        override fun copy(): DurationValue {
            return DurationValue(type, value)
        }

        override fun setFrom(
            value: DurationValue,
            game: HASGame?
        ) {
            this.value = value.value
            onChanged(game)
        }
    }

    interface GameRuleTypeVisitor {
        fun <T : Value<T>> visit(key: Key<T>, type: Type<T>) {}
        fun visitBoolean(key: Key<BooleanValue>, type: Type<BooleanValue>) {
        }

        fun visitInteger(key: Key<IntegerValue>, type: Type<IntegerValue>) {

        }

        fun visitDouble(key: Key<DoubleValue>, type: Type<DoubleValue>) {

        }

        fun visitLong(key: Key<LongValue>, type: Type<LongValue>) {

        }

        fun visitDuration(key: Key<DurationValue>, type: Type<DurationValue>) {

        }
    }

    internal fun interface VisitorCaller<T : Value<T>> {
        operator fun invoke(visitor: GameRuleTypeVisitor, key: Key<T>, type: Type<T>)
    }
}