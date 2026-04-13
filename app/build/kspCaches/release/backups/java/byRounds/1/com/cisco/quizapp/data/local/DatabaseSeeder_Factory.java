package com.cisco.quizapp.data.local;

import com.cisco.quizapp.data.repository.QuizRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DatabaseSeeder_Factory implements Factory<DatabaseSeeder> {
  private final Provider<QuizRepository> repositoryProvider;

  public DatabaseSeeder_Factory(Provider<QuizRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DatabaseSeeder get() {
    return newInstance(repositoryProvider.get());
  }

  public static DatabaseSeeder_Factory create(Provider<QuizRepository> repositoryProvider) {
    return new DatabaseSeeder_Factory(repositoryProvider);
  }

  public static DatabaseSeeder newInstance(QuizRepository repository) {
    return new DatabaseSeeder(repository);
  }
}
