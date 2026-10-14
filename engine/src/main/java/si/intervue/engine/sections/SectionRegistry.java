package si.intervue.engine.sections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The installed section packages, found by {@link ServiceLoader}; packages speaking another SPI
 * version are left out.
 */
public final class SectionRegistry {

  private final Map<String, SectionPackage> byId;

  private SectionRegistry(Map<String, SectionPackage> byId) {
    this.byId = byId;
  }

  public static SectionRegistry load() {
    Map<String, SectionPackage> byId = new LinkedHashMap<>();
    for (SectionPackage pkg : ServiceLoader.load(SectionPackage.class)) {
      if (pkg.definition().spiVersion() == SectionPackage.SPI_VERSION) {
        byId.putIfAbsent(pkg.definition().id(), pkg);
      }
    }
    return new SectionRegistry(byId);
  }

  public Optional<SectionPackage> find(String id) {
    return Optional.ofNullable(byId.get(id));
  }
}
