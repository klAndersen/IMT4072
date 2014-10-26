package macs.hig.imt4072_project;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * AsyncTask class that attemps to load an image and display it in an imageview. <br />
 * This class is largely based on tutorial from 
 * <a href="http://developer.android.com/training/displaying-bitmaps/index.html">AndroidDeveloper</a>.
 * @author Knut Lucas Andersen
 */
public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {	
	private static final int REQUIRED_HEIGHT = 350;
	private String imagePath = "";
	private static int _requiredWidth = 0;	
	private final WeakReference<ImageView> imageViewReference;

	/**
	 * Constructor for the image loading task
	 * @param imageView - ImageView: The imageview which the image is to be displayed
	 * @param requiredWidth - int: The width to use for the image in the imageview
	 */
	public ImageLoaderTask(ImageView imageView, int requiredWidth) {
		_requiredWidth = requiredWidth;
		//weakreference to ensure imageview gets garabage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
	} //constructor

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = null;
		//get the image path
		imagePath = params[0];
		//decode the image
		bitmap = decodeSelectedImageFromFilepath(imagePath, _requiredWidth, REQUIRED_HEIGHT);
		if (bitmap == null) {
			try {
				//check if passed value is a resource, and try to decode it
				Resources res = imageViewReference.get().getResources();
				bitmap = BitmapFactory.decodeResource(res, Integer.parseInt(imagePath));
			} catch(Exception ex) {
				ex.printStackTrace();
			} //try/catch
		} //if
		return bitmap; 
	} //doInBackground

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		//was the async task cancelled?
		if (isCancelled()) {
			bitmap = null;
		} //if
		//was the image loaded and is the imageview still valid?
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final ImageLoaderTask imageTask = getImageLoaderTask(imageView);
			if (this == imageTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
				StartApplication.fillArrayListWithColours(bitmap, imageView);
			} //if
		} //if
	} //onPostExecute

	/**
	 * Checks the current active asynctask (imageloading) and if the active tasks
	 * path isn't like the last selected imagepath, the asynctask get cancelled.
	 * @param imagePath - String: The imagepath to check
	 * @param imageView - ImageView: The resource imageview
	 * @return boolean: True - Task was cancelled or not associated with imageview, <br />
	 * False - The same work is already in progress
	 */
	public static boolean cancelPotentialWork(String imagePath, ImageView imageView) {
		//get the asynctask
		final ImageLoaderTask task = getImageLoaderTask(imageView);
		//is the asynctask active?
		if (task != null) {
			//get the asynctasks data
			final String path = task.imagePath;
			//is it loading another image then previously selected?
			if (path != imagePath) {
				//cancel the asynctask
				task.cancel(true);
			} else {
				//the same work is already in progress
				return false;
			} //if
		} //if
		//no task associated with the ImageView, or an existing task was cancelled
		return true;
	} //cancelPotentialWork

	/**
	 * Checks if the imageview has content, and creates an object of drawable, - 
	 * comparing it against AsyncDrawable. If it's an instance of AsyncDrawable, 
	 * the task is returned.
	 * @param imageView - ImageView: The resource imageview
	 * @return ImageLoaderTask: The active async imageloading task || null
	 */
	private static ImageLoaderTask getImageLoaderTask(ImageView imageView) {
		//does the imageview have content?
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				//retreive the task
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getImageLoaderTask();
			} //if
		} //if
		return null;
	} //getBitmapWorkerTask

	/**
	 * Checks if task is to be cancelled, and if its cancelled/not active, 
	 * it starts a new task loading selected image
	 * @param res - Resources:
	 * @param imagePath - String: The path to the selected image
	 * @param imageView - ImageView: The resource imageview
	 * @param placeholderImage - Bitmap: 
	 * @param requiredWidth - int:
	 */
	public static void loadSelectedImage(Resources res, String imagePath, ImageView imageView, 
			Bitmap placeholderImage, int requiredWidth) {
		try {
			//is the active asynctask to be cancelled?
			if (cancelPotentialWork(imagePath, imageView)) {
				//create a new task and drawable, and load the selected image
				final ImageLoaderTask task = new ImageLoaderTask(imageView, requiredWidth);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(res, placeholderImage, task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute(imagePath);
			} //if
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		} //try/catch
	} //loadSelectedImage

	/**
	 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 * @param options - BitmapFactory.Options
	 * @param reqWidth - int: The required width for the image
	 * @param reqHeight - int: The required height for the image
	 * @return int: inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		//For example, an image with resolution 2048x1536 that is decoded with an inSampleSize of 4 produces a bitmap of 
		//approximately 512x384. Loading this into memory uses 0.75MB rather than 12MB for the full image 
		//(assuming a bitmap configuration of ARGB_8888). 
		//Here’s a method to calculate a the sample size value based on a target width and height:
		//raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
		} //if
		return inSampleSize;
	} //calculateInSampleSize

	/**
	 * Setting the inJustDecodeBounds property to true while decoding avoids memory allocation, returning null for 
	 * the bitmap object but setting outWidth, outHeight and outMimeType. This technique allows you to read the dimensions 
	 * and type of the image data prior to construction (and memory allocation) of the bitmap.
	 * @param path - String: The path to the selected image
	 * @param reqWidth - int: The required width for the image
	 * @param reqHeight - int: The required height for the image
	 * @return {@link BitmapFactory#decodeFile(String, android.graphics.BitmapFactory.Options)}
	 */
	public static Bitmap decodeSelectedImageFromFilepath(String path, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	} //decodeSelectedImageFromFilepath

	/**
	 * A class that allows the AsyncTask to use the class BitMapDrawable for 
	 * drawing and loading images.
	 * @author Knut Lucas Andersen
	 */
	private static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<ImageLoaderTask> imageLoaderTaskReference;

		/**
		 * Constructor that creates a reference to the current active async image loading task
		 * @param res - Resources
		 * @param bitmap - Bitmap
		 * @param imageLoaderTask - ImageLoaderTask
		 */
		public AsyncDrawable(Resources res, Bitmap bitmap, ImageLoaderTask imageLoaderTask) {
			super(res, bitmap);
			imageLoaderTaskReference = new WeakReference<ImageLoaderTask>(imageLoaderTask);
		} //constructor

		/**
		 * Returns a reference to the current active task
		 * @return WeakReference to ImageLoaderTask 
		 */
		public ImageLoaderTask getImageLoaderTask() {
			return imageLoaderTaskReference.get();
		} //getImageLoaderTask
	} //AsyncDrawable
} //ImageLoaderTask