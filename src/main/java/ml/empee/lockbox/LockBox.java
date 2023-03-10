package ml.empee.lockbox;

import lombok.Getter;
import ml.empee.ioc.SimpleIoC;

/** Boot class of this plugin. **/

public final class LockBox extends org.bukkit.plugin.java.JavaPlugin {

  public static final String PREFIX = "  &bLockBox &8&l»&r ";
  private static final String SPIGOT_PLUGIN_ID = "";
  private static final Integer METRICS_PLUGIN_ID = 0;
  @Getter
  private final SimpleIoC iocContainer = new SimpleIoC(this);

  public void onEnable() {
    iocContainer.initialize("relocations");

    //Metrics.of(this, METRICS_PLUGIN_ID);
    //Notifier.listenForUpdates(this, SPIGOT_PLUGIN_ID);
  }

  public void onDisable() {
    iocContainer.removeAllBeans();
  }
}
