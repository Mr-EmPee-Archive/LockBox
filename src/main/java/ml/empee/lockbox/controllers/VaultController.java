package ml.empee.lockbox.controllers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Vaults;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.config.CommandsConfig;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Controller used for managing the plugin. **/
@RequiredArgsConstructor
public class VaultController implements Bean {

  private final CommandsConfig commandsConfig;
  private final Logger logger;
  private final VaultRegistry vaultRegistry;

  @Override
  public void onStart() {
    commandsConfig.register(this);
  }

  @CommandMethod("vault give <type> [target]")
  public void giveVault(
      CommandSender sender,
      @Argument Vaults.Type type,
      @Argument @Nullable Player target
  ) {
    if (target == null) {
      if (sender instanceof Player) {
        target = (Player) sender;
      } else {
        logger.log(sender, "You must select a player");
        return;
      }
    }

    ItemStack lock = vaultRegistry.findItemByType(type).build();

    boolean hasSucceed = target.getInventory().addItem(lock).isEmpty();
    if (!hasSucceed) {
      logger.log(sender,
          "Unable to give vault to &e%s&r his inventory was full", target.getName()
      );
      return;
    }

    logger.log(sender, "Vault given to &e%s", target.getName());
  }

}
