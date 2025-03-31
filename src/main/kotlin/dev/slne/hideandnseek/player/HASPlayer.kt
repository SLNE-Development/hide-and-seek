package dev.slne.hideandnseek.player

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.role.HASHiderRole
import dev.slne.hideandnseek.game.role.HASRole
import dev.slne.hideandnseek.game.role.HASSeekerRole
import dev.slne.hideandnseek.game.role.HASUndefinedRole
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.tp
import dev.slne.surf.surfapi.core.api.messages.adventure.Sound
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound
import net.querz.nbt.tag.CompoundTag
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.util.*
import org.bukkit.Sound as BukkitSound

class HASPlayer(val uuid: UUID) {
    val online get() = player?.isOnline == true
    val seeker get() = role == HASSeekerRole
    val hider get() = role == HASHiderRole
    val player: Player? get() = Bukkit.getPlayer(uuid)

    @Volatile
    var role: HASRole = HASUndefinedRole
        private set

    suspend fun setRole(role: HASRole, sendMessage: Boolean = true) {
        val previousRole = this.role
        this.role = role
        val player = player ?: return
        withContext(plugin.entityDispatcher(player)) {
            with(player) {
                gameMode = role.gameMode
                role.giveInventory(this)
            }

            if (sendMessage) {
                val roleChanged = previousRole != HASUndefinedRole
                player.sendText {
                    appendPrefix()
                    info("Du bist ")
                    if (roleChanged) {
                        info("nun ")
                    }
                    info("ein ")
                    text(role.displayName, color = role.color)
                    info("!")
                }
                player.playSound(Sound {
                    type(BukkitSound.ENTITY_PLAYER_LEVELUP)
                    volume(0.75f)
                    source(Sound.Source.PLAYER)
                }, Sound.Emitter.self())
            }
        }
    }

    suspend fun setScale(scale: Double) {
        val player = player ?: return
        withContext(plugin.entityDispatcher(player)) {
            with(player) {
                val attribute = getAttribute(Attribute.SCALE)
                if (attribute != null) {
                    attribute.baseValue = scale
                }
            }
        }
    }

    suspend fun reset() {
        val player = player ?: return
        withContext(plugin.entityDispatcher(player)) {
            with(player) {
                inventory.clear()
                health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
                foodLevel = 20
                saturation = 20f
                fireTicks = 0
                isFlying = false
                allowFlight = false
                gameMode = GameMode.ADVENTURE
            }
            setScale(1.0)
        }
    }

    suspend fun prepare() {
        reset()

        val player = player ?: return
        role.giveInventory(player)
        role.teleportStartPosition(player)
        role.applyScale(player)
    }

    suspend fun teleportToSpawn() {
        player?.tp(HASManager.settings.spawnLocation)
    }

    fun displayName() = buildText {
        text(player?.name ?: "#UNKNOWN", color = role.color)
    }

    fun toTag() = CompoundTag().apply {
        putString("uuid", uuid.toString())
        putString("roleClass", role::class.java.name)
    }

    companion object {
        operator fun get(uuid: UUID): HASPlayer {
            return HASPlayerManager.get(uuid)
        }

        fun fromTag(tag: CompoundTag): HASPlayer {
            val uuid = UUID.fromString(tag.getString("uuid"))
            val roleClass = Class.forName(tag.getString("roleClass")) as Class<out HASRole>
            val role = roleClass.kotlin.objectInstance ?: error("Role class $roleClass is not an object")

            val hasPlayer = HASPlayer(uuid)
            hasPlayer.role = role

            return hasPlayer
        }
    }
}