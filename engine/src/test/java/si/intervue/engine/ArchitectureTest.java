package si.intervue.engine;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Layer rules from PLAN.md 5.3; each rule names the dependency it forbids. */
@AnalyzeClasses(
    packages = "si.intervue.engine",
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  // Ports and use cases never reach up into the HTTP layer.
  @ArchTest
  static final ArchRule nothingBelowApiDependsOnApi =
      noClasses()
          .that()
          .resideOutsideOfPackage("si.intervue.engine.api..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("si.intervue.engine.api..")
          .allowEmptyShould(true);
}
