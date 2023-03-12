package ml.empee.lockbox.model;

import ml.empee.lockbox.model.decorations.RealisticDecorator;
import ml.empee.lockbox.model.protections.KeyProtection;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class Vaults {
  public static Vault buildVault(Block block, Type type) {
    switch (type) {
      case KEY -> {
        return new KeyVault(block);
      }

      default -> throw new UnsupportedOperationException("Vault not existing!");
    }
  }

  public static class KeyVault extends Vault {
    public KeyVault(Block block) {
      super(Type.KEY, block);

      decorator = new RealisticDecorator(this);
      securitySystem = new KeyProtection(this);
    }

    public Inventory getInventoryTemplate() {
      return Bukkit.createInventory(null, InventoryType.DISPENSER);
    }
  }

  public enum Type {
    KEY, PIN, BIOMETRIC
  }
}
