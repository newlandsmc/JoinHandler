package me.cookie.joinhandler

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.Location
import com.sk89q.worldedit.world.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream


class StarterCampfire {
    private val plugin = JavaPlugin.getPlugin(JoinHandler::class.java)
    private val schematicFile = File("${plugin.dataFolder}/schematics/newplayerspawn.schem")
    val clipboard = makeClipboard()

    fun getPlayerOffsets(player: Player): CuboidRegion{
        val region = clipboard.region
        val location: Location = BukkitAdapter.adapt(player.location)
        val clipboardOffset = region.minimumPoint.subtract(clipboard.origin)
        val realTo = location.toVector().add(clipboardOffset.toVector3())
        val max =
            realTo.add(region.maximumPoint.subtract(region.minimumPoint).toVector3())

        return CuboidRegion(realTo.toBlockPoint(), max.toBlockPoint())
    }

    fun spawn(player: Player){
        if(!schematicFile.exists()){
            plugin.logger.severe("Starter schematic doesnt exist...")
            schematicFile.mkdir()
            return
        }
        val world: World = BukkitAdapter.adapt(player.world)
        val location: Location = BukkitAdapter.adapt(player.location)


        val session = WorldEdit.getInstance().newEditSession(world)
        val operation: Operation = ClipboardHolder(clipboard).createPaste(session)
            .to(location.toVector().toBlockPoint()).ignoreAirBlocks(false).build()

        try {
            Operations.complete(operation)
            session.close()
        } catch (exception: WorldEditException) {
            exception.printStackTrace()
        }


    }

    private fun makeClipboard(): Clipboard{
        val format = ClipboardFormats.findByFile(schematicFile)
        format!!.getReader(FileInputStream(schematicFile)).use { reader -> return reader.read() }
    }
}