package mb.jython;

import java.io.File;

import org.python.core.Py;
import org.python.core.PyString;

public class GlobalPythonFactory 
{
	private static JythonObjectFactory panelRendererFact = null;
	private static JythonObjectFactory battleFunctionsFact;
	private static JythonObjectFactory cinematicActorFact;
	private static JythonObjectFactory musicScriptFact;
	public static void intialize()
	{
		JythonObjectFactory.sys  = Py.getSystemState();
		JythonObjectFactory.sys.path.append(new PyString(JythonObjectFactory.sys.getPath("scripts")));
		File scriptsFolder = new File(JythonObjectFactory.sys.getPath("scripts"));
		for (File file : scriptsFolder.listFiles())
		{
			if (file.getName().endsWith(".class"))
				file.delete();
		}
		
		if (panelRendererFact != null)
			createJPanelRender().reload();
		
        panelRendererFact = new JythonObjectFactory(JPanelRender.class, "PanelRender", "PanelRender");
        battleFunctionsFact = new JythonObjectFactory(JBattleFunctions.class, "BattleFunctions", "BattleFunctions");
        cinematicActorFact  = new JythonObjectFactory(JCinematicActor.class, "CinematicActor", "CinematicActor");
        musicScriptFact  = new JythonObjectFactory(JMusicSelector.class, "MusicScript", "MusicScript");
	}
	
	public static JCinematicActor createJCinematicActor()
	{
		return (JCinematicActor) cinematicActorFact.createObject();
	}
	
	public static JBattleFunctions createJBattleFunctions()
	{
		return (JBattleFunctions) battleFunctionsFact.createObject();
	}
	
	public static JPanelRender createJPanelRender()
	{
		return (JPanelRender) panelRendererFact.createObject();
	}	
	
	public static JMusicSelector createJMusicSelector()
	{
		return (JMusicSelector) musicScriptFact.createObject();
	}
}
