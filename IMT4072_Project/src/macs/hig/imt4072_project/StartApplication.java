package macs.hig.imt4072_project;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

//java imports
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
//android imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
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
//own library package import
import macs.hig.imt4072_library.*;

/**
 * Starting point for the application
 * @author Knut Lucas Andersen
 */
public class StartApplication extends Activity  {
	private static final int REQUEST_IMAGE_CODE = 1001;
	/** default display text when no image is loaded **/
	private static TextView tvDefaultText;
	/** path to image **/
	private static String _imagePath;
	/** list containing the colours in the loaded image **/
	private static ArrayList<Integer> _colourList;
	/** has an image been opened? **/
	private static boolean _isImageLoaded;
	/** has the image been saved **/
	private static boolean _isImageSaved;
	/** has a copy of this image been saved **/
	private static boolean _isCopySaved;
	/** is this the first time the program runs **/
	private static boolean _firstRun;
	/** GridLayout containing all the colours found in image **/
	private static GridLayout colourGridLayout;
	/** Imageview displaying image **/
	private static ImageView imgView;
	/** Bitmap object containing the loaded image **/
	private static Bitmap _bitmap;
	@SuppressWarnings("unused")
	private static String _imageUri;
	/** Context object for this activity **/
	private static Context _context;
	/** Color space operations **/
	private static ColorSpaceOperations _csOperations; 
	
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
		_imagePath = "";
		_context = this;
		_firstRun = true;
		_isCopySaved = false;
		_isImageSaved = true;
		_isImageLoaded = false;
		_bitmap = null;
		_colourList = new ArrayList<Integer>();
		colourGridLayout = new GridLayout(this);
		imgView = (ImageView) findViewById(R.id.importedImage);
		tvDefaultText = (TextView) findViewById(R.id.tvDefaultText);
	} //initializeVariables

	/**
	 * Fills an arraylist with the colours that exists in the passed bitmap.
	 * The imageview is used to get the width and height of image, to be able 
	 * to loop through all the pixels in the image. A HashSet is used first, since 
	 * this ignores duplicates. Colours are then sorted and the gridlayout is then 
	 * created.
	 * @param bitmap - Bitmap: Bitmap of image
	 * @param imgView - ImageView: Imageview containing the image
	 * @see {@link StartApplication#createGrid()}
	 */
	public static void fillArrayListWithColours(Bitmap bitmap, ImageView imgView) {
		_bitmap = bitmap;
		_colourList.clear();
		//using hashset since this ignores duplicates
		HashSet<Integer> hashColours = new HashSet<Integer>();
		int colour = 0,
				width = 0,
				height = 0;
		//since size may vary, select the smallest format size
		width = (imgView.getWidth() > _bitmap.getWidth()) ? _bitmap.getWidth() : imgView.getWidth();
		height = (imgView.getHeight() > _bitmap.getHeight()) ? _bitmap.getHeight() : imgView.getHeight();
		for(int w = 0; w < width; w++) {
			for(int h = 0; h < height; h++) {
				colour = _bitmap.getPixel(w, h);
				hashColours.add(colour);
			} //for
		} //for
		_colourList.addAll(hashColours);
		//Sorting
		Collections.sort(_colourList);
		createColorProfileFromImage();
		createGrid();
	} //fillArrayListWithColours
	
	private static void createColorProfileFromImage() {
		
	} //createColorProfileFromImage

	/**
	 * Returns an array containing the pixel cell sizes used in the gridlayout
	 * @return int[]: Array containing the different cell sizes for the gridlayout
	 */
	private final static int getCellPixelSize() {
		SharedPreferences sharedPreferences = Filestorage.getSharedPreferances(_context);
		int index = 0,
				defaultIndex = 0;
		int[] pixelSizeArray = _context.getResources().getIntArray(R.array.pixel_cell_size_array);
		defaultIndex = _context.getResources().getInteger(R.integer.default_pixel_size_index);
		index = sharedPreferences.getInt(Filestorage.SET_PIXEL_CELL_SIZE_INDEX, defaultIndex);
		return pixelSizeArray[index];
	} //getCellPixelSize

	/**
	 * Get the screen width of this device.
	 * @return {@link DisplayMetrics#widthPixels}
	 */
	private static int getScreenWidth() {
		DisplayMetrics displaymetrics = _context.getResources().getDisplayMetrics();
		return displaymetrics.widthPixels;
	} //getScreenWidth

	/**
	 * Creates a colour grid containing all the colours found in the image.
	 * It contains two different loops, depending on whether or not all 
	 * the colour cells can fit within the given width of the screen. If more 
	 * lines are needed, the second loop creates multiple rows within the 
	 * GridLayout.
	 */
	private static void createGrid() {
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
			if (!_firstRun) {
				removeGrid();
			} //if
			//check how many pixels are needed to draw the grid
			widthNeeded = GRID_CELL_PIXEL_SIZE * _colourList.size();
			//does the width thats needed exceed the display space?	
			if (widthNeeded > screenWidth) {				
				int rowIndex = 0,
						columnIndex = 0,
						usedWidthSpace = 0;
				//loop through the colours in the arraylist
				for(int index = 0; index < _colourList.size(); index++) {
					//create a textview that will contain current pixel colour
					pixelColour = new TextView(colourGridLayout.getContext());
					pixelColour.setWidth(GRID_CELL_PIXEL_SIZE);
					pixelColour.setHeight(GRID_CELL_PIXEL_SIZE);				
					pixelColour.setBackgroundColor(_colourList.get(index));
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
				for(int index = 0; index < _colourList.size(); index++) {
					//create a textview that will contain current pixel colour
					pixelColour = new TextView(colourGridLayout.getContext());
					pixelColour.setWidth(GRID_CELL_PIXEL_SIZE);
					pixelColour.setHeight(GRID_CELL_PIXEL_SIZE);				
					pixelColour.setBackgroundColor(_colourList.get(index));
					colourGridLayout.addView(pixelColour, new GridLayout.LayoutParams(rowSpec, GridLayout.spec(index)));
				} //for
			} //if			
			//add positioning rules for the gridlayout in parentview [doesn't work as intended]
			layoutParams.addRule(RelativeLayout.BELOW, R.id.relLayoutStart);		
			layoutParams.bottomMargin = RelativeLayout.ALIGN_PARENT_BOTTOM;
			//add gridlayout to the view
			((Activity) _context).addContentView(colourGridLayout, layoutParams);
			//since at least one run has now been completed, 
			//set that this is no longer the first run
			_firstRun = false;
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
			Toast.makeText(_context, _context.getString(R.string.exception_out_of_memory), Toast.LENGTH_LONG).show();
		} catch (StackOverflowError ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //try/catch
	} //createGrid

	/**
	 * Removes the colour grid if it has been added to the screen.
	 */
	private static void removeGrid() {
		//has the view been created?
		if (colourGridLayout.getParent() != null) {
			ViewGroup vg = (ViewGroup)(colourGridLayout.getParent());
			vg.removeView(colourGridLayout);
			colourGridLayout.removeAllViews();
			colourGridLayout = new GridLayout(_context);
		} //if
	} //removeGrid

	/**
	 * Opens an image for editing. If an image is already 
	 * opened, a check is done to see if the image is unsaved, 
	 * if the image is unsaved, discardImage() is called.
	 * @see StartApplication#discardImage()
	 */
	public void openImage() {
		try {
			//check if image is opened and unsaved?
			if (_isImageLoaded && !_isImageSaved) {
				//discard open image
				discardImage();
			} else {
				clearStartScreen();
				//create an intent to open the gallery browser
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				String intentTitle = getString(R.string.get_image_intent_title);
				startActivityForResult(Intent.createChooser(intent, intentTitle), REQUEST_IMAGE_CODE);
				_isCopySaved = false;
				_isImageSaved = true;
				_isImageLoaded = true;
				tvDefaultText.setVisibility(INVISIBLE);
			} //if
		} catch (Exception ex) {
			ex.printStackTrace();
			String errorMsg = "Error:\n";
			Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
		} //try/catch
	} //openImage

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
					_imagePath = cursor.getString(imageDataIndex);
					_imageUri = selectedImageUri.toString();
					//close the cursor
					cursor.close();
					Toast.makeText(this, getString(R.string.selected_image_is_loading), Toast.LENGTH_LONG).show();
					//start loading the selected image
					ImageLoaderTask.loadSelectedImage(this.getResources(), _imagePath, imgView, _bitmap, getScreenWidth());
				} //if
			} //if
		} catch(RuntimeException ex) {
			ex.printStackTrace();
			Toast.makeText(this, getString(R.string.exception_failed_loading_image), Toast.LENGTH_LONG).show();
		} catch(Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, getString(R.string.exception_failed_loading_image), Toast.LENGTH_LONG).show();
		} //try/catch
	} //onActivityResult

	/**
	 * Checks if an image has been opened and loaded, and then continues 
	 * with a check to see if the original image is to be overwritten or if 
	 * a copy should be made. If this is the first save and it should be saved 
	 * as copy, an extension is made to the filename. Then the content of the 
	 * imageview is copied into the bitmap, which is then compressed and flushed 
	 * to file by use of FileOutputStream.
	 */
	public void saveImage() {
		//was an image opened?
		if (_isImageLoaded) {
			try {
				//create a stream to write image to file
				String key = Filestorage.OVERWRITE_ORIGINAL_IMAGE_KEY;
				SharedPreferences sharedPreferences = Filestorage.getSharedPreferances(_context);
				boolean overWriteOriginal = sharedPreferences.getBoolean(key, false);
				//is the original to be kept and has the copy been saved as a new version?
				if (!overWriteOriginal && !_isCopySaved) {
					char period = '.';
					_isCopySaved = true;
					String additionalName = "_1";
					int periodIndex = _imagePath.lastIndexOf(period);					
					_imagePath = _imagePath.substring(0, periodIndex) + additionalName + _imagePath.substring(periodIndex);					
				} //if 
				FileOutputStream writeToFile = new FileOutputStream(_imagePath);
				_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, writeToFile);
				//flush stream to assure data gets written and close the stream writer 
				writeToFile.flush();
				writeToFile.close();
				_isImageSaved = true;
				Toast.makeText(_context, _context.getString(R.string.image_changes_saved), Toast.LENGTH_LONG).show();
			} catch (Exception ex) {
				ex.printStackTrace();
				String errorMsg = "Error:\n";
				Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
			} //try/catch
		} //if
	} //saveImage

	/**
	 * Shows a messagebox (AlertDialog) asking the user if changes to image 
	 * is to be saved. If changes are not to be saved, the isImageSaved so 
	 * that the user can then continue to open another image for editing.
	 */
	public void discardImage() {
		try { 
			String title = getString(R.string.messagebox_saveImage_title),
					message = getString(R.string.messagebox_saveImage_message),
					posButton = getString(R.string.messagebox_saveImage_posButton),
					negButton = getString(R.string.messagebox_saveImage_negButton);
			//create a Dialog object of AlertDialog
			AlertDialog.Builder warningBox = new AlertDialog.Builder(this);
			//set title and message to be displayed
			warningBox.setTitle(title);
			warningBox.setMessage(message);
			//add a listener to the OK button
			warningBox.setPositiveButton(posButton, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					saveImage();						
				} //onClick
			});
			//create a listener for the CANCEL button
			warningBox.setNegativeButton(negButton, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					//the user doesn't want to save changes
					_isImageSaved = true;			
				} //onClick
			});
			//show the inputbox
			warningBox.show();
		} catch (Exception ex) {
			String errorMsg = "Error:\n";
			Toast.makeText(this, errorMsg + ex.toString(), Toast.LENGTH_LONG).show();
		} //try/catch
	} //discardImage

	/**
	 * Clears the screen by removing the gridlayout, 
	 * emptying the content in imageview, sets bitmap 
	 * to null and sets the visibility of tvDefaultText 
	 * to true
	 */
	private void clearStartScreen() {
		//remove grid, clear imageview and show default text
		if (_isImageLoaded) 
			removeGrid();
		_isImageLoaded = false;
		if (imgView.getDrawable() != null) {
			imgView.setImageResource(0);
		 	_bitmap = null;
		} //if
		_colourList.clear();
		tvDefaultText.setVisibility(VISIBLE);
	} //clearStartScreen

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
			if (imgView.getDrawable() != null)
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