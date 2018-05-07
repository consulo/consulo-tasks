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

import static com.intellij.tasks.trello.model.TrelloLabel.LabelColor;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.Attribute;

/**
 * @author Mikhail Golubev
 */
@SuppressWarnings("UnusedDeclaration")
public class TrelloCard extends TrelloModel
{

	public static final String REQUIRED_FIELDS = "closed,desc,idMembers,idBoard,idList,labels,name,url,dateLastActivity";

	private String idBoard, idList;
	private List<String> idMembers;
	private String name;
	@SerializedName("desc")
	private String description;
	private String url;
	private boolean closed;
	private Date dateLastActivity;
	private List<TrelloLabel> labels;
	@SerializedName("actions")
	private List<TrelloCommentAction> comments = ContainerUtil.emptyList();
	/**
	 * This field is not part of card representation downloaded from server
	 * and set explicitly in {@code com.intellij.tasks.trello.TrelloRepository}
	 */
	private boolean isVisible = true;

	/**
	 * Serialization constructor
	 */
	@SuppressWarnings("UnusedDeclaration")
	public TrelloCard()
	{
	}

	@Override
	public String toString()
	{
		return String.format("TrelloCard(id='%s', name='%s')", getId(), name);
	}

	@Nonnull
	public String getIdBoard()
	{
		return idBoard;
	}

	@Nonnull
	public String getIdList()
	{
		return idList;
	}

	@Nonnull
	public List<String> getIdMembers()
	{
		return idMembers;
	}

	@Nonnull
	@Attribute("name")
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(@Nonnull String name)
	{
		this.name = name;
	}

	@Nonnull
	public String getDescription()
	{
		return description;
	}

	@Nonnull
	public String getUrl()
	{
		return url;
	}

	public boolean isClosed()
	{
		return closed;
	}

	@Nonnull
	public List<TrelloLabel> getLabels()
	{
		return labels;
	}

	@Nonnull
	public List<TrelloCommentAction> getComments()
	{
		return comments;
	}

	/**
	 * @return colors of labels with special {@link LabelColor#NO_COLOR} value excluded
	 */
	@Nonnull
	public Set<LabelColor> getColors()
	{
		if(labels == null || labels.isEmpty())
		{
			return EnumSet.noneOf(LabelColor.class);
		}
		return EnumSet.copyOf(ContainerUtil.mapNotNull(labels, new Function<TrelloLabel, LabelColor>()
		{
			@Override
			public LabelColor fun(TrelloLabel label)
			{
				final LabelColor color = label.getColor();
				return color == LabelColor.NO_COLOR ? null : color;
			}
		}));
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisible(boolean visible)
	{
		isVisible = visible;
	}

	@Nullable
	public Date getDateLastActivity()
	{
		return dateLastActivity;
	}
}
