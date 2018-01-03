package com.intellij.tasks.context;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	public TestBreakpointProperties createBreakpointProperties(@NotNull VirtualFile virtualFile, int i)
	{
		return new TestBreakpointProperties();
	}
}
