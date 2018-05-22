package mb.fc.engine.state;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import mb.fc.engine.CommRPG;
import mb.fc.game.menu.Menu;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.ResourceManager;

public class CreditsState extends LoadableGameState{
	private float scrollY = 750;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initAfterLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta) throws SlickException {
		scrollY = (float) Math.max(scrollY - .5, -1110);
		if (container.getInput().isKeyDown(Input.KEY_ENTER)) {
			System.exit(0);
		}
	}

	@Override
	public void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g) {
		drawCredits(container, g);
		drawMusicContributions(container, g);
		
		if (scrollY == -1110) {
			g.drawString("(Press enter key to exit)", 120, container.getHeight() - 40);
		}
		
	}

	@Override
	protected Menu getPauseMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_CREDITS;
	}
	
	private void drawMusicContributions(GameContainer gc, Graphics g) {
		float y = 700 + scrollY;
		Color accentColor = new Color(66, 134, 244);
		g.setColor(accentColor);
		g.drawString("Musical Contributions", 40, y);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 400, y);
		g.drawString("'The Tense Battle' ~ Sephirot24", 40, y += 5);
		g.drawString("'Rise of the Titans' ~ Computer112/Dem0lecule", 40, y += 30);
		g.drawString("'Remote Attack' ~ Computer112/Dem0lecule", 40, y += 30);
		g.drawString("'Surrounded' ~ Devastus", 40, y += 30);
		g.drawString("'Shark Patrol' ~ BenjaminTibbetts", 40, y += 30);
		g.drawString("'King's Banquet' ~ dfiechter2", 40, y += 30);
		g.drawString("'No Quarter' ~ Jeremiah 'McTricky' George", 40, y += 30);
		g.drawString("'Unveiling for Brass Ensemble' ~ BrianSadler", 40, y += 30);
		g.drawString("'Triumphant' ~ DavidGrossmanMusic", 40, y += 30);
		g.drawString("'Hero Music' ~ Benmode", 40, y += 30);	
		
		g.drawString("Legacies of Veridocia is an open-source project developed by a dedicated ", -60, y += 250);
		g.drawString("team of volunteers and we are always looking for more help. If you're ", -60, y += 30);
		g.drawString("interested in joining us in any capacity please contact us at ", -60, y += 30);
		g.drawString("legaciesofveridocia@gmail.com", -60, y += 30);
		g.drawString("Thank you for playing!", 120, y += 120);	
			
	}

	private void drawCredits(GameContainer gc, Graphics g) {
		g.resetTransform();
		float y = 0 + scrollY;
		Color accentColor = new Color(66, 134, 244);
		g.setColor(accentColor);
		g.translate(gc.getWidth() / 2 - 240, 0);
		g.drawString("Credits", 30, y);
		
		g.drawString("Project Leads", 40, y += 20);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 400, y);
		g.drawString("MXC", 40, y += 5);		
		g.drawString("Stordarth", 220, y);
		g.drawString("Corsair", 40, y += 20);
		g.drawString("Broked", 220, y);
		g.drawString("SirIsaacLemon", 40, y += 20);
		
		
		y -= 45;
		g.setColor(accentColor);
		g.drawString("- Project", 75, y += 5);		
		g.drawString("- Project", 310, y);
		g.drawString("- Artist", 112, y += 20);
		g.drawString("- Programming", 283, y);
		g.drawString("- Engineering", 165, y += 20);
				
		g.setColor(accentColor);
		g.drawString("Map Creation", 40, y += 30);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 360, y);
		g.drawString("zexxar", 40, y += 5);
		g.drawString("Drakonis", 220, y);
		
		g.setColor(accentColor);
		g.drawString("Story and Script", 40, y += 30);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 360, y);
		g.drawString("Rick", 40, y += 5);
		g.drawString("Antman 537", 220, y);
		g.drawString("Nuburan", 40, y += 20);
		g.drawString("Hirsute", 220, y);
		
		g.setColor(accentColor);
		g.drawString("Artists", 40, y += 30);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 360, y);
		g.drawString("Dark Link", 40, y += 5);
		g.drawString("Wyndigo", 220, y);
		g.drawString("Mystic Shadow", 40, y += 20);
		g.drawString("Alones", 220, y);
		g.drawString("whiterose", 40, y += 20);
		g.drawString("Omega Entity", 220, y);
		g.drawString("Googrifflon", 40, y += 20);
		g.drawString("Red Archer", 220, y);		
		
		g.setColor(accentColor);
		g.drawString("Character Creation", 40, y += 30);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 360, y);
		g.drawString("Balbaroy", 40, y += 5);
		g.drawString("xenometal", 220, y);
		g.drawString("Aldur", 40, y += 20);
		g.drawString("RagnarokkerAJ", 220, y);
		g.drawString("Al Gritzmacher", 40, y += 20);
		
		g.setColor(accentColor);
		g.drawString("Project Inspiration", 40, y += 30);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 360, y);
		g.drawString("BigNailCow", 40, y += 5);
		g.drawString("Space King", 220, y);
		g.drawString("SFC Community", 40, y += 20);
		
				
		g.setColor(accentColor);
		g.drawString("Support", 40, y += 30);
		g.setColor(Color.white);
		g.drawLine(40, y += 20, 360, y);
		g.drawString("Loftus", 40, y += 5);
		g.drawString("nightshade00123", 220, y);		
		g.drawString("Wandering Dezorian", 40, y += 20);
		g.drawString("NekoNova", 220, y);
		g.drawString("SirHedge", 40, y += 20);
		g.drawString("Amelie", 220, y);
	}
}
