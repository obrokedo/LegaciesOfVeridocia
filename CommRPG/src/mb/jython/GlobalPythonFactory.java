package mb.jython;

import java.io.File;

import mb.fc.loading.LoadingState;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;

/**
 * Factory to create Jython objects that are backed by the corresponding Python script. Methods
 * called on these objects will use the results as determined by the script.
 * NOTE: Currently the scripts can be re-loaded via the debug menu, which means subsequent calls
 * to the creation methods contained herein may result in objects whose methods return different values
 *
 * @TODO When we make it to a point where we don't want to be able to reload the Python scripts for testing purposes
 * then we should make this a "singleton" class that can only be initialized once
 *
 * @see /mb/fc/game/menu/DebugMenu
 * @see /scripts/
 *
 * @author Broked
 *
 */
public class GlobalPythonFactory
{
	/**
	 * Hold an instance of each of the Jython objects that correspond with a Python script
	 */
	private static JCinematicActor cinematicActor = null;
	private static JBattleFunctions battleFunctions = null;
	private static JPanelRender panelRender = null;
	private static JMusicSelector musicSelector = null;
	private static JSpell spell = null;
	private static JBattleEffect battleEffect = null;
	private static JLevelProgression levelProgression = null;

	/**
	 * A boolean flag indicating whether this factory has been initialized.
	 * Creation methods will not work unless this value is set true
	 */

	private static boolean initialized = false;

	/**
	 * Initializes the GlobalPythonFactory by loading all of the python scripts and
	 * storing a copy
	 */
	public static void intialize()
	{
		initialized = true;

		/************************************************************************/
		/* Depending on whether this is being built for a single jar or not; 	*/
		/* single jar or not scripts will be loaded differently				  	*/
		/************************************************************************/
		// The build is being done for one large JAR that contains all resources
		if (LoadingState.inJar)
		{
			String jarPath = JythonObjectFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			jarPath = jarPath.replaceAll("%20", " ");
			PySystemState state = new PySystemState();
			state.path.insert(0,Py.newString(jarPath + java.io.File.separator + "scripts"));
			Py.setSystemState(state);
		}

		JythonObjectFactory.sys  = Py.getSystemState();

		// The build is being done for a jar that does not contain all of the resources
		if (!LoadingState.inJar)
		{
			JythonObjectFactory.sys.path.append(new PyString(JythonObjectFactory.sys.getPath("scripts")));


			File scriptsFolder = new File(JythonObjectFactory.sys.getPath("scripts"));
			for (File file : scriptsFolder.listFiles())
			{
				if (file.getName().endsWith(".class"))
					file.delete();
			}
		}

		// There should only ever be a single instance of this class, so set all of the
		// values so they can be accessed in a static way
        panelRender = (JPanelRender) (new JythonObjectFactory(JPanelRender.class, "PanelRender", "PanelRender")).createObject();
        battleFunctions = (JBattleFunctions) (new JythonObjectFactory(JBattleFunctions.class, "BattleFunctions", "BattleFunctions")).createObject();
        cinematicActor  = (JCinematicActor) (new JythonObjectFactory(JCinematicActor.class, "CinematicActor", "CinematicActor")).createObject();
        musicSelector  = (JMusicSelector) (new JythonObjectFactory(JMusicSelector.class, "MusicScript", "MusicScript")).createObject();
        spell  = (JSpell) (new JythonObjectFactory(JSpell.class, "Spells", "Spells")).createObject();
        battleEffect = (JBattleEffect) (new JythonObjectFactory(JBattleEffect.class, "BattleEffect", "BattleEffect")).createObject();
        levelProgression = (JLevelProgression) (new JythonObjectFactory(JLevelProgression.class, "LevelProgression", "LevelProgression")).createObject();
	}

	/**
	 * Gets a script-backed JCinematicActor. This
	 * method should only be called after the factory
	 * has been initialized
	 *
	 * @return a script-backed JCinematicActor
	 */
	public static JCinematicActor createJCinematicActor()
	{
		checkFactoryInitialized();
		return cinematicActor;
	}

	/**
	 * Gets a script-backed JBattleFunctions. This
	 * method should only be called after the factory
	 * has been initialized
	 *
	 * @return a script-backed JBattleFunctions
	 */
	public static JBattleFunctions createJBattleFunctions()
	{
		checkFactoryInitialized();
		return battleFunctions;
	}

	/**
	 * Gets a script-backed JPanelRender. This
	 * method should only be called after the factory
	 * has been initialized
	 *
	 * @return a script-backed JPanelRender
	 */
	public static JPanelRender createJPanelRender()
	{
		checkFactoryInitialized();
		return panelRender;
	}

	/**
	 * Gets a script-backed JMusicSelector. This
	 * method should only be called after the factory
	 * has been initialized
	 *
	 * @return a script-backed JMusicSelector
	 */
	public static JMusicSelector createJMusicSelector()
	{
		checkFactoryInitialized();
		return musicSelector;
	}

	/**
	 * Gets a script-backed JSpell. This
	 * method should only be called after the factory
	 * has been initialized
	 *
	 * @return a script-backed JSpell
	 */
	public static JSpell createJSpell()
	{
		checkFactoryInitialized();
		return spell;
	}

	public static JBattleEffect createJBattleEffect(int id)
	{
		return battleEffect.init(id);
	}

	public static JLevelProgression createLevelProgression()
	{
		checkFactoryInitialized();
		return levelProgression;
	}

	/**
	 * Ensures that this factory has been initialized before attempting to
	 * return a Jython object
	 */
	private static void checkFactoryInitialized()
	{
		if (!initialized)
			throw new RuntimeException("Attempted to create Jython object before initializing the factory");
	}
}
