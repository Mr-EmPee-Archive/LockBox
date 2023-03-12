package ml.empee.lockbox.controllers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
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
  private final VaultRegistry vaultRegistry;

  @Override
  public void onStart() {
    commandsConfig.register(this);
  }

  @CommandMethod("vault give <type> [target]")
  public void giveVault(
      CommandSender sender,
      @Argument VaultRegistry.Type type,
      @Argument @Nullable Player target
  ) {
    if (target == null) {
      if (sender instanceof Player) {
        target = (Player) sender;
      } else {
        Logger.log(sender, "&cYou must select a player");
        return;
      }
    }

    ItemStack lock = vaultRegistry.findItemByType(type).build();

    boolean hasSucceed = target.getInventory().addItem(lock).isEmpty();
    if (!hasSucceed) {
      Logger.log(sender,
          "&cUnable to give vault to &e%s&e his inventory was full", target.getName()
      );
      return;
    }

    Logger.log(sender, "&7Vault given to &e%s", target.getName());
  }

}
