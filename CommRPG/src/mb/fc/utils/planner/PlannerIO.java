package mb.fc.utils.planner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import mb.fc.engine.log.LoggingUtils;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

public class PlannerIO {
	private static final Logger LOGGER = LoggingUtils.createLogger(PlannerIO.class);
	
	public static String PATH_ENEMIES = "definitions/Enemies";
	public static String PATH_HEROES = "definitions/Heroes";
	public static String PATH_ITEMS = "definitions/Items";
	public static String PATH_QUESTS = "Quests";
	public static String PATH_MAPS = "map";
	
	/********************************************/
	/* Export data methods 						*/
	/********************************************/
	public boolean exportDataToFile(ArrayList<PlannerContainer> containers,
			String pathToFile, boolean append) {
		ArrayList<String> buffer = export(containers);

		Path path = Paths.get(pathToFile);
		try {
			if (append)
				Files.write(path, buffer, StandardCharsets.UTF_8,
						StandardOpenOption.APPEND);
			else
				Files.write(path, buffer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while trying to save the data:"
					+ e.getMessage(), "Error saving data", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public static ArrayList<String> export(ArrayList<PlannerContainer> containers)
	{
		ArrayList<String> buffer = new ArrayList<String>();
		for (int i = 0; i < containers.size(); i++) {
			PlannerContainer pc = containers.get(i);

			buffer.add(exportLine(pc.getPcdef().getDefiningLine(),
					pc.getDefLine(), i));

			for (PlannerLine pl : pc.getLines())
				buffer.add(exportLine(pl.getPlDef(), pl, -1));

			buffer.add("</" + pc.getPcdef().getDefiningLine().getTag() + ">");
		}
		return buffer;
	}

	private static String exportLine(PlannerLineDef pldef, PlannerLine pl, int id) {
		String stringBuffer = "";
		if (pl.isDefining())
			stringBuffer += "<" + pldef.getTag();
		else
			stringBuffer += "\t<" + pldef.getTag();

		if (id != -1)
			stringBuffer += " id=" + id;

		for (int i = 0; i < pldef.getPlannerValues().size(); i++) {
			PlannerValueDef pvd = pldef.getPlannerValues().get(i);
			stringBuffer += " " + pvd.getTag() + "=";
			if (pvd.getValueType() == PlannerValueDef.TYPE_BOOLEAN)
				stringBuffer += pl.getValues().get(i);
			else if (pvd.getValueType() == PlannerValueDef.TYPE_STRING ||
					pvd.getValueType() == PlannerValueDef.TYPE_LONG_STRING ||
					pvd.getValueType() == PlannerValueDef.TYPE_MULTI_LONG_STRING)
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE)
					stringBuffer += "\"" + pl.getValues().get(i) + "\"";
				else
					stringBuffer += "\"" + ((PlannerReference) pl.getValues().get(i)).getName() + "\"";
			else if (pvd.getValueType() == PlannerValueDef.TYPE_INT
					|| pvd.getValueType() == PlannerValueDef.TYPE_UNBOUNDED_INT) {
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE)
					stringBuffer += pl.getValues().get(i);
				else
					stringBuffer += PlannerFrame.referenceListByReferenceType.get(pvd.getRefersTo() - 1).indexOf(pl.getValues().get(i));
					// stringBuffer += (int) pl.getValues().get(i) - 1;
			} else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT) {
				String newVals = "";
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE) {
					String[] oldVals = ((String) pl.getValues().get(i)).split(",");
	
					for (int j = 0; j < oldVals.length; j++) {
						newVals = newVals + (Integer.parseInt(oldVals[j]) - 1);
						if (j + 1 <= oldVals.length)
							newVals += ",";
					}
				} else {
					@SuppressWarnings("unchecked")
					ArrayList<PlannerReference> refs = (ArrayList<PlannerReference>) pl.getValues().get(i);
					for (int j = 0; j < refs.size(); j++) {
						newVals = newVals + PlannerFrame.referenceListByReferenceType.get(pvd.getRefersTo() - 1).indexOf(refs.get(j));
						if (j + 1 <= refs.size())
							newVals += ",";
					}
				}

				stringBuffer += newVals;
			}
		}
		if (pl.isDefining())
			stringBuffer += ">";
		else
			stringBuffer += "/>";
		return stringBuffer;
	}

	/********************************************/
	/* Import data methods 						*/
	/********************************************/
	public void parseContainer(String path, PlannerTab plannerTab, String itemXmlTag, 
			Hashtable<String, PlannerContainerDef> containersByName) throws IOException {
		parseContainer(
				XMLParser.process(Files.readAllLines(
						Paths.get(path), StandardCharsets.UTF_8)),
				plannerTab, itemXmlTag, containersByName);
	}

	public void parseContainer(ArrayList<TagArea> tas, PlannerTab plannerTab,
			String allowableValue, Hashtable<String, PlannerContainerDef> containersByName) {
		for (TagArea ta : tas) {
			if (!ta.getTagType().equalsIgnoreCase(allowableValue))
				continue;
			// LOGGER.finest(ta.getTagType());
			PlannerContainerDef pcd = containersByName.get(ta.getTagType());
			PlannerContainer plannerContainer = new PlannerContainer(pcd, plannerTab);
			PlannerLine plannerLine = plannerContainer.getDefLine();
			parseLine(plannerLine, pcd.getDefiningLine(), ta);
			pcd.getDataLines().add(new PlannerReference(plannerContainer.getDescription()));

			plannerTab.addPlannerContainer(plannerContainer);

			for (TagArea taChild : ta.getChildren()) {
				boolean found = false;
				for (PlannerLineDef allowable : pcd.getAllowableLines()) {
					if (taChild.getTagType().equalsIgnoreCase(
							allowable.getTag())) {
						found = true;
						PlannerLine childLine = new PlannerLine(allowable,
								false);
						parseLine(childLine, allowable, taChild);
						plannerContainer.addLine(childLine);
					}
				}

				if (!found)
					LOGGER.warning("Unable to find tag definition for "
							+ taChild.getTagType());
			}
		}

		plannerTab.updateAttributeList(-1);
	}

	private void parseLine(PlannerLine plannerLine, PlannerLineDef pld,
			TagArea ta) {
		LOGGER.finest("PARENT: " + pld.getTag());
		for (PlannerValueDef pvd : pld.getPlannerValues()) {
			// Handle string values
			if (pvd.getValueType() == PlannerValueDef.TYPE_STRING || pvd.getValueType() == PlannerValueDef.TYPE_LONG_STRING ||
					pvd.getValueType() == PlannerValueDef.TYPE_MULTI_LONG_STRING)
				plannerLine.getValues().add(ta.getAttribute(pvd.getTag()));
					
			// Handle integer values
			else if (pvd.getValueType() == PlannerValueDef.TYPE_INT
					|| pvd.getValueType() == PlannerValueDef.TYPE_UNBOUNDED_INT) {
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE) {
					LOGGER.finest("TAG: " + pvd.getTag() + " "
							+ ta.getAttribute(pvd.getTag()));
					int value = 0;
					try {
						value = Integer.parseInt(ta.getAttribute(
								pvd.getTag()));
					} catch (NumberFormatException ex) {
					}

					plannerLine.getValues().add(value);
				}
				else
				{
					if (ta.getAttribute(pvd.getTag()) != null)
						plannerLine.getValues().add(Integer.parseInt(ta.getAttribute(pvd.getTag())) + 1);
					else
						plannerLine.getValues().add(0);
				}
			// Handle multiple int values
			} else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT) {
				String newVals = "";

				if (ta.getAttribute(pvd.getTag()) != null) {
					String[] values = ta.getAttribute(pvd.getTag())
							.split(",");
					for (int j = 0; j < values.length; j++) {
						newVals = newVals + (Integer.parseInt(values[j]) + 1);
						if (j + 1 != values.length)
							newVals = newVals + ",";
					}
				} else
					newVals = "0";

				plannerLine.getValues().add(newVals);
			// Handle boolean valuess
			} else if (pvd.getValueType() == PlannerValueDef.TYPE_BOOLEAN)
				plannerLine.getValues().add(
						Boolean.parseBoolean(ta.getAttribute(pvd.getTag())));

		}
	}
}
