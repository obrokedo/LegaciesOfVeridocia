package mb.fc.loading;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import mb.fc.game.ui.PaddedGameContainer;

public class LOVFirstLoadRenderer extends LOVLoadRenderer {
	private Music loadingMusic;
	
	public LOVFirstLoadRenderer(GameContainer container, Music loadingMusic) {
		super(container);
		this.loadingMusic = loadingMusic;
	}

	@Override
	public void render(LoadingStatus loading) {
		super.render(loading);
		drawCredits(graphics);
	}

	@Override
	public void doneLoading() {
		if (loadingMusic != null) {
			loadingMusic.fade(500, 0f, true);
			loadingMusic = null;
		}
	}

	private void drawCredits(Graphics g) {
		int y = 10;
		Color accentColor = new Color(66, 134, 244);
		g.setColor(accentColor);
		g.translate(((PaddedGameContainer) gc).getDisplayPaddingX(), 0);
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
		
		g.resetTransform();
	}
}
