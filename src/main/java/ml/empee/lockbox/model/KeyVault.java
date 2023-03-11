package ml.empee.lockbox.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyVault extends Vault implements ConditionalVault {

  private static final NamespacedKey dataKey = new NamespacedKey(plugin, "vaultData");
  private static final NamespacedKey decorationKey = new NamespacedKey(plugin, "decorationOf");
  private static final NamespacedKey vaultAccesPointKey = new NamespacedKey(plugin, "accessPoint");

  private final int vaultId;
  private List<Entity> entities;
  private Entity dataEntity;

  public KeyVault(Block block) {
    super(block, Type.KEY);
    vaultId = computeId(block);
  }

  private List<Entity> findEntities() {
    return block.getLocation().add(0.5, 0.5, 0.5).getNearbyEntities(1, 2, 1).stream()
        .filter(e -> e.getPersistentDataContainer().has(decorationKey))
        .filter(e -> e.getPersistentDataContainer().get(decorationKey, PersistentDataType.INTEGER) == vaultId)
        .toList();
  }

  @Override
  public void spawnDecorations(BlockFace front) {
    if(entities != null || !findEntities().isEmpty()) {
      throw new IllegalStateException("Decorations already spawned");
    }

    if(front == BlockFace.UP || front == BlockFace.DOWN || front == BlockFace.SELF) {
      front = BlockFace.NORTH;
    }

    Location offset = new Location(block.getWorld(), front.getModX(), 0, front.getModZ());
    ArmorStand lid = (ArmorStand) block.getWorld().spawnEntity(
        offset.clone()
            .multiply(0.26)
            .add(block.getLocation())
            .add(0.5, -1.2, 0.5),
        EntityType.ARMOR_STAND
    );

    lid.getEquipment().setHelmet(new ItemStack(Material.QUARTZ_BLOCK));
    lid.setGravity(false);
    lid.setVisible(false);
    lid.setMarker(true);
    markAsDecoration(lid);

    ItemFrame frame = (ItemFrame) block.getWorld().spawnEntity(
        offset.clone().add(block.getLocation()), EntityType.ITEM_FRAME
    );

    frame.setFixed(true);
    frame.setRotation(Rotation.COUNTER_CLOCKWISE);
    frame.setItem(new ItemStack(Material.LEVER));
    frame.setFacingDirection(front, true);
    frame.getPersistentDataContainer().set(vaultAccesPointKey, PersistentDataType.BYTE, (byte) 1);
    markAsDecoration(frame);

    dataEntity = lid;
    dataEntity.getPersistentDataContainer().set(dataKey, PersistentDataType.BYTE, (byte) 1);
  }

  private void markAsDecoration(Entity entity) {
    PersistentDataContainer data = entity.getPersistentDataContainer();
    data.set(decorationKey, PersistentDataType.INTEGER, vaultId);
  }

  @Override
  public boolean isDecoration(Entity entity) {
    Integer decId = entity.getPersistentDataContainer().get(decorationKey, PersistentDataType.INTEGER);
    return decId != null && decId == vaultId;
  }

  @Override
  public void deleteDecorations() {
    if(entities == null) {
      entities = findEntities();
    }

    entities.forEach(Entity::remove);
  }

  protected PersistentDataContainer loadData() {
    if(dataEntity == null) {
      dataEntity = findEntities().stream()
          .filter(e -> e.getPersistentDataContainer().has(dataKey))
          .findFirst().orElseThrow(
          () -> new IllegalStateException("Unable to access vault data at " + block.getLocation() + " entity not existing")
      );
    }

    return dataEntity.getPersistentDataContainer();
  }

  @Override
  protected Inventory getDefaultInventory() {
    return Bukkit.createInventory(null, InventoryType.DISPENSER);
  }

  @Override
  public boolean shouldOpen(@Nullable Location location, @Nullable Entity entity) {
    if(entity == null) {
      return false;
    }

    return entity.getPersistentDataContainer().has(vaultAccesPointKey);
  }
}
