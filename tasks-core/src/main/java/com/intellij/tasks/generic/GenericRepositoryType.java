package com.intellij.tasks.generic;

import com.intellij.tasks.TasksIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskRepository;
import consulo.task.TaskRepositorySubtype;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;
import consulo.util.jdom.JDOMUtil;
import consulo.util.xml.serializer.XmlSerializer;
import org.jdom.Document;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * User: Evgeny.Zakrevsky
 * Date: 10/4/12
 */
@ExtensionImpl
public class GenericRepositoryType extends BaseRepositoryType<GenericRepository> {

  @Nonnull
  @Override
  public String getId() {
    return "Generic";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.localizeTODO("Generic");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return PlatformIconGroup.nodesPpweb();
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new GenericRepository(this);
  }

  @Override
  public Class<GenericRepository> getRepositoryClass() {
    return GenericRepository.class;
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(final GenericRepository repository,
                                           final Project project,
                                           final Consumer<GenericRepository> changeListener) {
    return new GenericRepositoryEditor<>(project, repository, changeListener);
  }

  @Override
  public List<TaskRepositorySubtype> getAvailableSubtypes() {
    return Arrays.asList(
      this,
      new AsanaRepository(),
      new AssemblaRepository(),
      new SprintlyRepository()
    );
  }

  public class GenericSubtype implements TaskRepositorySubtype {
    private final String myId;
    private final Image myIcon;

    GenericSubtype(String id, Image icon) {
      myId = id;
      myIcon = icon;
    }

    @Nonnull
    @Override
    public String getId() {
      return myId;
    }

    @Nonnull
    @Override
    public LocalizeValue getPresentableName() {
      return LocalizeValue.localizeTODO(myId + " [G]");
    }

    @Nonnull
    @Override
    public Image getIcon() {
      return myIcon;
    }

    @Override
    public TaskRepository createRepository() {
      Document document;
      try {
        String configFileName = myId.toLowerCase() + ".xml";
        //URL resourceUrl = ResourceUtil.getResource(GenericRepositoryType.class, "connectors", configFileName);
        URL resourceUrl = GenericRepository.class.getResource("connectors/" + configFileName);
        if (resourceUrl == null) {
          throw new AssertionError("Repository configuration file '" + configFileName + "' not found");
        }
        document = JDOMUtil.loadResourceDocument(resourceUrl);
      }
      catch (Exception e) {
        throw new AssertionError(e);
      }
      GenericRepository repository = XmlSerializer.deserialize(document.getRootElement(), GenericRepository.class);
      if (repository != null) {
        repository.setRepositoryType(GenericRepositoryType.this);
        repository.setSubtypeId(getId());
      }
      return repository;
    }
  }

  // Subtypes:
  public final class AsanaRepository extends GenericSubtype {
    public AsanaRepository() {
      super("Asana", TasksIcons.Asana);
    }
  }

  public final class AssemblaRepository extends GenericSubtype {
    public AssemblaRepository() {
      super("Assembla", TasksIcons.Assembla);
    }
  }

  public final class SprintlyRepository extends GenericSubtype {
    public SprintlyRepository() {
      super("Sprintly", TasksIcons.Sprintly);
    }
  }
}
