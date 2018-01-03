package com.intellij.tasks.context;

import org.jetbrains.annotations.Nullable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;

/**
 * @author VISTALL
 * @since 2018-01-03
 */
public class TestBreakpointProperties extends XBreakpointProperties<TestBreakpointProperties>
{
	@Nullable
	@Override
	public TestBreakpointProperties getState()
	{
		return this;
	}

	@Override
	public void loadState(TestBreakpointProperties o)
	{
		XmlSerializerUtil.copyBean(o, this);
	}
}
