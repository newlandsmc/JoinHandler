package me.cookie.joinhandler.listeners

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.Vector3
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuit: Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        val player = event.player
        val clipboard = playerCampfire[player]
        if(clipboard != null){
            for (i in clipboard.minimumPoint.blockX..clipboard.maximumPoint.blockX) {
                for (j in clipboard.minimumPoint.blockY..clipboard.maximumPoint.blockY) {
                    for (k in clipboard.minimumPoint.blockZ..clipboard.maximumPoint.blockZ) {
                        val block: Block = player.world.getBlockAt(i, j, k)
                        if(block.type == BukkitAdapter.adapt(clipboard.getBlock(
                                BukkitAdapter.adapt(
                                    Location(player.world, i.toDouble(), j.toDouble(), k.toDouble())
                                ).toVector().toBlockPoint()
                        )).material){
                            println("ran")
                            Location(player.world, i.toDouble(), j.toDouble(), k.toDouble()).
                            block.type = Material.AIR
                            println(oldBlocks[player]!![Vector3.at(i.toDouble(), j.toDouble(), k.toDouble())]!!)
                        }
                    }
                }
            }
            oldBlocks.remove(player)
            playerCampfire.remove(player)
        }
    }
}