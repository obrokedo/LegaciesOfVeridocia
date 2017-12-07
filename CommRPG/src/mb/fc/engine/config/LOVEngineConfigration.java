package mb.fc.engine.config;

public class LOVEngineConfigration extends DefaultEngineConfiguration {
	public LOVEngineConfigration() {
		healthPanelRenderer = new LOVHealthPanelRenderer();
		spellMenuRenderer = new LOVSpellMenuRenderer();
	}
}
