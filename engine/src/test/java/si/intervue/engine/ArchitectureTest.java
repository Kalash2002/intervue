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

  // Packages are found through the SPI at run time; the engine never names one.
  @ArchTest
  static final ArchRule engineNeverNamesAPackage =
      noClasses()
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("si.intervue.languages..", "si.intervue.sections..");

  // The SPIs, the content port and the process runner sit below the use cases and the store.
  @ArchTest
  static final ArchRule contentAndLanguagesStayBelowSessions =
      noClasses()
          .that()
          .resideInAnyPackage(
              "si.intervue.engine.content..",
              "si.intervue.engine.languages..",
              "si.intervue.engine.sections..",
              "si.intervue.engine.runners..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("si.intervue.engine.session..", "si.intervue.engine.store..");
}
