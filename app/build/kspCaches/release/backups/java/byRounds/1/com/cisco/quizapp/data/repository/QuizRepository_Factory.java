package com.cisco.quizapp.data.repository;

import com.cisco.quizapp.data.local.dao.QuestionDao;
import com.cisco.quizapp.data.local.dao.TopicDao;
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
public final class QuizRepository_Factory implements Factory<QuizRepository> {
  private final Provider<TopicDao> topicDaoProvider;

  private final Provider<QuestionDao> questionDaoProvider;

  public QuizRepository_Factory(Provider<TopicDao> topicDaoProvider,
      Provider<QuestionDao> questionDaoProvider) {
    this.topicDaoProvider = topicDaoProvider;
    this.questionDaoProvider = questionDaoProvider;
  }

  @Override
  public QuizRepository get() {
    return newInstance(topicDaoProvider.get(), questionDaoProvider.get());
  }

  public static QuizRepository_Factory create(Provider<TopicDao> topicDaoProvider,
      Provider<QuestionDao> questionDaoProvider) {
    return new QuizRepository_Factory(topicDaoProvider, questionDaoProvider);
  }

  public static QuizRepository newInstance(TopicDao topicDao, QuestionDao questionDao) {
    return new QuizRepository(topicDao, questionDao);
  }
}
