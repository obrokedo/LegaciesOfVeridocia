package mb.jython;

import java.io.File;

import mb.fc.game.battle.spell.Spell;
import mb.gl2.loading.LoadingState;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;

public class GlobalPythonFactory
{
	private static JythonObjectFactory panelRendererFact = null;
	private static JythonObjectFactory battleFunctionsFact;
	private static JythonObjectFactory cinematicActorFact;
	private static JythonObjectFactory musicScriptFact;
	private static JythonObjectFactory spellFact;

	public static void intialize()
	{
		// UNCOMMENT THIS FOR SINGLE JAR
		if (LoadingState.inJar)
		{
			String jarPath = JythonObjectFactory.class.getProtectionDomain().getCodeSource().getLocation() .getPath();
			PySystemState state = new PySystemState();
			state.path.insert(0,Py.newString(jarPath + java.io.File.separator + "scripts"));
			Py.setSystemState(state);
		}

		JythonObjectFactory.sys  = Py.getSystemState();
		System.out.println("Path");
		System.out.println(JythonObjectFactory.sys.path);

		// BEGIN COMMENTING HERE FOR SINGLE JAR
		if (!LoadingState.inJar)
		{
			JythonObjectFactory.sys.path.append(new PyString(JythonObjectFactory.sys.getPath("scripts")));


			File scriptsFolder = new File(JythonObjectFactory.sys.getPath("scripts"));
			for (File file : scriptsFolder.listFiles())
			{
				if (file.getName().endsWith(".class"))
					file.delete();
			}


			if (panelRendererFact != null)
				createJPanelRender().reload();
		}

		// END COMMENTING HERE FOR SINGLE JAR

        panelRendererFact = new JythonObjectFactory(JPanelRender.class, "PanelRender", "PanelRender");
        battleFunctionsFact = new JythonObjectFactory(JBattleFunctions.class, "BattleFunctions", "BattleFunctions");
        cinematicActorFact  = new JythonObjectFactory(JCinematicActor.class, "CinematicActor", "CinematicActor");
        musicScriptFact  = new JythonObjectFactory(JMusicSelector.class, "MusicScript", "MusicScript");
        spellFact  = new JythonObjectFactory(Spell.class, "Spells", "Spells");
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

	public static Spell createSpell()
	{
		return (Spell) spellFact.createObject();
	}
}
