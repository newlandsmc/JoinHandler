package me.cookie.joinhandler.listeners

import me.cookie.joinhandler.JoinHandler
import me.cookie.joinhandler.StarterCampfire
import me.cookie.semicore.formatPlayerPlaceholders
import me.cookie.semicore.message.dialogue.Dialogue
import me.cookie.semicore.message.dialogue.queueDialogue
import me.cookie.semicore.message.messagequeueing.MessageReceiver
import me.cookie.semicore.message.messagequeueing.QueuedMessage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoin: Listener {
    private val plugin = JavaPlugin.getPlugin(JoinHandler::class.java)
    @EventHandler
    fun onPlayerSpawn(event: PlayerJoinEvent){
        val player = event.player
        if(player.hasPlayedBefore()){
            // First join logic, spawn campfire
            event.joinMessage(
                MiniMessage.get().parse(
                    plugin.config.getString("first-join")!!.formatPlayerPlaceholders(player)
                )
            )

            player.queueDialogue(
                createFirstJoinDialogue(player)
            )
        }

        StarterCampfire().spawn(player)
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