package me.cookie.joinhandler.listeners

import me.cookie.joinhandler.JoinHandler
import me.cookie.semicore.message.dialogue.Dialogue
import me.cookie.semicore.message.dialogue.queueDialogue
import me.cookie.semicore.message.messagequeueing.MessageReceiver
import me.cookie.semicore.message.messagequeueing.QueuedMessage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(JoinHandler::class.java)
    @EventHandler
    fun onPlayerJoin(event: PlayerSpawnLocationEvent /* Use spawn event for better time accuracy */ ){
        val player = event.player
        if(!player.hasPlayedBefore()){
            player.queueDialogue(
                createFirstJoinDialogue(player)
            )
        }else{
            //TODO send other join dialogue
        }
    }

    private fun createFirstJoinDialogue(player: Player): Dialogue{
        val messageList = mutableListOf<QueuedMessage>()
        val messageConfigSection = plugin.config.getConfigurationSection("first-join")!!.getKeys(false)

        for ((i, message) in messageConfigSection.withIndex()){
            var delay = 0
            for(j in 0 until i){
                delay += plugin.config.getInt("first-join.${messageConfigSection.toTypedArray()[j+1]}.delay")
            }
            messageList.add(
                QueuedMessage(
                    message = MiniMessage.get().parse(
                        plugin.config.getString("first-join.${message}.message")!!
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