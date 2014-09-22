package macs.hig.imt4072_project;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class that saves values related to settings in SharedPreferences, 
 * and reads/writes to files internally and externally (SD-Card).
 * @author Knut Lucas Andersen
 */
public final class Filestorage {
	/** SharedPreference value key:  **/
	protected static final String USE_DEFAULT_COLOUR_PROFILE_KEY = "useDefaultProfile";
	/** SharedPreference value key:  **/
	protected static final String OVERWRITE_ORIGINAL_IMAGE_KEY = "overwriteOriginalImage";
	/** SharedPreference value key:  **/
	protected static final String NOTIFY_ABOUT_OWERWRITING_ORIGINAL_KEY = "notifyOriginalOverwritten";
	/** SharedPreference value key:  **/
	protected static final String SET_PIXEL_CELL_SIZE_INDEX = "setPixelCellSizeIndex";
	/** SharedPreference value key: Enable/disable undo function **/
	protected static final String ENABLE_UNDO_FUNCTION_KEY = "enableUndoFunction";
	/** This applications SharedPreference **/
	protected static final String SHARED_PREFERENCES_SETTINGS = "settingsPreferance";

	private Filestorage() {
		throw new UnsupportedOperationException();
	} //constructor

	/**
	 * Returns an object of SharedPreferences that contains the chosen settings for the application
	 * @param context - Context
	 * @return SharedPreferences: The object that contains the settings displayed on SettingsActivity
	 */
	protected static SharedPreferences getSharedPreferances(Context context) {
		//only this application will use the given sharedpreference
		int mode = Context.MODE_PRIVATE;
		//create and retrieve the shared preferance
		SharedPreferences sharedPreferance = context.getSharedPreferences(SHARED_PREFERENCES_SETTINGS, mode);
		return sharedPreferance;
	} //getSharedPreferances

	/**
	 * Saves the value for whether or not to use the default colour profile
	 * @param context - Context
	 * @param useDefaultProfile - boolean: True - use default profile, <br />
	 * false - use image-specfic colour profile
	 */
	public static void saveColurProfileUseOption(Context context, final boolean useDefaultProfile) {
		//create an editor to edit the sharedpreferences
		SharedPreferences.Editor spEditor = getSharedPreferances(context).edit();
		//save the value in sharedpreferences
		spEditor.putBoolean(USE_DEFAULT_COLOUR_PROFILE_KEY, useDefaultProfile);
		spEditor.commit();
	} //saveColurProfileUseOption

	/**
	 * Saves the value for whether or not to overwrite the original image when saving
	 * @param context - Context
	 * @param overwriteOriginal - boolean: True - overwrite original image, <br />
	 * false - create a copy of original image
	 */
	public static void saveOverwriteImageOption(Context context, final boolean overwriteOriginal) {
		//create an editor to edit the sharedpreferences
		SharedPreferences.Editor spEditor = getSharedPreferances(context).edit();
		//save the value in sharedpreferences
		spEditor.putBoolean(OVERWRITE_ORIGINAL_IMAGE_KEY, overwriteOriginal);
		spEditor.commit();
	} //saveOverwriteImageOption

	/**
	 * Saves the value for whether or not to notify each time that original image will 
	 * be overwritten
	 * @param context - Context
	 * @param notifyOverwrite - boolean: True - notify every time that original image will be overwritten, <br />
	 * false - don't notify that original image will be replaced/overwritten
	 */
	public static void saveNotifyOverwriteOption(Context context, final boolean notifyOverwrite) {
		//create an editor to edit the sharedpreferences
		SharedPreferences.Editor spEditor = getSharedPreferances(context).edit();
		//save the value in sharedpreferences
		spEditor.putBoolean(NOTIFY_ABOUT_OWERWRITING_ORIGINAL_KEY, notifyOverwrite);
		spEditor.commit();
	} //saveNotifyOverwriteOption

	/**
	 * Saves the index for selected value for the displayed colour pixels
	 * @param context - Context
	 * @param pixelCellSizeIndex - int: size of pixel in each grid
	 */
	public static void savePixelCellSizeIndex(Context context, final int pixelCellSizeIndex) {
		//create an editor to edit the sharedpreferences
		SharedPreferences.Editor spEditor = getSharedPreferances(context).edit();
		//save the value in sharedpreferences
		spEditor.putInt(SET_PIXEL_CELL_SIZE_INDEX, pixelCellSizeIndex);
		spEditor.commit();
	} //savePixelCellSizeIndex

	/**
	 * Saves the value for whether or not to enable the undo function <br />
	 * (UNDO is not implemented)
	 * @param context - Context
	 * @param enableUndo - boolean: True - enable undo function, <br />
	 * false - undo function not enabled
	 */
	public static void saveEnabledUndoOption(Context context, final boolean enableUndo) {
		//create an editor to edit the sharedpreferences
		SharedPreferences.Editor spEditor = getSharedPreferances(context).edit();
		//save the value in sharedpreferences
		spEditor.putBoolean(ENABLE_UNDO_FUNCTION_KEY, enableUndo);
		spEditor.commit();
	} //saveEnabledUndoOption

} //Filestorage