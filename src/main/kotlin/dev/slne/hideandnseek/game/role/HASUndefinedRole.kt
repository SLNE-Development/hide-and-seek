package dev.slne.hideandnseek.game.role

import net.kyori.adventure.text.format.TextColor

object HASUndefinedRole : HASRole("Unbekannt", TextColor.color(0xFFFFFF)) {
    override fun canDamage(role: HASRole) = false
}