package macs.hig.imt4072_project;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Knut Lucas Andersen
 */
public class StartApplication extends Activity  {
	private static final int REQUEST_IMAGE_CODE = 1001;
	private static TextView tvDefaultText;
	private static String imagePath;
	private static ArrayList<Integer> colourList;
	//has an image been opened
	private static boolean isImageLoaded;
	//has the image been saved
	private static boolean isImageSaved;
	//is this the first time the program runs
	private static boolean firstRun;
	//GridLayout containing all the colours found in image
	private static GridLayout colourGridLayout;
	private static ImageView imgView;
	private static Bitmap bitmap;
	@SuppressWarnings("unused")
	private static String imageUri;

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
		colourList = new ArrayList<Integer>();
		imgView = (ImageView) findViewById(R.id.importedImage);
		colourGridLayout = new GridLayout(this);
		tvDefaultText = (TextView) findViewById(R.id.tvDefaultText);
	} //initializeVariables

	public static void fillArrayListWithColours(Bitmap bitmap, ImageView imgView) {
		colourList.clear();
		HashSet<Integer> hashColours = new HashSet<Integer>();
		int colour = 0,
				width = 0,
				height = 0;
		//since size may vary, select the smallest format size
		width = (imgView.getWidth() > bitmap.getWidth()) ? bitmap.getWidth() : imgView.getWidth();
		height = (imgView.getHeight() > bitmap.getHeight()) ? bitmap.getHeight() : imgView.getHeight();
		for(int w = 0; w < width; w++) {
			for(int h = 0; h < height; h++) {
				colour = bitmap.getPixel(w, h);
				hashColours.add(colour);
			} //for
		} //for

		colourList.addAll(hashColours);

		//Sorting
		Collections.sort(colourList);/*, new Comparator<Integer>() {
			@Override
			public int compare(Integer colour1, Integer colour2) { 
				return  colour1.compareTo(colour2);
			}
		});
		ArrayList<String> test = new ArrayList<String>();
		for(int i = 0; i < colourList.size(); i++) {
			test.add(Integer.toHexString(colourList.get(i)));
		}
		
		Collections.sort(test);*/
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
			screenHeight = LayoutParams.WRAP_CONTENT;
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
					pixelColour = new TextView(colourGridLayout.getContext());
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
					pixelColour = new TextView(colourGridLayout.getContext());
					pixelColour.setWidth(GRID_CELL_PIXEL_SIZE);
					pixelColour.setHeight(GRID_CELL_PIXEL_SIZE);				
					pixelColour.setBackgroundColor(colourList.get(index));
					colourGridLayout.addView(pixelColour, new GridLayout.LayoutParams(rowSpec, GridLayout.spec(index)));
				} //for
			} //if			
			//add positioning rules for the gridlayout in parentview [doesn't work as intended]
			layoutParams.addRule(RelativeLayout.BELOW, R.id.relLayoutStart);		
			layoutParams.bottomMargin = RelativeLayout.ALIGN_PARENT_BOTTOM;
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
				discardImage();
			} //if
			//create an intent to open the gallery browser
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			String intentTitle = getString(R.string.get_image_intent_title);
			startActivityForResult(Intent.createChooser(intent, intentTitle), REQUEST_IMAGE_CODE);
			tvDefaultText.setVisibility(INVISIBLE);
		} catch (Exception ex) {
			String errorMsg = "Error:\n";
			Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
		} //try/catch
	} //openImage

	public static void updateInterface(ArrayList<Integer> colours) {
		colourList.clear();
		colourList.addAll(colours);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode != RESULT_CANCELED && resultCode == RESULT_OK) {
				if (requestCode == REQUEST_IMAGE_CODE) {
					//get the uri path to the selected image
					Uri selectedImageUri = data.getData();
					String sortOrder = null,
							selection = null;
					String[] projection = { MediaStore.Images.Media.DATA };
					String[] selectionArgs = null;
					//create query to get the selected picture/image
					Cursor cursor = getContentResolver().query(selectedImageUri, projection, selection, selectionArgs, sortOrder);
					int imageDataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					//get the pictures path and uri
					imagePath = cursor.getString(imageDataIndex);
					imageUri = selectedImageUri.toString();
					//close the cursor
					cursor.close();
					Toast.makeText(this, getString(R.string.selected_image_is_loading), Toast.LENGTH_LONG).show();
					//start loading the selected image
					ImageLoaderTask.loadSelectedImage(this.getResources(), imagePath, imgView, bitmap, getScreenWidth());
				} //if
			} //if
		} catch(RuntimeException ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Error: Could not load image!", Toast.LENGTH_LONG).show();
		} catch(Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Error: Could not load image!", Toast.LENGTH_LONG).show();
		} //try/catch
	} //onActivityResult

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
		try {
			createGrid();
		}catch (Exception ex) {
		} //try/catch
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