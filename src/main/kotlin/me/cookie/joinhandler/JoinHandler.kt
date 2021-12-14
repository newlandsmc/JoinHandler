package me.cookie.joinhandler

import me.cookie.joinhandler.listeners.PlayerJoin
import org.bukkit.plugin.java.JavaPlugin

class JoinHandler: JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerJoin(), this)
    }
}