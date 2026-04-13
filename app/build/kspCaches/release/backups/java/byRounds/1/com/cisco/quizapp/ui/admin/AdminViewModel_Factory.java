package com.cisco.quizapp.ui.admin;

import android.content.Context;
import com.cisco.quizapp.data.repository.QuizRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AdminViewModel_Factory implements Factory<AdminViewModel> {
  private final Provider<QuizRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  public AdminViewModel_Factory(Provider<QuizRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public AdminViewModel get() {
    return newInstance(repositoryProvider.get(), contextProvider.get());
  }

  public static AdminViewModel_Factory create(Provider<QuizRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    return new AdminViewModel_Factory(repositoryProvider, contextProvider);
  }

  public static AdminViewModel newInstance(QuizRepository repository, Context context) {
    return new AdminViewModel(repository, context);
  }
}
