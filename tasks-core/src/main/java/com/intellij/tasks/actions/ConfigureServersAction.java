package com.intellij.tasks.actions;

import javax.annotation.Nonnull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.tasks.config.TaskRepositoriesConfigurable;
import consulo.annotations.RequiredDispatchThread;

/**
 * User: Evgeny Zakrevsky
 */

public class ConfigureServersAction extends BaseTaskAction
{
	public ConfigureServersAction()
	{
		super("Configure Servers...", null, AllIcons.General.Settings);
	}

	@RequiredDispatchThread
	@Override
	public void actionPerformed(@Nonnull AnActionEvent e)
	{
		TaskRepositoriesConfigurable configurable = new TaskRepositoriesConfigurable(getProject(e));
		if(ShowSettingsUtil.getInstance().editConfigurable(getProject(e), configurable))
		{
			serversChanged();
		}
	}

	protected void serversChanged()
	{

	}
}
