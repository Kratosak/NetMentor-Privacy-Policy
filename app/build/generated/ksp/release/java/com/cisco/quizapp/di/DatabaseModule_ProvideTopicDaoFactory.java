package com.cisco.quizapp.di;

import com.cisco.quizapp.data.local.QuizDatabase;
import com.cisco.quizapp.data.local.dao.TopicDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideTopicDaoFactory implements Factory<TopicDao> {
  private final Provider<QuizDatabase> dbProvider;

  public DatabaseModule_ProvideTopicDaoFactory(Provider<QuizDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TopicDao get() {
    return provideTopicDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTopicDaoFactory create(Provider<QuizDatabase> dbProvider) {
    return new DatabaseModule_ProvideTopicDaoFactory(dbProvider);
  }

  public static TopicDao provideTopicDao(QuizDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTopicDao(db));
  }
}
