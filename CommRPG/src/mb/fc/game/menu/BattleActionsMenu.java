package mb.fc.game.menu;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.move.AttackableSpace;
import mb.fc.game.sprite.CombatSprite;

import org.newdawn.slick.Image;


public class BattleActionsMenu extends QuadMenu
{
	public BattleActionsMenu(StateInfo stateInfo) {
		super(Panel.PANEL_BATTLE, stateInfo);

		icons = new Image[8];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheets().get("actionicons").getSubImage(i % 4, i / 4);

		text = new String[] {"Attack", "Magic", "Items", "Stay"};
		enabled = new boolean[4];
		enabled[3] = true;
	}

	@Override
	public void initialize() {
		this.selected = Direction.DOWN;
		CombatSprite currentSprite = stateInfo.getCurrentSprite();

		if (currentSprite.getSpellsDescriptors() != null && stateInfo.getCurrentSprite().getSpellsDescriptors().size() > 0)
			enabled[1] = true;
		else
			enabled[1] = false;

		if (currentSprite.getItemsSize() > 0)
			enabled[2] = true;
		else
			enabled[2] = false;

		/**************************************************/
		/* Determine if there are enemies in attack range */
		/**************************************************/
		int declaredRange = currentSprite.getAttackRange();
		int range[][] = null;

		switch (declaredRange)
		{
			case 1:
				range = AttackableSpace.RANGE_1;
				break;
			case 2:
				range = AttackableSpace.RANGE_2;
				break;
			case 3:
				range = AttackableSpace.RANGE_3;
				break;
			case EquippableItem.RANGE_BOW_2_NO_1:
				range = AttackableSpace.RANGE_2_1;
				break;
		}

		int rangeOffset = (range.length - 1) / 2;

		enabled[0] = false;

		OUTER: for (int i = 0; i < range.length; i++)
		{
			for (int j = 0; j < range[0].length; j++)
			{
				if (range[i][j] == 1)
				{
					CombatSprite targetable = stateInfo.getCombatSpriteAtTile(currentSprite.getTileX() - rangeOffset + i,
							currentSprite.getTileY() - rangeOffset + j, false);
					if (targetable != null)
					{
						enabled[0] = true;
						this.selected = Direction.UP;
						break OUTER;
					}
				}
			}
		}
	}

	@Override
	public MenuUpdate onBack() {
		stateInfo.sendMessage(Message.MESSAGE_SHOW_MOVEABLE);
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public MenuUpdate onConfirm() {
		switch (selected)
		{
			case UP:
				stateInfo.sendMessage(Message.MESSAGE_ATTACK_PRESSED);
				break;
			case LEFT:
				stateInfo.sendMessage(Message.MESSAGE_SHOW_SPELLMENU);
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
				break;
			case RIGHT:
				stateInfo.sendMessage(Message.MESSAGE_SHOW_ITEM_MENU);
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
				break;
			case DOWN:
				stateInfo.sendMessage(Message.MESSAGE_PLAYER_END_TURN);
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
				break;
		}

		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public boolean makeAddAndRemoveSounds()
	{
		return true;
	}
}
