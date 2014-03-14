package mb.fc.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import mb.fc.resource.FCResourceManager;

public class XMLParser 
{
	public static class TagArea
	{
		private Hashtable<String, String> params;
		private ArrayList<TagArea> children;
		private String tagType;
		
		public TagArea(String line) throws IOException
		{			
			// Iterate through the line changing all of the spaces contained in quotations to something else to something else
			int end = 0;
			while (true)
			{
				int start = line.indexOf("\"", end + 1);
				if (start == -1)
					break;
				end = line.indexOf("\"", start + 1);
				if (end == -1)
					throw new IOException("Unmatched quotation mark on line: " + line);
				
				
				String original = line.substring(start + 1, end);
				if (!original.contains(" "))
					continue;
				String fixed = original.replaceAll(" ", "_");
				line = line.replace(original, fixed);
			}
			
			line = line.replaceAll("  ", " ");
			String[] split = line.split(" ");			
			tagType = split[0].replace("<", "").replace(">", "").replace("/>", "");			
			// System.out.println(tagType);
			params = new Hashtable<String, String>();
			children = new ArrayList<TagArea>();						
						
			for (int i = 1; i < split.length; i++)
			{
				String s = split[i];
				s = s.replace("/>", "");
				s = s.replace(">", "");
				s = s.replace("\"", "");
				
				String[] splitParm = s.split("=", 2);
				params.put(splitParm[0], splitParm[1].replace("_", " "));
				// System.out.println("\t" + splitParm[0] + " " + splitParm[1]);
			}
		}
		
		public void addChild(TagArea ta)
		{
			children.add(ta);
		}

		public Hashtable<String, String> getParams() {
			return params;
		}

		public ArrayList<TagArea> getChildren() {
			return children;
		}

		public String getTagType() {
			return tagType;
		}
	}
	
	public static ArrayList<TagArea> process(String file, Class<?> cl) throws IOException
	{
		ArrayList<TagArea> parents = new ArrayList<TagArea>();
		
		List<String> allLines = FCResourceManager.readAllLines(file, cl);
		Stack<TagArea> openTags = new Stack<TagArea>();
		for (String s : allLines)
		{				
			s = s.trim();
			if (s.startsWith("<!") || s.startsWith("<?"))
				continue;
			
			if (s.startsWith("</"))
			{
				openTags.pop();
			}
			else if (s.startsWith("<"))
			{
				TagArea ta = new TagArea(s);
				
				if (openTags.size() > 0)
				{
					openTags.peek().addChild(ta);
					openTags.push(ta);
				}
				else
				{
					openTags.push(ta);
					parents.add(ta);
				}
				
				if (s.endsWith("/>"))
					openTags.pop();
			}
		}
		return parents;
	}
}
