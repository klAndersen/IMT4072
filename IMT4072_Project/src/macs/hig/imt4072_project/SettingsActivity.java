package macs.hig.imt4072_project;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 
 * @author Knut Lucas Andersen
 */
public class SettingsActivity extends Activity {
	//get the saved values from sharedpreferences
	SharedPreferences sharedPreferences;
	private static Context context;
	private static CheckBox chkNotificationOverwrite;
	private static Spinner spinnerSetPixelSize;
	private static TextView tvNotificationOverwrite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		context = this;
		sharedPreferences = Filestorage.getSharedPreferances(this);
		setValuesCheckboxes();
		setValueSpinner();
	} //onCreate

	/**
	 * Sets the values in the checkboxes
	 */
	private void setValuesCheckboxes() {
		CheckBox checkBox;
		boolean enabled = false;
		//set checked checked for chkUseDefaultColourProfile
		boolean checked = sharedPreferences.getBoolean(Filestorage.USE_DEFAULT_COLOUR_PROFILE_KEY, true);
		checkBox = (CheckBox) findViewById(R.id.chkUseDefaultColourProfile);
		checkBox.setChecked(checked);
		//set checked checked for chkOverwriteOriginalImage
		checked = sharedPreferences.getBoolean(Filestorage.OVERWRITE_ORIGINAL_IMAGE_KEY, false);
		checkBox = (CheckBox) findViewById(R.id.chkOverwriteOriginalImage);
		checkBox.setChecked(checked);
		enabled = checkBox.isChecked();
		//set checked checked for chkNotificationOverwrite
		checked = sharedPreferences.getBoolean(Filestorage.NOTIFY_ABOUT_OWERWRITING_ORIGINAL_KEY, false);
		chkNotificationOverwrite = (CheckBox) findViewById(R.id.chkNotificationOverwrite);
		tvNotificationOverwrite = (TextView) findViewById(R.id.tvNotificationOverwrite);
		tvNotificationOverwrite.setEnabled(enabled);
		chkNotificationOverwrite.setEnabled(enabled);
		chkNotificationOverwrite.setChecked(checked);
		//set checked checked for chkEnableUndoFunction
		checked = sharedPreferences.getBoolean(Filestorage.ENABLE_UNDO_FUNCTION_KEY, false);
		checkBox = (CheckBox) findViewById(R.id.chkEnableUndoFunction);
		checkBox.setChecked(checked);
	} //setValuesCheckboxes

	private void setValueSpinner() {
		try {
			int defaultIndex = 0, 
					selectedIndex = 0;
			ArrayAdapter<Integer> adapter;
			spinnerSetPixelSize = (Spinner) findViewById(R.id.spinnerSetPixelSize);
			defaultIndex = getResources().getInteger(R.integer.default_pixel_size_index);
			adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item);
			int[] pixelSizeArray = getResources().getIntArray(R.array.pixel_cell_size_array);
			selectedIndex = sharedPreferences.getInt(Filestorage.SET_PIXEL_CELL_SIZE_INDEX, defaultIndex);
			//loop through array and add elements to the adapter
			for(int index = 0; index < pixelSizeArray.length; index++) {
				adapter.add(pixelSizeArray[index]);
			} //for
			spinnerSetPixelSize.setAdapter(adapter);
			spinnerSetPixelSize.setSelection(selectedIndex);
			//create listener for the spinner
			spinnerSetPixelSize.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
					//get the index for the selected element and save the index in sharedpreferences
					int item = spinnerSetPixelSize.getSelectedItemPosition();
					Filestorage.savePixelCellSizeIndex(context, item);
				} //onItemSelected
				@Override
				public void onNothingSelected(AdapterView<?> arg0) { 
					//nothing selcted, don't do anything
				} //onNothingSelected
			}); //setOnItemSelectedListener
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		} //try/catch
	} //setValueSpinner

	public void useDefaultColourProfile(View v) {
		CheckBox chkUseDefaultColourProfile = (CheckBox) v.findViewById(R.id.chkUseDefaultColourProfile);
		boolean checked = chkUseDefaultColourProfile.isChecked();
		Filestorage.saveColurProfileUseOption(this, checked);
	} //useDefaultColourProfile

	public void setOverwriteImage(View v) {
		CheckBox chkOverwriteOriginalImage = (CheckBox) v.findViewById(R.id.chkOverwriteOriginalImage);
		boolean checked = chkOverwriteOriginalImage.isChecked();
		Filestorage.saveOverwriteImageOption(this, checked);
		chkNotificationOverwrite.setEnabled(checked); 
		tvNotificationOverwrite.setEnabled(checked);
		if(!checked) {
			chkNotificationOverwrite.setChecked(checked);
		} //if
	} //setOverwriteImage

	public void setOverwriteImageNotification(View v) {
		CheckBox chkNotificationOverwrite = (CheckBox) v.findViewById(R.id.chkNotificationOverwrite);
		boolean checked = chkNotificationOverwrite.isChecked();
		Filestorage.saveNotifyOverwriteOption(this, checked);
	} //setOverwriteImageNotification

	public void setEnableUndoFunction(View v) {
		CheckBox chkEnableUndoFunction = (CheckBox) v.findViewById(R.id.chkEnableUndoFunction);
		boolean checked = chkEnableUndoFunction.isChecked();
		Filestorage.saveEnabledUndoOption(this, checked);
	} //setEnableUndoFunction

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	} //onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} //if
		return super.onOptionsItemSelected(item);
	} //onOptionsItemSelected
} //SettingsActivity