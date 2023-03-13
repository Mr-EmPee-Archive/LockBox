package ml.empee.lockbox.events.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.events.VaultPlacedEvent;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.items.VaultItem;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.services.VaultService;
import ml.empee.lockbox.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class VaultPlacingListener implements RegisteredListener, Bean {

  private final VaultService vaultService;
  private final VaultRegistry vaultRegistry;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onVaultPlace(BlockPlaceEvent event) {
    ItemStack item = event.getItemInHand();

    VaultItem vaultItem = vaultRegistry.findVaultItem(item).orElse(null);
    if(vaultItem == null) {
      return;
    }

    BlockFace front = LocationUtils.getFaceInFront(
        event.getPlayer().getLocation().getBlock(), event.getBlock()
    );

    Vault vault = vaultService.createVault(event.getBlock(), front, vaultItem.getType());
    Bukkit.getPluginManager().callEvent(new VaultPlacedEvent(event.getPlayer(), vault));
  }

}
