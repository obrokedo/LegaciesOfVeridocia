package mb.fc.utils.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mb.fc.engine.log.LoggingUtils;

public class PlannerReference {
	private static final Logger LOGGER = LoggingUtils.createLogger(PlannerReference.class);
	private String name;

	public PlannerReference(String name) {
		super();
		this.name = name;
	}

	/**
	 * This method is responsible reseting values of referencers when the referencee is deleted
	 * 
	 * @param referenceType the id (PlannerValueDef) that identifies the type of PlannerContainer that has been removed
	 * @param referenceIndex for int and multi-int fields the index of the item that was removed
	 */
	
	public static void removeReferences(int referenceType, int referenceIndex) {
		PlannerFrame.referenceListByReferenceType.get(referenceType - 1).remove(referenceIndex).setName("");
		
		/*
		for (int i = 0; i < jtp.getTabCount() - 2; i++) {
			for (PlannerContainer pcs : plannerTabs.get(i).getListPC()) {
				for (PlannerLine pl : pcs.getLines()) {
					for (int j = 0; j < pl.getPlDef().getPlannerValues().size(); j++) {
						PlannerValueDef pvd = pl.getPlDef().getPlannerValues()
								.get(j);

						if (pvd.getRefersTo() == referenceType
								&& pvd.getValueType() == PlannerValueDef.TYPE_INT ||
									pvd.getValueType() == PlannerValueDef.TYPE_UNBOUNDED_INT) {
							if ((int) pl.getValues().get(j) == referenceIndex + 1)
								pl.getValues().set(j, 0);
							else if ((int) pl.getValues().get(j) > referenceIndex + 1) {
								pl.getValues().set(j,
										(int) pl.getValues().get(j) - 1);
							}
						}
					}
				}
			}
		}
		*/
	}
	
	
	public static void establishReferences(List<PlannerTab> tabsWithReferences, ArrayList<ArrayList<PlannerReference>> referenceListByReferenceType) {
		List<String> badReferences = new ArrayList<>();
		
		for (PlannerTab plannerTab : tabsWithReferences) {
			for (PlannerContainer plannerContainer : plannerTab.getListPC()) {
				establishLineReference(referenceListByReferenceType, badReferences, plannerContainer, plannerContainer.getDefLine());
				for (PlannerLine pl : plannerContainer.getLines()) {
					establishLineReference(referenceListByReferenceType, badReferences, plannerContainer, pl);
				}
			}
		}
		
		displayBadReferences(badReferences);
	}

	public static void displayBadReferences(List<String> badReferences) {
		// Display errors
		if (badReferences.size() > 0) {
			JTextArea errors = new JTextArea();
			JScrollPane errorsScroll = new JScrollPane(errors);
			
			StringBuffer stringBuf = new StringBuffer();
			for (String badRef : badReferences) {
				stringBuf.append(badRef + System.lineSeparator());
				LOGGER.fine(badRef);
				System.out.println(badRef);
			}
			errors.setText(stringBuf.toString());
			JOptionPane.showMessageDialog(null, errorsScroll);
		}
	}

	public static void establishLineReference(ArrayList<ArrayList<PlannerReference>> referenceListByReferenceType,
			List<String> badReferences, PlannerContainer plannerContainer, PlannerLine pl) {
		for (int j = 0; j < pl.getPlDef().getPlannerValues().size() && j < pl.getValues().size(); j++) {
			PlannerValueDef pvd = pl.getPlDef().getPlannerValues().get(j);
			
			if (pvd.getRefersTo() != PlannerValueDef.REFERS_NONE && 
					!(pl.getValues().get(j) instanceof PlannerReference) &&
					!(pl.getValues().get(j) instanceof ArrayList<?>)) {
				ArrayList<PlannerReference> references = referenceListByReferenceType.get(pvd.getRefersTo() - 1);
				if (pvd.getValueType() == PlannerValueDef.TYPE_INT) {
					// Unfortunately it's difficult to find errors here since we don't know whether the index they 
					// point to is reasonable or not, settle for indicating out of range errors
					int index = (int) pl.getValues().get(j) - 1;
					pl.getValues().set(j, establishIntReference(index, badReferences, plannerContainer, pl, j, references, pvd));
				} else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT) {
					String[] vals = ((String) pl.getValues().get(j)).split(",");
					List<PlannerReference> multiIntList = new ArrayList<PlannerReference>();
					for (String val : vals) {
						int valParsed = Integer.parseInt(val) - 1;
						multiIntList.add(establishIntReference(valParsed, badReferences, plannerContainer, pl, j, references, pvd));
					}
					pl.getValues().set(j, multiIntList);
				} else if (pvd.getValueType() == PlannerValueDef.TYPE_STRING) {
					int referenceIndex = references.indexOf(new PlannerReference((String) pl.getValues().get(j)));
					if (referenceIndex != -1) {
						pl.getValues().set(j, references.get(referenceIndex));
					} else if (pvd.isOptional()) {
						pl.getValues().set(j, new PlannerReference(""));
					} else {
						if (plannerContainer != null) {
							badReferences.add(plannerContainer.getDefLine().getPlDef().getName() + " named: " + 
								plannerContainer.getDefLine().getValues().get(0) + " with attribute " + 
								pl.getPlDef().getName() + 
								" has a bad reference to '" + (String) pl.getValues().get(j) + "' on it's " + pl.getPlDef().getPlannerValues().get(j).getDisplayTag() + " value");
						} else {
							badReferences.add("Attribute " + pl.getPlDef().getName() + 
								" has a bad reference to '" + (String) pl.getValues().get(j) + "' on it's " + pl.getPlDef().getPlannerValues().get(j).getDisplayTag() + " value");
						}
						//badReferences.add(pl.getValues().get(0) + " of type " + plannerContainer.getDefLine().getPlDef().getName() + 
							//	" has a bad reference to " + (String) pl.getValues().get(j) + " of type " + pvd.getRefersTo());
						pl.getValues().set(j, new PlannerReference(""));
					}
				}
			}
		}
	}

	private static PlannerReference establishIntReference(int index, List<String> badReferences, PlannerContainer plannerContainer, PlannerLine pl, int j,
			ArrayList<PlannerReference> references, PlannerValueDef pvd) {
		if (index < 0 || index >= references.size()) {
			if (!pvd.isOptional()) {
				if (plannerContainer != null) {
					badReferences.add(plannerContainer.getDefLine().getPlDef().getName() + " named: " + 
						plannerContainer.getDefLine().getValues().get(0) + " with attribute " + 
						pl.getPlDef().getName() + 
						" has a bad reference to index " + index + " on it's " + pvd.getDisplayTag() + " value");
				} else {
					badReferences.add("Attribute " + pl.getPlDef().getName() + 
						" has a bad reference to index " + index + " on it's " + pvd.getDisplayTag() + " value");
				}
			}
			return new PlannerReference("");
		} else {
			return references.get(index);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlannerReference other = (PlannerReference) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlannerReference [name=" + name + "]";
	}
}