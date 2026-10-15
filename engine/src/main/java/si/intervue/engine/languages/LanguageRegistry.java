package si.intervue.engine.languages;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The installed language packages, found by {@link ServiceLoader}; packages speaking another SPI
 * version are left out so the engine never calls a contract it does not know.
 */
public final class LanguageRegistry {

  private final Map<String, LanguagePackage> byId;

  private LanguageRegistry(Map<String, LanguagePackage> byId) {
    this.byId = byId;
  }

  public static LanguageRegistry load() {
    Map<String, LanguagePackage> byId = new LinkedHashMap<>();
    for (LanguagePackage pkg : ServiceLoader.load(LanguagePackage.class)) {
      if (pkg.definition().spiVersion() == LanguagePackage.SPI_VERSION) {
        byId.putIfAbsent(pkg.definition().id(), pkg);
      }
    }
    return new LanguageRegistry(byId);
  }

  public Optional<LanguagePackage> find(String id) {
    return Optional.ofNullable(byId.get(id));
  }

  /** Every installed package, in discovery order. */
  public Collection<LanguagePackage> all() {
    return byId.values();
  }
}
