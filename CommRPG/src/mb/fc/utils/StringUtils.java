package mb.fc.utils;

public class StringUtils {
	public static boolean isEmpty(String str)
	{
		return (str == null || str.trim().length() == 0 || str.trim().equalsIgnoreCase("null"));
	}
	
	public static boolean isNotEmpty(String str)
	{
		return !isEmpty(str);
	}
}
