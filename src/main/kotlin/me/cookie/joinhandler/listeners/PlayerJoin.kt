package me.cookie.joinhandler.listeners

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.math.Vector3
import com.sk89q.worldedit.regions.CuboidRegion
import me.cookie.joinhandler.JoinHandler
import me.cookie.joinhandler.StarterCampfire
import me.cookie.semicore.formatPlayerPlaceholders
import me.cookie.semicore.message.dialogue.Dialogue
import me.cookie.semicore.message.dialogue.queueDialogue
import me.cookie.semicore.message.messagequeueing.MessageReceiver
import me.cookie.semicore.message.messagequeueing.QueuedMessage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.event.player.PlayerSpawnLocationEvent


val oldBlocks = HashMap<Player, HashMap<Vector3, Material>>()
val playerCampfire = HashMap<Player, Clipboard>()
val playerOffsets = HashMap<Player, CuboidRegion>()

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(JoinHandler::class.java)


    @EventHandler
    fun onPlayerSpawn(event: PlayerSpawnLocationEvent){

        val player = event.player
        if(!player.hasPlayedBefore() || plugin.config.getBoolean("debug-spawn")){
            val campfireSpawner = StarterCampfire()
            val clipboard = campfireSpawner.clipboard
            val offsets = campfireSpawner.getPlayerOffsets(player)

            val locationTypeMap = HashMap<Vector3, Material>()
            for (i in 0..offsets.maximumPoint.blockX - offsets.minimumPoint.blockX) {
                for (j in 0..offsets.maximumPoint.blockY - offsets.minimumPoint.blockY) {
                    for (k in 0..offsets.maximumPoint.blockZ - offsets.minimumPoint.blockZ) {
                        val block: Block = player.world.getBlockAt(
                            i + offsets.minimumPoint.blockX,
                            j + offsets.minimumPoint.blockY,
                            k + offsets.minimumPoint.blockZ
                        )
                        locationTypeMap[Vector3.at(i.toDouble(), j.toDouble(), k.toDouble())] = block.type
                    }
                }
            }

            oldBlocks[player] = locationTypeMap
            playerCampfire[player] = clipboard
            playerOffsets[player] = offsets
            player.queueDialogue(
                createFirstJoinDialogue(player)
            )

            campfireSpawner.spawn(player)
        }
    }


    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        if(!event.player.hasPlayedBefore() || plugin.config.getBoolean("debug-spawn")){
            event.joinMessage(
                MiniMessage.get().parse(
                    plugin.config.getString("first-join")!!
                        .formatPlayerPlaceholders(event.player)
                )
            )
        }else{
            event.joinMessage(
                MiniMessage.get().parse(
                    plugin.config.getString("welcome-back")!!
                        .formatPlayerPlaceholders(event.player)
                )
            )
        }

    }

    // Probably move this to dialogue system in core?

    private fun createFirstJoinDialogue(player: Player): Dialogue{
        val messageList = mutableListOf<QueuedMessage>()
        val messageConfigSection = plugin.config.getConfigurationSection("first-join-dialogue")!!
            .getKeys(false)

        for ((i, message) in messageConfigSection.withIndex()){
            var delay = 0
            for(j in 0 until i){
                delay += plugin.config
                    .getInt("first-join-dialogue.${messageConfigSection.toTypedArray()[j+1]}.delay") + 1
            }
            messageList.add(
                QueuedMessage(
                    message = MiniMessage.get().parse(
                        plugin.config.getString("first-join-dialogue.${message}.message")!!
                    ),
                    receiver = MessageReceiver.PLAYER,
                    System.currentTimeMillis() + (delay.toLong() * 1000),
                    playerToSend = player
                )
            )
        }

        return Dialogue(
            messages = messageList,
            receiver = MessageReceiver.PLAYER,
            whenToSend = System.currentTimeMillis()
        )
    }
}