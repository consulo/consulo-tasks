/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.tasks.trello.model;

import jakarta.annotation.Nonnull;

import consulo.util.xml.serializer.annotation.Attribute;
import consulo.util.xml.serializer.annotation.Tag;

/**
 * @author Mikhail Golubev
 */

@Tag("TrelloUser")
public class TrelloUser extends TrelloModel
{

	public static final String REQUIRED_FIELDS = "username";

	private String username;

	/**
	 * Serialization constructor
	 */
	@SuppressWarnings("UnusedDeclaration")
	public TrelloUser()
	{
	}

	@Override
	public String toString()
	{
		return String.format("TrelloUser(id='%s' username='%s')", getId(), username);
	}

	@Nonnull
	@Attribute("name")
	@Override
	public String getName()
	{
		return getUsername();
	}

	@Override
	public void setName(@Nonnull String name)
	{
		username = name;
	}

	@Nonnull
	public String getUsername()
	{
		return username;
	}
}
