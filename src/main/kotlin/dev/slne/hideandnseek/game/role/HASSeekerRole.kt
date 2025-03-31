package dev.slne.hideandnseek.game.role

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.tp
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.LeatherArmorMeta

object HASSeekerRole : HASRole("Sucher", TextColor.color(0xE74C3C)) {
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

            with(player.inventory) {
                clear()
                setItem(0, sword)
                setItem(1, bow)
                setItem(8, arrow)

                this.helmet = helmet
                this.chestplate = chestplate
                this.leggings = leggings
                this.boots = boots
            }
        }

    override suspend fun teleportStartPosition(player: Player) {
        player.tp(HASManager.settings.lobbyLocation)
    }

    override fun canDamage(role: HASRole) = role != this

}