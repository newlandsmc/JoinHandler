package me.cookie.joinhandler

import me.cookie.joinhandler.commands.SpawnCampfire
import me.cookie.joinhandler.listeners.PlayerJoin
import me.cookie.joinhandler.listeners.PlayerQuit
import org.bukkit.plugin.java.JavaPlugin

class JoinHandler: JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerJoin(this), this)
        server.pluginManager.registerEvents(PlayerQuit(this), this)

        getCommand("spawncampfire")!!.setExecutor(SpawnCampfire(this))
        saveDefaultConfig()
    }
}