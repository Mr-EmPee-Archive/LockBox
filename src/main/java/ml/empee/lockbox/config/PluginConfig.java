package ml.empee.lockbox.config;

import lombok.Getter;
import lombok.Setter;
import ml.empee.configurator.Configuration;
import ml.empee.configurator.annotations.Path;
import ml.empee.ioc.Bean;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

@Getter
public class PluginConfig extends Configuration implements Bean {

  @Path("lang")
  private Locale language;

  private void setLanguage(String language) {
    String[] lang = language.split("_");
    if(lang.length != 2) {
      throw new IllegalArgumentException("Invalid language: " + language);
    }

    this.language = new Locale(lang[0], lang[1]);
  }

  public PluginConfig(JavaPlugin plugin) {
    super(plugin, "config.yml", 1);
  }
}
