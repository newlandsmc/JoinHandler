package me.cookie.joinhandler.listeners

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.math.Vector3
import me.cookie.joinhandler.JoinHandler
import me.cookie.joinhandler.StarterCampfire
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
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.event.player.PlayerSpawnLocationEvent


val oldBlocks = HashMap<Player, HashMap<Vector3, Material>>()
val playerCampfire = HashMap<Player, Clipboard>()

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(JoinHandler::class.java)


    @EventHandler
    fun onPlayerSpawn(event: PlayerSpawnLocationEvent){

        val player = event.player
        if(player.hasPlayedBefore()){
            val campfireSpawner = StarterCampfire()
            val clipboard = campfireSpawner.clipboard
            val locationTypeMap = HashMap<Vector3, Material>()
            for (i in clipboard.minimumPoint.blockX..clipboard.maximumPoint.blockX) {
                for (j in clipboard.minimumPoint.blockY..clipboard.maximumPoint.blockY) {
                    for (k in clipboard.minimumPoint.blockZ..clipboard.maximumPoint.blockZ) {
                        val block: Block = player.world.getBlockAt(i, j, k)
                        locationTypeMap[Vector3.at(i.toDouble(), j.toDouble(), k.toDouble())] = block.type
                    }
                }
            }
            oldBlocks[player] = locationTypeMap
            playerCampfire[player] = clipboard

            player.queueDialogue(
                createFirstJoinDialogue(player)
            )

            campfireSpawner.spawn(player)
        }
    }

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