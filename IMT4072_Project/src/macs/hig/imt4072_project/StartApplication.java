package macs.hig.imt4072_project;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Knut Lucas Andersen
 */
public class StartApplication extends Activity  {
	TextView tvDefaultText;
	@SuppressWarnings("unused")
	private String imagePath;
	ArrayList<Integer> colourList;
	//has an image been opened
	private boolean isImageLoaded;
	//has the image been saved
	private boolean isImageSaved;
	//is this the first time the program runs
	private boolean firstRun;
	//GridLayout containing all the colours found in image
	private GridLayout colourGridLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_start_application);
			initializeVariables();
		} catch (RuntimeException ex) {
			//may occur if failing to create UI
			ex.printStackTrace();
		} catch (Exception ex) {
			//print errors
			ex.printStackTrace();
		} //try/catch
	} //onCreate

	private void initializeVariables() {
		imagePath = "";
		firstRun = true;
		isImageSaved = true;
		isImageLoaded = false;
		colourGridLayout = new GridLayout(this);
		tvDefaultText = (TextView) findViewById(R.id.tvDefaultText);
		//TODO: move this
		fillArrayListWithColours();
	} //initializeVariables

	private void fillArrayListWithColours() {
		//TODO: 
		//change this into working code
		colourList = new ArrayList<Integer>();
		colourList.add(Color.BLACK);
		colourList.add(Color.RED);
		colourList.add(Color.GREEN);
		colourList.add(Color.BLUE);
		colourList.add(Color.WHITE);
		//*256*256
		int XtremeNo = 240;
		for(int i = 5; i < XtremeNo; i++) {
			colourList.add(Color.BLUE);	
		} //for
	} //fillArrayListWithColours

	private final int getCellPixelSize() {
		SharedPreferences sharedPreferences = Filestorage.getSharedPreferances(this);
		int index = 0,
				defaultIndex = 0;
		int[] pixelSizeArray = getResources().getIntArray(R.array.pixel_cell_size_array);
		defaultIndex = getResources().getInteger(R.integer.default_pixel_size_index);
		index = sharedPreferences.getInt(Filestorage.SET_PIXEL_CELL_SIZE_INDEX, defaultIndex);
		return pixelSizeArray[index];
	} //getCellPixelSize

	private int getScreenWidth() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	} //getScreenWidth

	private void createGrid() {
		try {
			/** Constant for the pixel size of each grid cell **/
			final int GRID_CELL_PIXEL_SIZE = getCellPixelSize();
			int screenWidth = 0,
					screenHeight = 0,
					widthNeeded = 0;
			//textview functioning as a cell with pixel colour
			TextView pixelColour;
			screenWidth = getScreenWidth();
			screenHeight = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			LayoutParams layoutParams = new LayoutParams(screenWidth, screenHeight);
			//since the gridlayout is added programatically, one need to remove 
			//and re-draw it in case it has been updated from the settings screen
			if (!firstRun) {
				ViewGroup vg = (ViewGroup)(colourGridLayout.getParent());
				vg.removeView(colourGridLayout);
				colourGridLayout.removeAllViews();
				colourGridLayout = new GridLayout(this);
			} //if
			//check how many pixels are needed to draw the grid
			widthNeeded = GRID_CELL_PIXEL_SIZE * colourList.size();
			//does the width thats needed exceed the display space?	
			if (widthNeeded > screenWidth) {				
				int rowIndex = 0,
						columnIndex = 0,
						usedWidthSpace = 0;
				//loop through the colours in the arraylist
				for(int index = 0; index < colourList.size(); index++) {
					//create a textview that will contain current pixel colour
					pixelColour = new TextView(this);
					pixelColour.setWidth(GRID_CELL_PIXEL_SIZE);
					pixelColour.setHeight(GRID_CELL_PIXEL_SIZE);				
					pixelColour.setBackgroundColor(colourList.get(index));
					//calculate the widthspace used and check if it has exceeded screen width
					usedWidthSpace = (columnIndex*GRID_CELL_PIXEL_SIZE) + GRID_CELL_PIXEL_SIZE;
					if (usedWidthSpace > screenWidth) {
						columnIndex = 0;
						rowIndex++;
					} //if
					colourGridLayout.addView(pixelColour, new GridLayout.LayoutParams(GridLayout.spec(rowIndex), 
							GridLayout.spec(columnIndex)));
					columnIndex++;
				} //for
			} else {
				//needed width does not exceed display width, so just draw grid
				GridLayout.Spec rowSpec = GridLayout.spec(0);
				for(int index = 0; index < colourList.size(); index++) {
					//create a textview that will contain current pixel colour
					pixelColour = new TextView(this);
					pixelColour.setWidth(GRID_CELL_PIXEL_SIZE);
					pixelColour.setHeight(GRID_CELL_PIXEL_SIZE);				
					pixelColour.setBackgroundColor(colourList.get(index));
					colourGridLayout.addView(pixelColour, new GridLayout.LayoutParams(rowSpec, GridLayout.spec(index)));
				} //for
			} //if
			//add positioning rules for the gridlayout in parentview
			layoutParams.addRule(RelativeLayout.BELOW, R.id.importedImage);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			//add gridlayout to the view
			this.addContentView(colourGridLayout, layoutParams);
			//since at least one run has now been completed, 
			//set that this is no longer the first run
			firstRun = false;
		} catch (StackOverflowError ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //try/catch
	} //createGrid

	/**
	 * 
	 */
	public void openImage() {
		try {
			//is an image already been loaded?
			if (isImageLoaded) {
				//has the image been saved (if altered)?
				if (!isImageSaved) {
					//ask if image should be saved
				} //if
			} //if
			tvDefaultText.setVisibility(INVISIBLE);
		} catch (Exception ex) {
			String errorMsg = "Error:\n";
			Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
		} //try/catch
	} //openImage

	/**
	 * 
	 */
	public void saveImage() {
		try {			
			//was an image opened?
			if (isImageLoaded) {

			} //if
		} catch (Exception ex) {
			String errorMsg = "Error:\n";
			Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
		} //try/catch
	} //saveImage

	/**
	 * 
	 */
	public void discardImage() {
		try { 
			//was an image opened?
			if (isImageLoaded) {
				//has the image been saved?
				if (!isImageSaved) {
					//ask if image should be saved
				} else {
					//image saved, close/remove image

					tvDefaultText.setVisibility(VISIBLE);
				} //if
			} //if
		} catch (Exception ex) {
			String errorMsg = "Error:\n";
			Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
		} //try/catch
	} //discardImage

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_application, menu);
		return true;
	} //onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_open_image:
			openImage();
			return true;
		case R.id.action_save_image:
			saveImage();
			return true;
		case R.id.action_discard_image:
			discardImage();
			return true;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		} //switch
	} //onOptionsItemSelected

	@Override
	protected void onResume() {
		super.onResume();
		createGrid();
	} //onPostResume

	@Override
	protected void onStop() {
		super.onStop();
	} //onStop

	@Override
	protected void onDestroy() {
		super.onDestroy();
	} //onDestroy
} //StartApplication