package me.cookie.joinhandler.listeners

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin: Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        event.player.sendMessage(
            Component.text("This is a test message.")
        )
    }
}