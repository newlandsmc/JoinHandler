package me.cookie.joinhandler

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.session.ClipboardHolder
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream


class StarterCampfire {
    private val plugin = JavaPlugin.getPlugin(JoinHandler::class.java)
    private val schematic = File("${plugin.dataFolder}/schematics/starterSchematic.schematic")
    fun spawn(player: Player){
        if(!schematic.exists()){
            plugin.logger.severe("Starter schematic doesnt exist...")
            schematic.mkdir()
            return
        }

        var clipboard: Clipboard
        val pLoc = player.location
        val format = ClipboardFormats.findByFile(schematic)
        format!!.getReader(FileInputStream(schematic)).use { reader -> clipboard = reader.read() }
        val WELoc = BukkitAdapter.adapt(pLoc)
        val editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(pLoc.world))

        val operation = ClipboardHolder(clipboard)
            .createPaste(editSession)
            .to(WELoc.toVector().toBlockPoint())
            .ignoreAirBlocks(false)
            .build()

        Operations.complete(operation)
    }
}