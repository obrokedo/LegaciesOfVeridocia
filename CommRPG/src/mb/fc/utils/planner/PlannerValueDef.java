package mb.fc.utils.planner;


public class PlannerValueDef
{
	public static final int REFERS_NONE = 0;
	public static final int REFERS_TRIGGER = 1;
	public static final int REFERS_CINEMATIC = 2;
	public static final int REFERS_TEXT = 3;
	public static final int REFERS_HERO = 4;
	public static final int REFERS_ENEMY = 5;
	public static final int REFERS_ITEM = 6;
	public static final int REFERS_QUEST = 7;
	public static final int REFERS_AI_APPROACH = 8;
	public static final int REFERS_STAT_GAINS = 9;
	public static final int REFERS_ITEM_STYLE = 10;
	public static final int REFERS_ITEM_TYPE = 11;
	public static final int REFERS_ITEM_RANGE = 12;
	public static final int REFERS_MOVE_TYPE = 13;
	public static final int REFERS_SPELL = 14;
	public static final int REFERS_ITEM_AREA = 15;
	public static final int REFERS_DIRECTION = 16;
	public static final int REFERS_ANIMATIONS = 17;
	public static final int REFERS_MAP = 18;
	public static final int REFERS_SPRITE_IMAGE = 19;
	public static final int REFERS_EFFECT = 20;
	public static final int REFERS_ATTRIBUTE_STRENGTH = 21;
	public static final int REFERS_BODYMIND_GAIN = 22;
	public static final int REFERS_AI = 23;
	public static final int REFERS_TERRAIN = 24;
	public static final int REFERS_PALETTE = 25;
	// Extended weapon stats
	public static final int REFERS_WEAPON_DAMAGE_TYPE = 26;
	public static final int REFERS_AFFINITIES = 27;
	public static final int REFERS_CONDITIONS = 28;

	public static final int TYPE_STRING = 0;
	public static final int TYPE_INT = 1;
	public static final int TYPE_BOOLEAN = 2;
	public static final int TYPE_MULTI_INT = 3;
	public static final int TYPE_LONG_STRING = 4;
	public static final int TYPE_UNBOUNDED_INT = 5;

	// Refers to
	private int refersTo;

	// Value type
	private int valueType;

	// Variable output
	private String tag;

	private boolean optional = false;

	private String displayTag;
	private String displayDescription;

	public PlannerValueDef(int refersTo, int valueType, String tag,
			boolean optional, String displayTag, String displayDescription) {
		super();
		this.refersTo = refersTo;
		this.valueType = valueType;
		this.tag = tag;
		this.optional = optional;
		this.displayTag = displayTag;
		this.displayDescription = displayDescription;
	}

	public PlannerValueDef copy()
	{
		return new PlannerValueDef(refersTo, valueType, tag, optional, displayTag, displayDescription);
	}

	public int getRefersTo() {
		return refersTo;
	}

	public int getValueType() {
		return valueType;
	}

	public String getTag() {
		return tag;
	}

	public boolean isOptional() {
		return optional;
	}

	public String getDisplayTag() {
		return displayTag;
	}

	public String getDisplayDescription() {
		return displayDescription;
	}
}
