package com.cisco.quizapp.ui.result;

import androidx.lifecycle.SavedStateHandle;
import com.cisco.quizapp.data.repository.QuizRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ResultViewModel_Factory implements Factory<ResultViewModel> {
  private final Provider<QuizRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ResultViewModel_Factory(Provider<QuizRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ResultViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ResultViewModel_Factory create(Provider<QuizRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ResultViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static ResultViewModel newInstance(QuizRepository repository,
      SavedStateHandle savedStateHandle) {
    return new ResultViewModel(repository, savedStateHandle);
  }
}
