package com.intellij.tasks.integration;

import javax.annotation.Nonnull;
import com.intellij.tasks.TaskManagerTestCase;
import com.intellij.tasks.generic.GenericRepository;
import com.intellij.tasks.generic.GenericRepositoryType;

/**
 * @author Mikhail Golubev
 */
public abstract class GenericSubtypeTestCase extends TaskManagerTestCase {
  protected GenericRepository myRepository;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    final GenericRepositoryType genericType = new GenericRepositoryType();
    myRepository = createRepository(genericType);
  }

  @Nonnull
  protected abstract GenericRepository createRepository(GenericRepositoryType genericType);
}
