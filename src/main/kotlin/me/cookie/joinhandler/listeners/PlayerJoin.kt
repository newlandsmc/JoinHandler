package me.cookie.joinhandler.listeners

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.math.Vector3
import com.sk89q.worldedit.regions.CuboidRegion
import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.formatPlayerPlaceholders
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

val oldBlocks = HashMap<Player, HashMap<Vector3, Material>>()
val playerCampfire = HashMap<Player, Clipboard>()
val playerOffsets = HashMap<Player, CuboidRegion>()

class PlayerJoin(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (!player.hasPlayedBefore() || plugin.config.getBoolean("debug-spawn")) {
            event.joinMessage(
                    plugin.config.getString("first-join")!!
                            .formatPlayerPlaceholders(player)
                            .formatMinimessage()
            )
        } else {
            event.joinMessage(
                    plugin.config.getString("welcome-back")!!
                            .formatPlayerPlaceholders(player)
                            .formatMinimessage()
            )
        }
    }
}
