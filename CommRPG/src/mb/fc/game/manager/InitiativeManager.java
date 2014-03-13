package mb.fc.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mb.fc.engine.ForsakenChampions;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MultiSpriteContextMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class InitiativeManager extends Manager
{	
	private InitiativeMenu initMenu;
	private static final Color TRANS = new Color(255, 255, 255, 180);
	
	private class InitiativeMenu extends Panel implements MouseListener
	{		
		private boolean displayInit = true;	
		ArrayList<CombatSprite> initOrder;
		
		public InitiativeMenu(StateInfo stateInfo) {
			super(Panel.PANEL_INITIATIVE);
			stateInfo.registerMouseListener(this);
			
		}

		@Override
		public void render(FCGameContainer gc, Graphics graphics) {
			graphics.setColor(Color.white);
			graphics.drawRect(20, 20, 30, 14);
			if (displayInit && initOrder != null && initOrder.size() > 0)
			{
				graphics.drawString("^", 28, 22);
				for (int i = 0; i < 10; i++)
				{
					graphics.drawRect(20, 40 + i * 38, 30, 30);
					graphics.drawImage(initOrder.get(i % initOrder.size()).getCurrentImage(), 24, 42 + i * 38, TRANS); 
				}
			}
			else
				graphics.drawString("v", 28, 18);
		}		
		
		@Override
		public boolean mouseUpdate(int frameMX, int frameMY, int mapMX,
				int mapMY, boolean leftClicked, boolean rightClicked,
				StateInfo stateInfo) 
		{				
			if (leftClicked && Panel.contains(20, 50, 
					frameMX, 20, 34, frameMY))
			{
				displayInit = !displayInit;
				return true;
			}
							
			return false;
		}

		@Override
		public int getZOrder() {
			return MouseListener.ORDER_INIT;
		}
	}
	
	private class InitComparator implements Comparator<CombatSprite>
	{
		@Override
		public int compare(CombatSprite c1, CombatSprite c2) {
			return c2.getCurrentInit() - c1.getCurrentInit() ;
		}
	}
	
	@Override
	public void initialize() {
	
	}

	private void initializeAfterSprites()
	{
		initMenu = new InitiativeMenu(stateInfo);
		// The host will initialize the turn order, so check to see if you are the host
		initializeInitOrder();
		stateInfo.addPanel(initMenu);
	}
	
	public void updateOnTurn()
	{
		getNextTurn();
	}
	
	private void getNextTurn()
	{
		ArrayList<CombatSprite> initOrder = new ArrayList<CombatSprite>();
		for (CombatSprite s : stateInfo.getCombatSprites())
		{							
			initOrder.add(s);
		}
		
		CombatSprite nextTurn = null;
		
		while (nextTurn == null)
		{
			for (CombatSprite cs : initOrder)
			{
				// Increase the sprites initiaitive by 7 and potentially an addtional 1 based on speed
				cs.setCurrentInit(cs.getCurrentInit() + 7 + (ForsakenChampions.RANDOM.nextInt(100) < cs.getCurrentSpeed() ? 1 : 0));
				if (cs.getCurrentInit() >= 100)
				{
					if (nextTurn == null || cs.getCurrentInit() > nextTurn.getCurrentInit() ||
						(cs.getCurrentInit() == nextTurn.getCurrentInit() && 
							cs.getCurrentSpeed() > nextTurn.getCurrentInit()))
							nextTurn = cs; 
				}
			}
		}	
		
		nextTurn.setCurrentInit(0);
		stateInfo.sendMessage(new SpriteContextMessage(Message.MESSAGE_COMBATANT_TURN, nextTurn));
		
		Collections.sort(initOrder, new InitComparator());
		
		stateInfo.sendMessage(new MultiSpriteContextMessage(Message.MESSAGE_SET_INIT_ORDER, initOrder));		
	}
	
	public void initializeInitOrder()
	{
		ArrayList<CombatSprite> initOrder = new ArrayList<CombatSprite>();
		for (CombatSprite s : stateInfo.getCombatSprites())
		{
			s.setCurrentInit(s.getMaxSpeed());
			
			initOrder.add(s);
		}		
		
		stateInfo.sendMessage(new MultiSpriteContextMessage(Message.MESSAGE_SET_INIT_ORDER, initOrder));
	}

	@Override
	public void recieveMessage(Message message) {
		switch (message.getMessageType())
		{
			case Message.MESSAGE_NEXT_TURN:
				System.out.println("NEXT TURN");
				updateOnTurn();
				break;
			case Message.MESSAGE_SET_INIT_ORDER:
				initMenu.initOrder = ((MultiSpriteContextMessage) message).getSprites();
				break;
			case Message.MESSAGE_INTIIALIZE:
				System.out.println("INIT IN INIT");
				initializeAfterSprites();
				break;
		}
	}	
}
