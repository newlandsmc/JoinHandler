package me.cookie.joinhandler.commands

import com.sk89q.worldedit.math.Vector3
import me.cookie.cookiecore.formatMinimessage
import me.cookie.cookiecore.formatPlayerPlaceholders
import me.cookie.cookiecore.message.dialogue.Dialogue
import me.cookie.cookiecore.message.dialogue.queueDialogue
import me.cookie.cookiecore.message.messagequeueing.MessageReceiver
import me.cookie.cookiecore.message.messagequeueing.QueuedMessage
import me.cookie.joinhandler.StarterCampfire
import me.cookie.joinhandler.listeners.oldBlocks
import me.cookie.joinhandler.listeners.playerCampfire
import me.cookie.joinhandler.listeners.playerOffsets
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class SpawnCampfire(private val plugin: JavaPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) return true
        val player = Bukkit.getPlayer(args[0])
        if(player == null){
            plugin.logger.warning("Tried to place campfire on a null player.")
            return true
        }
        if (!player.hasPlayedBefore() || plugin.config.getBoolean("debug-spawn")) {
            val campfireSpawner = StarterCampfire()
            val clipboard = campfireSpawner.clipboard
            val offsets = campfireSpawner.getPlayerOffsets(player)

            val locationTypeMap = HashMap<Vector3, Material>()
            for (i in
            0..offsets.maximumPoint.blockX - offsets.minimumPoint.blockX) {
                for (j in
                0..offsets.maximumPoint.blockY -
                        offsets.minimumPoint.blockY) {
                    for (k in
                    0..offsets.maximumPoint.blockZ -
                            offsets.minimumPoint.blockZ) {
                        val block: Block =
                            player.world.getBlockAt(
                                i + offsets.minimumPoint.blockX,
                                j + offsets.minimumPoint.blockY,
                                k + offsets.minimumPoint.blockZ
                            )
                        locationTypeMap[
                                Vector3.at(
                                    i.toDouble(),
                                    j.toDouble(),
                                    k.toDouble()
                                )] = block.type
                    }
                }
            }

            oldBlocks[player] = locationTypeMap
            playerCampfire[player] = clipboard
            playerOffsets[player] = offsets
            if (plugin.config.getBoolean("enable-dialogue"))
                player.queueDialogue(createFirstJoinDialogue(player))

            campfireSpawner.spawn(player)
            player.setBedSpawnLocation(player.location, true)
        }
        return true
    }

    private fun createFirstJoinDialogue(player: Player): Dialogue {
        val messageList = mutableListOf<QueuedMessage>()
        val messageConfigSection =
            plugin.config.getConfigurationSection("first-join-dialogue")!!.getKeys(false)

        for ((i, message) in messageConfigSection.withIndex()) {
            var delay = plugin.config.getInt(
                "first-join-dialogue.${messageConfigSection.toTypedArray()[0]}.delay"
            ) + 1
            for (j in 0 until i) {
                delay +=
                    plugin.config.getInt(
                        "first-join-dialogue.${messageConfigSection.toTypedArray()[j+1]}.delay"
                    ) + 1
            }
            messageList.add(
                QueuedMessage(
                    message = "first-join-dialogue.${message}.message".formatMinimessage(),
                    receiver = MessageReceiver.PLAYER,
                    System.currentTimeMillis() + (delay.toLong() * 1000),
                    playerToSend = player
                )
            )
        }

        return Dialogue(
            messages = messageList,
            receiver = MessageReceiver.PLAYER,
            whenToSend = System.currentTimeMillis(),
            muteForAfter = plugin.config.getInt(
                "first-join-dialogue.${messageConfigSection.last()}.mute-chat-for"
            ),
            toRun =  {
                object: BukkitRunnable() {
                    override fun run() {
                        val otherPlayer = Bukkit.getPlayer(player.uniqueId) ?: return // Refresh player
                        if(!otherPlayer.hasPlayedBefore()){
                            Bukkit.getServer().onlinePlayers.forEach { receiver ->
                                receiver.sendMessage(
                                    plugin.config.getString("first-join")!!
                                        .formatPlayerPlaceholders(player)
                                        .formatMinimessage()
                                )
                            }
                        }

                    }
                }.runTaskLater(plugin, 5)
            }
        )
    }
}
