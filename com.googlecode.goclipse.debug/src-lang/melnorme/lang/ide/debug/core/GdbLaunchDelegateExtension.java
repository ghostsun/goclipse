package melnorme.lang.ide.debug.core;

import melnorme.utilbox.core.DevelopmentCodeMarkers;

import org.eclipse.cdt.dsf.debug.sourcelookup.DsfSourceLookupDirector;
import org.eclipse.cdt.dsf.debug.sourcelookup.DsfSourceLookupParticipant;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunchDelegate;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class GdbLaunchDelegateExtension extends GdbLaunchDelegate {
	
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		setDefaultProcessFactory(configuration); // Reset process factory to what GdbLaunch expected
		
		ILaunch launch = super.getLaunch(configuration, mode);
		// workaround for DLTK bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=419273
		launch.setAttribute("org.eclipse.dltk.debug.debugConsole", "false");
		return launch;
	}
	
	@Override
	protected ISourceLocator getSourceLocator(ILaunchConfiguration configuration, DsfSession session)
			throws CoreException {
		return super.getSourceLocator(configuration, session);
	}
	
	@Override
	protected IPath checkBinaryDetails(ILaunchConfiguration config) throws CoreException {
		// Now verify we know the program to debug.
		IPath exePath = LaunchUtils.verifyProgramPath(config, null);
		// Finally, make sure the program is a proper binary.
		
		// BM: this code is disabled because without a project the binary verifier defaults to ELF on any platform
		if(DevelopmentCodeMarkers.UNIMPLEMENTED_FUNCTIONALITY) {
			LaunchUtils.verifyBinary(config, exePath);
		}
		return exePath;
	}
	
	@Override
	protected DsfSourceLookupDirector createDsfSourceLocator(ILaunchConfiguration configuration, DsfSession session)
			throws CoreException {
		DsfSourceLookupDirector sourceLookupDirector = new LangSourceLookupDirector(session);
		
		sourceLookupDirector.addParticipants( 
				new ISourceLookupParticipant[]{ new DsfSourceLookupParticipant(session) } );
		return sourceLookupDirector;
	}
	
}