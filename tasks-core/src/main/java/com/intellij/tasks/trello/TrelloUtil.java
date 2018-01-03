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

package com.intellij.tasks.trello;

import static com.intellij.tasks.trello.model.TrelloLabel.LabelColor;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.trello.model.TrelloBoard;
import com.intellij.tasks.trello.model.TrelloCard;
import com.intellij.tasks.trello.model.TrelloCommentAction;
import com.intellij.tasks.trello.model.TrelloList;

/**
 * @author Mikhail Golubev
 */
public class TrelloUtil
{
	public static final Pattern TRELLO_ID_PATTERN = Pattern.compile("[a-z0-9]{24}");
	public static final Gson GSON = buildGson();
	public static final String TRELLO_API_BASE_URL = "https://api.trello.com/1";
	public static final TypeToken<List<TrelloCard>> LIST_OF_CARDS_TYPE = new TypeToken<List<TrelloCard>>()
	{ /* empty */
	};
	public static final TypeToken<List<TrelloBoard>> LIST_OF_BOARDS_TYPE = new TypeToken<List<TrelloBoard>>()
	{ /* empty */
	};
	public static final TypeToken<List<TrelloList>> LIST_OF_LISTS_TYPE = new TypeToken<List<TrelloList>>()
	{ /* empty */
	};
	public static final TypeToken<List<TrelloCommentAction>> LIST_OF_COMMENTS_TYPE = new TypeToken<List<TrelloCommentAction>>()
	{ /* empty */
	};

	private static Gson buildGson()
	{
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Date.class, new DateDeserializer());
		gson.registerTypeAdapter(LabelColor.class, new LabelColorDeserializer());
		return gson.create();
	}

	private static class DateDeserializer implements JsonDeserializer<Date>
	{
		// Trello send UTC dates, so time zone is always Z
		private static final DateFormat ISO8601_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

		static
		{
			ISO8601_DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
		}

		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			String dateString = json.getAsString();
			try
			{
				return ISO8601_DATETIME_FORMAT.parse(dateString);
			}
			catch(ParseException e)
			{
				throw null;
			}
		}
	}

	private static class LabelColorDeserializer implements JsonDeserializer<LabelColor>
	{
		@Override
		public LabelColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			final String colorName = json.getAsString().toUpperCase();
			if(colorName.isEmpty())
			{
				return LabelColor.NO_COLOR;
			}
			try
			{
				return LabelColor.valueOf(colorName);
			}
			catch(IllegalArgumentException e)
			{
				return LabelColor.NO_COLOR;
			}
		}
	}
}
