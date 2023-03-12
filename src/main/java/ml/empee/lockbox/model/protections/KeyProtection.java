package ml.empee.lockbox.model.protections;

import ml.empee.lockbox.model.Vault;
import org.bukkit.entity.Player;

public class KeyProtection extends VaultProtection {

  public KeyProtection(Vault vault) {
    super(vault);
  }

  @Override
  public boolean hasAccess(Player player) {
    //TODO
    return true;
  }
}
