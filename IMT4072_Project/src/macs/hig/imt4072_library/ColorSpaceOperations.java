package macs.hig.imt4072_library;

//color space import
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;

/**
 * Class for color space operations.
 * @see 
 * <a href="http://docs.oracle.com/javase/8/docs/api/java/awt/color/ICC_ColorSpace.html">Java Docs: ICC_ColorSpace</a>
 */
public class ColorSpaceOperations extends ICC_ColorSpace {
	/** default generated serialization id*/
	private static final long serialVersionUID = -8655309726775540603L;
	
	public ColorSpaceOperations(ICC_Profile profile) {
		super(profile);
	} //constructor

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		return super.fromCIEXYZ(colorvalue);
	}

	@Override
	public float[] fromRGB(float[] rgbvalue) {
		return super.fromRGB(rgbvalue);
	}

	@Override
	public float getMaxValue(int component) {
		return super.getMaxValue(component);
	}

	@Override
	public float getMinValue(int component) {
		return super.getMinValue(component);
	}

	@Override
	public ICC_Profile getProfile() {
		return super.getProfile();
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		return super.toCIEXYZ(colorvalue);
	}

	@Override
	public float[] toRGB(float[] colorvalue) {
		return super.toRGB(colorvalue);
	}

	@Override
	public String getName(int arg0) {
		return super.getName(arg0);
	}

	@Override
	public int getNumComponents() {
		return super.getNumComponents();
	}

	@Override
	public int getType() {
		return super.getType();
	}

	@Override
	public boolean isCS_sRGB() {
		return super.isCS_sRGB();
	}
} //ColorSpaceOperations