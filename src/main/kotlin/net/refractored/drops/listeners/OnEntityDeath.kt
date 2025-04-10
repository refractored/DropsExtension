package net.refractored.drops.listeners

import net.refractored.bloodmoonreloaded.registry.BloodmoonRegistry
import net.refractored.bloodmoonreloaded.types.implementation.BloodmoonWorld
import net.refractored.drops.DropsExtension
import net.refractored.drops.drops.DropsRegistry
import net.refractored.hordes.hordes.HordeRegistry
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class OnEntityDeath : Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun run(event: EntityDeathEvent) {
        val bloodWorld = BloodmoonRegistry.getWorld(event.entity.world.name) ?: return

        if (bloodWorld.status != BloodmoonWorld.Status.ACTIVE) {
            return
        }

        val dropsConfig = DropsRegistry.getConfig(event.entity.world) ?: return

        if (dropsConfig.entities != null && dropsConfig.entities.none { it.matches(event.entity) }) {
            return
        }

        if (dropsConfig.configSection.getBoolean("disable-vanilla-drops")) {
            event.drops.clear()
        }

        if (event.entity.location.y > dropsConfig.configSection.getInt("farm-prevention.max-y")) {
            return
        }

        if (dropsConfig.configSection.getBoolean("farm-prevention.killed-by-player") && event.entity.killer !is Player) {
            return
        }

        if (dropsConfig.configSection.getBoolean("farm-prevention.line-of-sight") &&
            event.entity.killer?.let(event.entity::hasLineOfSight) != true
        ) {
            return
        }

        val isHorde: Boolean = getHordeInfo(event.entity)

        val amount =
            (
                dropsConfig.configSection
                    .getInt("min-size")
                    .coerceAtLeast(0)..dropsConfig.configSection.getInt("max-size").coerceAtLeast(0)
            ).random()

        for (i in 0..amount) {
            event.entity.world.dropItemNaturally(
                event.entity.location,
                dropsConfig.getWeightedRandomItemstack(isHorde),
            )
        }
    }

    fun getHordeInfo(entity: Entity): Boolean {
        if (DropsExtension.instance.hordes == null) return false
        val config = HordeRegistry.getHordeConfig(entity.world) ?: return false
        return entity.persistentDataContainer.has(config.pdcKey)
    }
}
