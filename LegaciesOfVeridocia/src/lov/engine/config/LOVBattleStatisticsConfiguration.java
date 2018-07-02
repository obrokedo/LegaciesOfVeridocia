package lov.engine.config;

import lov.game.definition.LOVEnemyDefinition;
import lov.game.definition.LOVHeroDefinition;
import tactical.engine.config.BattleStatisticConfigration;
import tactical.game.definition.EnemyDefinition;
import tactical.game.definition.HeroDefinition;
import tactical.utils.XMLParser.TagArea;

public class LOVBattleStatisticsConfiguration implements BattleStatisticConfigration {
	// TODO This doesn't really show what needs to be changed
	@Override
	public HeroDefinition parseHeroDefinition(TagArea tagArea) {
		return new LOVHeroDefinition(tagArea);
	}

	@Override
	public EnemyDefinition parseEnemyDefinition(TagArea tagArea) {
		return new LOVEnemyDefinition(tagArea);				
	}

}
