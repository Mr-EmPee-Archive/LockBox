package ml.empee.lockbox.model.decorations;

import ml.empee.lockbox.model.Vault;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

public class RealisticDecorator extends VaultDecorator {
  public RealisticDecorator(Vault vault) {
    super(vault);
  }

  @Override
  public void spawnDecorations(BlockFace front) {
    if(!front.name().matches("EAST|NORTH|WEST|SOUTH")) {
      front = BlockFace.NORTH;
    }

    Location location = vault.getLocation();
    World world = location.getWorld();

    Location offset = new Location(world, front.getModX(), 0, front.getModZ());
    ArmorStand lid = (ArmorStand) world.spawnEntity(
        offset.clone()
            .multiply(0.26)
            .add(location)
            .add(0.5, -1.2, 0.5),
        EntityType.ARMOR_STAND
    );

    lid.getEquipment().setHelmet(new ItemStack(Material.QUARTZ_BLOCK));
    lid.setGravity(false);
    lid.setVisible(false);
    lid.setMarker(true);
    markAsDecoration(lid);

    ItemFrame frame = (ItemFrame) world.spawnEntity(
        offset.clone().add(location), EntityType.ITEM_FRAME
    );

    frame.setFixed(true);
    frame.setRotation(Rotation.COUNTER_CLOCKWISE);
    frame.setItem(new ItemStack(Material.LEVER));
    frame.setFacingDirection(front, true);
    markAsDecoration(frame);
  }
}
