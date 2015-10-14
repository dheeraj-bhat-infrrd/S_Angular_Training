package com.realtech.socialsurvey.core.entities;

/**
 * Holds different types of image details
 */
public class ImagesCollection {

	private ImageDetails normalImage;
	private ImageDetails thumbnailImage;

	public ImageDetails getNormalImage() {
		return normalImage;
	}

	public void setNormalImage(ImageDetails normalImage) {
		this.normalImage = normalImage;
	}

	public ImageDetails getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(ImageDetails thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

}
