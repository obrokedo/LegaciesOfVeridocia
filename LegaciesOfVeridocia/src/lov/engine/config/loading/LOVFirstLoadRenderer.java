package lov.engine.config.loading;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import tactical.game.ui.PaddedGameContainer;
import tactical.loading.LoadingStatus;

public class LOVFirstLoadRenderer extends LOVLoadRenderer {
	private Music loadingMusic;
	private int waitTime = 1500;
	private boolean doneLoading = false;
	
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
		if (!doneLoading) {
			if (loadingMusic != null) {
				loadingMusic.fade(1500, 0f, true);
				loadingMusic = null;
			}
			doneLoading = true;
		}
	}

	@Override
	public boolean canTransition(int delta) {
		return (waitTime -= delta) < 0;
	}

	private void drawCredits(Graphics g) {
		int y = 5;
		g.resetTransform();
		Color accentColor = new Color(66, 134, 244);
		g.setColor(accentColor);
		g.translate(gc.getWidth() / 2 - 240, 0);
		g.drawString("Credits", -90, y);
		
		g.drawString("Project Leads", -70, y += 20);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("Keegan McCarthy 'MXC'", -70, y += 5);		
		g.drawString("Peter Dale 'Stordarth'", 230, y);
		g.drawString("Brian Amell 'Corsair'", -70, y += 20);
		g.drawString("Broked", 230, y);
		g.drawString("Giuseppe T. 'SirIsaacLemon'", -70, y += 20);
		
		
		y -= 45;
		g.setColor(accentColor);
		g.drawString("- Project", 125, y += 5);		
		g.drawString("- Project", 435, y);
		g.drawString("- Artist", 125, y += 20);
		g.drawString("- Programming", 295, y);
		g.drawString("- Engineering", 180, y += 20);
				
		g.setColor(accentColor);
		g.drawString("Map Creation", -70, y += 30);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("Kevin Redenz 'zexxar'", -70, y += 5);
		g.drawString("Johannes Hüsing 'Drakonis'", 220, y);
		
		g.setColor(accentColor);
		g.drawString("Story and Script", -70, y += 30);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("Richard Page 'Rick'", -70, y += 5);
		g.drawString("Ethan Rowe 'Antman 537'", 220, y);
		g.drawString("Jon Chown 'Nuburan'", -70, y += 20);
		g.drawString("Hirsute", 220, y);
		
		g.setColor(accentColor);
		g.drawString("Artists", -70, y += 30);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("Joshua Greiner 'Dark Link'", -70, y += 5);
		g.drawString("L. Porteous 'Wyndigo'", 220, y);
		g.drawString("Mystic Shadow", -70, y += 20);
		g.drawString("Alones", 220, y);
		g.drawString("whiterose", -70, y += 20);
		g.drawString("Dani Hunt 'Omega Entity'", 220, y);
		g.drawString("Googrifflon", -70, y += 20);
		g.drawString("Red Archer", 220, y);		
		
		g.setColor(accentColor);
		g.drawString("Character Creation", -70, y += 30);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("Frank Gritzmacher 'Balbaroy'", -70, y += 5);
		g.drawString("xenometal", 220, y);
		g.drawString("Chris Geddis 'Aldur'", -70, y += 20);
		g.drawString("RagnarokkerAJ", 220, y);
		g.drawString("Al Gritzmacher", -70, y += 20);
		
		g.setColor(accentColor);
		g.drawString("Project Inspiration", -70, y += 30);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("BigNailCow", -70, y += 5);
		g.drawString("Space King", 220, y);
		g.drawString("Patrick Parent 'ZeXr0'", -70, y += 20);		
		g.drawString("aanderse", 220, y);
		g.drawString("SFC Community", -70, y += 20);
		
				
		g.setColor(accentColor);
		g.drawString("Support", -70, y += 30);
		g.setColor(Color.white);
		g.drawLine(-70, y += 20, 520, y);
		g.drawString("Loftus", -70, y += 5);
		g.drawString("nightshade00123", 220, y);		
		g.drawString("Wandering Dezorian", -70, y += 20);
		g.drawString("NekoNova", 220, y);
		g.drawString("SirHedge", -70, y += 20);
		g.drawString("Amelie", 220, y);
		
		g.resetTransform();
	}
}
