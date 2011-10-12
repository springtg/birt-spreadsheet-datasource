package de.cbrell.birt.report.data.oda.spreadsheet.ui.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
	private static ResourceBundle rb = ResourceBundle.getBundle( 
			"de.cbrell.birt.report.data.oda.spreadsheet.ui.i18n.Messages" );
	
	public static String getMessage( String key )
	{
		try
		{
			if ( rb != null )
				return rb.getString( key );
			// Fall through to return key
		}
		catch ( MissingResourceException e )
		{
		}
		return  " #" + key + "# ";		
	}
	
	public static String getString(String key) {
		return getMessage(key);
	}
	
	public static String formatMessage( String key, Object[] args) 
	{
		return MessageFormat.format( getMessage(key), args);
	}
}
