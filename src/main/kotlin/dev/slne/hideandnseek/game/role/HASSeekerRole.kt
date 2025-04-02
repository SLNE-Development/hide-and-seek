package dev.slne.hideandnseek.game.role

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.jeff_media.morepersistentdatatypes.DataType
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.hideandnseek.player.HASPlayer
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.hideandnseek.util.tp
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.bukkit.api.event.listen
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.bukkit.api.util.key
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers
import kotlinx.coroutines.*
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.util.Ticks
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val specialItemCooldownGroup = key("has_special_item_cooldown")

object HASSeekerRole : HASRole("Sucher", TextColor.color(0xE74C3C)) {
    val specialItemKey = key("has_special_item")

    init {
        listen<PlayerInteractEvent> {
            val item = item
            val type = item?.persistentDataContainer
                ?.get(specialItemKey, SpecialItemType.pdcType)
                ?: return@listen

            plugin.launch { type.handle(player, item) }
        }
    }

    override suspend fun giveInventory(player: Player) =
        withContext(plugin.entityDispatcher(player)) {
//            Items.prepareSeekerInventory(player)

            val sword = buildItem(Material.WOODEN_SWORD) {
                displayName { primary("Schwert des Suchers") }
                buildLore {
                    line { secondary("Nutze dieses Schwert, um die versteckten") }
                    line { secondary("Spieler aufzuspüren und zu fangen.") }
                    line { info("Es ist unzerstörbar und dein treuer Begleiter.") }
                }
                meta {
                    isUnbreakable = true
                }
            }
            val bow = buildItem(Material.BOW) {
                displayName { primary("Bogen des Jägers") }
                buildLore {
                    line { secondary("Nutze den Bogen, um flüchtende Spieler") }
                    line { secondary("aus der Ferne aufzuhalten.") }
                    line { info("Unendlich haltbar, für unendliche Jagd.") }
                }
                meta {
                    isUnbreakable = true
                }
                addEnchantment(Enchantment.INFINITY, Enchantment.INFINITY.maxLevel)
            }
            val arrow = buildItem(Material.ARROW) {
                displayName { primary("Pfeil der Unendlichkeit") }
                buildLore {
                    line { secondary("Ein einfacher Pfeil, der niemals ausgeht.") }
                    line { info("Nutze ihn weise und treffsicher.") }
                }
            }

            val helmet = buildItem(Material.LEATHER_HELMET) {
                displayName { primary("Helm des Aufspürers") }
                buildLore {
                    line { secondary("Dieser Helm schützt dich") }
                    line { secondary("und unterstützt dich bei der Jagd nach Versteckten.") }
                    line { info("Leicht, robust und zuverlässig.") }
                }
                meta<LeatherArmorMeta> {
                    setColor(Color.BLUE)
                    isUnbreakable = true
                }
            }
            val chestplate = buildItem(Material.LEATHER_CHESTPLATE) {
                displayName { primary("Brustplatte des Suchers") }
                buildLore {
                    line { secondary("Verleiht dir Schutz und Stärke bei") }
                    line { secondary("deiner Mission, alle Spieler zu finden.") }
                    line { info("Sie wird niemals versagen.") }
                }
                meta<LeatherArmorMeta> {
                    setColor(Color.BLUE)
                    isUnbreakable = true
                }
            }
            val leggings = buildItem(Material.LEATHER_LEGGINGS) {
                displayName { primary("Hose des Suchers") }
                buildLore {
                    line { secondary("Mit dieser Hose bewegst du dich geschmeidig") }
                    line { secondary("durch jedes Terrain.") }
                    line { info("Robust und zuverlässig für die Jagd.") }
                }
                meta<LeatherArmorMeta> {
                    setColor(Color.BLUE)
                    isUnbreakable = true
                }
            }
            val boots = buildItem(Material.LEATHER_BOOTS) {
                displayName { primary("Stiefel des Fährtenlesers") }
                buildLore {
                    line { secondary("Geräuschlos und leicht, ideal um dich deinen") }
                    line { secondary("Gegnern unbemerkt zu nähern.") }
                    line { info("Die perfekten Schuhe für den Sucher.") }
                }
                meta<LeatherArmorMeta> {
                    setColor(Color.BLUE)
                    isUnbreakable = true
                }
            }

            val glowItem = buildItem(Material.GLOWSTONE_DUST) {
                displayName { primary("Leuchtendes Item") }
                buildLore {
                    line { secondary("Ein Item, das leuchtet.") }
                    line { info("Es ist ein Platzhalter.") }
                }

                editPersistentDataContainer {
                    it.set(specialItemKey, SpecialItemType.pdcType, SpecialItemType.GLOW)
                }
            }
            val shrinkItem = buildItem(Material.BARRIER) {
                displayName { primary("Verstecktes Item") }
                buildLore {
                    line { secondary("Ein Item, das nicht sichtbar ist.") }
                    line { info("Es ist ein Platzhalter.") }
                }

//                editPersistentDataContainer {
//                    it.set(specialItemKey, SpecialItemType.pdcType, SpecialItemType.SHRINK)
//                }


                @Suppress("UnstableApiUsage")
                setData(
                    DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                        .addModifier(
                            Attribute.SCALE, AttributeModifier(
                                key("shrink"),
                                plugin.data.settings.gameRules.getDouble(HASGameRules.RULE_PLAYER_SCALE) - 1,
                                AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            )
                        )
                )
            }

            with(player.inventory) {
                clear()
                setItem(0, sword)
                setItem(1, bow)
                setItem(8, arrow)
                setItem(4, glowItem)
                setItem(5, shrinkItem)

                this.helmet = helmet
                this.chestplate = chestplate
                this.leggings = leggings
                this.boots = boots
            }

            val has = player.HAS
            SpecialItemType.GLOW.sendRemainingCooldown(
                has,
                glowItem,
                plugin.data.settings.gameRules
                    .getDuration(HASGameRules.RULE_SPECIAL_ITEM_COOLDOWN)
            )
        }

