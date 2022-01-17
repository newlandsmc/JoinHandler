package me.cookie.joinhandler.listeners

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.Vector3
import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.formatPlayerPlaceholders
import me.cookie.cookiecore.inDialogue
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerQuit(private val plugin: JavaPlugin): Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        val player = event.player

        if(!player.hasPlayedBefore() && player.inDialogue){
            event.quitMessage(null) // Don't send quit message if they haven't completed dialogue yet.
        }else{
            event.quitMessage(plugin.config.getString("leave-message")!!
                .formatPlayerPlaceholders(player).formatMinimessage())
        }

        if(!player.hasPlayedBefore()) {
            if(plugin.config.getBoolean("revert-spawn-structure")){
                val clipboard = playerCampfire[player]
                val offsets = playerOffsets[player]!!
                if(clipboard != null){
                    for (i in 0..offsets.maximumPoint.blockX - offsets.minimumPoint.blockX) {
                        for (j in 0..offsets.maximumPoint.blockY - offsets.minimumPoint.blockY) {
                            for (k in 0..offsets.maximumPoint.blockZ - offsets.minimumPoint.blockZ) {
                                val block: Block = player.world.getBlockAt(
                                    i + offsets.minimumPoint.blockX,
                                    j + offsets.minimumPoint.blockY,
                                    k + offsets.minimumPoint.blockZ
                                )
                                if(block.type == Material.CHEST) continue
                                if(block.type == BukkitAdapter.adapt(clipboard.getBlock(
                                        BukkitAdapter.adapt(
                                            Location(player.world,
                                                i.toDouble() + clipboard.minimumPoint.blockX,
                                                j.toDouble() + clipboard.minimumPoint.blockY,
                                                k.toDouble() + clipboard.minimumPoint.blockZ)
                                        ).toVector().toBlockPoint().subtract()
                                    )).material){
                                    block.type = oldBlocks[player]!![Vector3.at(i.toDouble(), j.toDouble(), k.toDouble())]!!
                                }
                            }
                        }
                    }
                    oldBlocks.remove(player)
                    playerCampfire.remove(player)
                    playerOffsets.remove(player)
                }
            }
        }
    }
}