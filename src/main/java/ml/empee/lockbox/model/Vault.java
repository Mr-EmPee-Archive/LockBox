package ml.empee.lockbox.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ml.empee.lockbox.utils.ItemSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

/** Represent a lock **/

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Vault {

  protected static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Vault.class);
  protected static final NamespacedKey inventoryKey = new NamespacedKey(plugin, "inventory");

  protected final Block block;
  protected final Type type;

  private PersistentDataContainer data;
  private Inventory inventory;

  /**
   * Compute the id that a lock applies to the given block will have
   **/
  public static int computeId(Block block) {
    return (block.getWorld().getName() + "W" + block.getX() + "X" + block.getY() + "Y" + block.getZ()).hashCode();
  }

  public static Vault buildVault(Block block, Type type) {
    switch (type) {
      case KEY -> {
        return new KeyVault(block);
      }

      default -> throw new UnsupportedOperationException("Vault not implemented!");
    }
  }

  /** Create the vault by spawning necessary entities **/
  public abstract void spawnDecorations(BlockFace front);
  /** Delete the vault by removing used entities **/
  public abstract void deleteDecorations();
  public abstract boolean isDecoration(Entity entity);

  public PersistentDataContainer getData() {
    if(data == null) {
      data = loadData();
    }

    return data;
  }
  protected abstract PersistentDataContainer loadData();

  protected abstract Inventory getDefaultInventory();
  @SneakyThrows
  protected Inventory loadInventory() {
    Inventory inventory = getDefaultInventory();

    PersistentDataContainer data = getData();
    String encodedInventory = data.get(inventoryKey, PersistentDataType.STRING);
    if(encodedInventory != null) {
      inventory.addItem(
          ItemSerializer.inventoryFromBase64(encodedInventory)
      );
    }

    return inventory;
  }
  @SneakyThrows
  protected void saveInventory() {
    if(inventory == null) {
      return;
    }

    PersistentDataContainer data = getData();
    String encodedInventory = ItemSerializer.toBase64(inventory.getContents());
    data.set(inventoryKey, PersistentDataType.STRING, encodedInventory);
  }
  public Inventory getInventory() {
    if(inventory == null) {
      loadInventory();
    }

    return inventory;
  }

    /** Existing type of a lock **/
  public enum Type {
    KEY, PIN, BIOMETRIC;
  }
}