    override suspend fun teleportStartPosition(player: Player, game: HASGame) {
        player.tp(game.settings.lobbyLocation)
    }

    override fun canDamage(role: HASRole) = role != this

    private enum class SpecialItemType() {
        GLOW {
            override suspend fun handle(
                player: Player,
                item: ItemStack
            ) {
                val duration = plugin.data.settings.gameRules
                    .getDuration(HASGameRules.RULE_GLOW_ITEM_EFFECT_DURATION)

                if (runPreCheck(player, item,  plugin.data.settings.gameRules
                        .getDuration(HASGameRules.RULE_SPECIAL_ITEM_COOLDOWN))) {
                    val effect = PotionEffectType.GLOWING.createEffect(
                        (duration.inWholeMilliseconds / Ticks.SINGLE_TICK_DURATION_MS).toInt(),
                        255
                    ).withIcon(false).withParticles(false).withAmbient(false)

                    forEachPlayerInRegion({
                        val has = it.HAS
                        if (has.hider) {
                            it.addPotionEffect(effect)
                        } else if (has.seeker) {
                            it.sendText {
                                appendPrefix()
                                info("Die Versteckten Spieler sind nun für ")
                                append(
                                    TimeUtil.formatLongTimestamp(
                                        TimeUnit.SECONDS,
                                        duration.inWholeSeconds,
                                        Colors.VARIABLE_VALUE
                                    )
                                )
                                info(" sichtbar.")
                            }
                        }
                    })
                }
            }
        },
        SHRINK {
            override suspend fun handle(
                player: Player,
                item: ItemStack
            ) {
                val cooldown =
                    plugin.data.settings.gameRules.getDuration(HASGameRules.RULE_SPECIAL_ITEM_COOLDOWN)

                if (runPreCheck(player, item, cooldown)) {
                    withContext(plugin.entityDispatcher(player)) {
                        val scale = player.getAttribute(Attribute.SCALE)
                            ?: error("Scale attribute is null for player ${player.name}")
                        val currentScale = scale.baseValue
                        val newScale = plugin.data.settings.gameRules
                            .getDouble(HASGameRules.RULE_PLAYER_SCALE)

                        scale.baseValue = newScale
                        player.sendText {
                            appendPrefix()
                            info("Du bist nun auf ")
                            variableValue(newScale)
                            info(" geschrumpft.")
                        }

                        delay(cooldown)
                        scale.baseValue = currentScale
                        player.sendText {
                            appendPrefix()
                            info("Du bist nun wieder auf ")
                            variableValue(currentScale)
                            info(" gewachsen.")
                        }
                    }
                }
            }
        };

        abstract suspend fun handle(player: Player, item: ItemStack)

        private var lastUseTime = 0L

        /**
         * Checks if the item can be used and updates the last use time.
         *
         * @param player The player who used the item.
         * @param item The item that was used.
         * @param cooldown The cooldown duration.
         * @return true if the item can be used, false otherwise.
         */
        suspend fun runPreCheck(
            player: Player,
            item: ItemStack,
            cooldown: Duration
        ): Boolean {
            val game = HASManager.currentGame ?: run {
                player.sendText {
                    appendPrefix()
                    error("Aktuell ist kein Spiel aktiv und du kannst das Item nicht benutzen.")
                }
                return false
            }

            if (game.canPlayersJoin) {
                player.sendText {
                    appendPrefix()
                    error("Du kannst das Item noch nicht benutzen, da das Spiel noch nicht gestartet ist.")
                }
                return false
            }

            val currentTime = System.currentTimeMillis()
            val remainingTime = cooldown.inWholeMilliseconds - (currentTime - lastUseTime)

            if (remainingTime <= 0) {
                lastUseTime = currentTime

                val cooldownJobs = coroutineScope {
                    game.seekers.map { seeker ->
                        launch {
                            seeker.sendCooldown(item, cooldown)
                        }
                    }
                }

                cooldownJobs.joinAll()
                return true
            }

            return false
        }

        suspend fun sendRemainingCooldown(
            player: HASPlayer,
            item: ItemStack,
            cooldown: Duration
        ) {
            val remainingTime = cooldown.inWholeMilliseconds - (System.currentTimeMillis() - lastUseTime)
            val remainingDuration = remainingTime.milliseconds
            player.sendCooldown(item, remainingDuration)
        }

        companion object {
            val pdcType: PersistentDataType<String, SpecialItemType> =
                DataType.asEnum(SpecialItemType::class.java)
        }
    }
}