package ml.empee.lockbox.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import ml.empee.lockbox.model.decorations.VaultDecorator;
import ml.empee.lockbox.model.protections.VaultProtection;
import ml.empee.lockbox.registries.VaultRegistry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Vault {
  @Getter
  private final UUID uuid;
  @Getter
  private final VaultRegistry.Type type;
  @Getter
  private final Block block;

  @Delegate
  protected VaultProtection securitySystem;
  @Delegate
  protected VaultDecorator decorator;

  public abstract Inventory getInventoryTemplate();

  public Location getLocation() {
    return block.getLocation();
  }
}
