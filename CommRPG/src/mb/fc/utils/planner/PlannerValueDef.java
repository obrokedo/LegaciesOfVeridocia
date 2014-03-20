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
	public static final int REFERS_AI = 8;
	public static final int REFERS_STAT_GAINS = 9;
	public static final int REFERS_ITEM_STYLE = 10;
	public static final int REFERS_ITEM_TYPE = 11;
	public static final int REFERS_ITEM_RANGE = 12;
	public static final int REFERS_MOVE_TYPE = 13;	
	public static final int REFERS_SPELL = 14;
	public static final int REFERS_ITEM_AREA = 15;
	
	public static final int TYPE_STRING = 0;
	public static final int TYPE_INT = 1;
	public static final int TYPE_BOOLEAN = 2;
	
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
