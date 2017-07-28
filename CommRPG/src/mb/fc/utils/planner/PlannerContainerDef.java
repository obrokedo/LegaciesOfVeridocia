package mb.fc.utils.planner;

import java.util.ArrayList;

public class PlannerContainerDef
{
	private PlannerLineDef definingLine;
	private ArrayList<PlannerLineDef> allowableLines;
	private ArrayList<ArrayList<PlannerReference>> listOfLists;
	private int writeToIndex;

	public PlannerContainerDef(PlannerLineDef definingLine,
			ArrayList<PlannerContainerDef> allowableContainers,
			ArrayList<PlannerLineDef> allowableLines, ArrayList<ArrayList<PlannerReference>> listOfLists,
			int writeToIndex)
	{
		this.definingLine = definingLine;
		this.allowableLines = allowableLines;
		this.listOfLists = listOfLists;
		this.writeToIndex = writeToIndex;
	}

	public PlannerLineDef getDefiningLine() {
		return definingLine;
	}

	public ArrayList<PlannerLineDef> getAllowableLines() {
		return allowableLines;
	}

	/**
	 * Get the "list of lists" that contain the name of every item definied so that they
	 * may be refered to by REFER tags
	 *
	 * @return the "list of lists" that contain the name of every item definied so that they
	 * may be refered to by REFER tags.
	 */
	public ArrayList<PlannerReference> getDataLines()
	{
		return listOfLists.get(writeToIndex);
	}

	public ArrayList<ArrayList<PlannerReference>> getListOfLists() {
		return listOfLists;
	}
}
