package com.cisco.quizapp;

import com.cisco.quizapp.data.local.DatabaseSeeder;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class CiscoQuizApp_MembersInjector implements MembersInjector<CiscoQuizApp> {
  private final Provider<DatabaseSeeder> databaseSeederProvider;

  public CiscoQuizApp_MembersInjector(Provider<DatabaseSeeder> databaseSeederProvider) {
    this.databaseSeederProvider = databaseSeederProvider;
  }

  public static MembersInjector<CiscoQuizApp> create(
      Provider<DatabaseSeeder> databaseSeederProvider) {
    return new CiscoQuizApp_MembersInjector(databaseSeederProvider);
  }

  @Override
  public void injectMembers(CiscoQuizApp instance) {
    injectDatabaseSeeder(instance, databaseSeederProvider.get());
  }

  @InjectedFieldSignature("com.cisco.quizapp.CiscoQuizApp.databaseSeeder")
  public static void injectDatabaseSeeder(CiscoQuizApp instance, DatabaseSeeder databaseSeeder) {
    instance.databaseSeeder = databaseSeeder;
  }
}
