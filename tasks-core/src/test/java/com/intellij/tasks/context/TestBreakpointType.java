package com.intellij.tasks.context;

import javax.annotation.Nonnull;

import javax.annotation.Nullable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;

/**
 * @author VISTALL
 * @since 2018-01-03
 */
public class TestBreakpointType extends XLineBreakpointType<TestBreakpointProperties>
{
	public TestBreakpointType()
	{
		super("test", "Test Breakpoint");
	}

	@Nullable
	@Override
	public TestBreakpointProperties createBreakpointProperties(@Nonnull VirtualFile virtualFile, int i)
	{
		return new TestBreakpointProperties();
	}
}
