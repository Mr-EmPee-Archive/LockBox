package ml.empee.lockbox.events.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.model.Vaults;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.services.VaultService;
import ml.empee.lockbox.utils.LocationUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class VaultPlacingListener implements RegisteredListener, Bean {

  private final VaultService vaultService;
  private final VaultRegistry vaultRegistry;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    ItemStack item = event.getItemInHand();

    Vaults.Type type = vaultRegistry.findVaultTypeByItem(item).orElse(null);
    if(type == null) {
      return;
    }

    onVaultPlace(event.getPlayer(), event.getBlock(), type);
  }

  public void onVaultPlace(Player player, Block block, Vaults.Type type) {
    vaultService.createVault(block, LocationUtils.getFaceInFront(player.getLocation().getBlock(), block), type);
  }

}
