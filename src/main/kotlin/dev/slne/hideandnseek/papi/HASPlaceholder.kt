package dev.slne.hideandnseek.papi

import dev.slne.hideandnseek.papi.placeholder.HASCountPlaceholder
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiExpansion

class HASPlaceholder: PapiExpansion(
    "has",
    listOf(HASCountPlaceholder())
)