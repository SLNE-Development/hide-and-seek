package dev.slne.hideandnseek.command.sub

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.ListArgumentBuilder
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.command.getAreaOrThrow
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.player.HASPlayer
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.joinToComponent
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandTree.settingsCommand() = literalArgument("settings") {
    withPermission(HASPermissions.GAMERULE_COMMAND)

    HASGameRules().visitGameRuleTypes(object : HASGameRules.GameRuleTypeVisitor {
        override fun <T : HASGameRules.Value<T>> visit(
            key: HASGameRules.Key<T>,
            type: HASGameRules.Type<T>
        ) {
            literalArgument(key.id) {
                anyExecutor { sender, _ ->
                    queryRule(sender, key)
                }

                then(
                    type.createArgument("value")
                        .executes(CommandExecutor { sender, args ->
                            setRule(sender, args, key)
                        })
                )
            }
        }
    })

    literalArgument("initialSeekers") {
        anyExecutor { sender, _ ->
            val initialSeekers = plugin.data.settings.initialSeekers
            if (initialSeekers == null) {
                sender.sendText {
                    appendPrefix()
                    info("Es sind keine initialen Suchenden gesetzt.")
                }
                return@anyExecutor
            }

            sender.sendText {
                appendPrefix()
                info("Die initialen Suchenden sind ")
                append(initialSeekers.joinToComponent { it.displayName() })
            }
        }

        literalArgument("#clear") {
            anyExecutor { sender, _ ->
                plugin.data.settings.initialSeekers = null
                sender.sendText {
                    appendPrefix()
                    success("Die initialen Suchenden wurden zurückgesetzt.")
                }
            }
        }

        argument(
            ListArgumentBuilder<Player>("seekers")
                .withList(Bukkit.getOnlinePlayers())
                .withMapper { it.name }
                .buildGreedy()
        ) {
            anyExecutor { sender, args ->
                val seekers: List<Player> by args
                if (seekers.isEmpty()) {
                    throw CommandAPI.failWithString("Es wurden keine Spieler angegeben.")
                }

                plugin.data.settings.initialSeekers =
                    seekers.map { HASPlayer[it.uniqueId] }.toObjectSet()

                sender.sendText {
                    appendPrefix()
                    success("Die initialen Suchenden wurden auf ")
                    append(seekers.joinToComponent { it.displayName() })
                    success(" gesetzt.")
                }
            }
        }
    }

    literalArgument("startRadius") {
        playerExecutor { sender, _ ->
            val startRadius = sender.getAreaOrThrow().settings.startRadius
            sender.sendText {
                appendPrefix()
                info("Der Start-Radius ist ")
                variableValue(startRadius.toString())
            }
        }

        integerArgument("radius", 1) {
            playerExecutor { sender, args ->
                val radius: Int by args

                sender.getAreaOrThrow().settings.startRadius = radius
                sender.sendText {
                    appendPrefix()
                    success("Der Start-Radius wurde auf ")
                    variableValue(radius.toString())
                    success(" gesetzt.")
                }
            }
        }
    }

    literalArgument("endRadius") {
        playerExecutor { sender, _ ->
            val endRadius = sender.getAreaOrThrow().settings.endRadius
            sender.sendText {
                appendPrefix()
                info("Der End-Radius ist ")
                variableValue(endRadius.toString())
            }
        }

        integerArgument("radius", 1) {
            playerExecutor { sender, args ->
                val radius: Int by args

                val settings = sender.getAreaOrThrow().settings

                if (radius > settings.startRadius) {
                    throw CommandAPI.failWithString("Der End-Radius muss kleiner als der Start-Radius sein.")
                }

                settings.endRadius = radius
                sender.sendText {
                    appendPrefix()
                    success("Der End-Radius wurde auf ")
                    variableValue(radius.toString())
                    success(" gesetzt.")
                }
            }
        }
    }
}

private fun <T : HASGameRules.Value<T>> setRule(
    sender: CommandSender,
    args: CommandArguments,
    key: HASGameRules.Key<T>
) {
    val rule = plugin.data.settings.gameRules.getRule(key)
    rule.setFromArgument(args, "value", key)
    sender.sendText {
        appendPrefix()
        success("Die Spielregel ")
        append {
            variableValue(key.id)
            hoverEvent(Component.text(key.description, Colors.INFO))
        }
        success(" wurde auf ")
        variableValue(rule.serialize())
        success(" gesetzt.")
    }
}

private fun <T : HASGameRules.Value<T>> queryRule(sender: CommandSender, key: HASGameRules.Key<T>) {
    val rule = plugin.data.settings.gameRules.getRule(key)
    sender.sendText {
        appendPrefix()
        success("Die Spielregel ")
        append {
            variableValue(key.id)
            hoverEvent(Component.text(key.description, Colors.INFO))
        }
        success(" ist auf ")
        variableValue(rule.serialize())
        success(" gesetzt.")
    }
}