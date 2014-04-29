package mb.fc.utils.planner;

import java.util.ArrayList;

public class PlannerContainerDef
{
	private PlannerLineDef definingLine;
	private ArrayList<PlannerLineDef> allowableLines;
	private ArrayList<ArrayList<String>> listOfLists;
	private int writeToIndex;
	
	public PlannerContainerDef(PlannerLineDef definingLine,
			ArrayList<PlannerContainerDef> allowableContainers,
			ArrayList<PlannerLineDef> allowableLines, ArrayList<ArrayList<String>> listOfLists,
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
	
	public ArrayList<String> getDataLines()
	{
		return listOfLists.get(writeToIndex);
	}

	public ArrayList<ArrayList<String>> getListOfLists() {
		return listOfLists;
	}
}
