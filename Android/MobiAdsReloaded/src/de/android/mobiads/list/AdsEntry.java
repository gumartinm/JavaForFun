package de.android.mobiads.list;

import android.graphics.Bitmap;


/**
 * Encapsulates information about an ads entry
 */
public final class AdsEntry {
	
	private final String title;
	private final String text;
	private final Bitmap icon;
	private final int idAd;
	private final String URL;
	private boolean readStatus;

	public AdsEntry(final String title, final String text, 
						final Bitmap icon, final int idAd, 
							final String URL, final boolean alreadyRead) {
		this.title = title;
		this.text = text;
		this.icon = icon;
		this.idAd = idAd;
		this.URL = URL;
		this.readStatus = alreadyRead;
	}

	/**
	 * @return Title of ads entry
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return Text of ads entry
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return Icon of this ads entry
	 */
	public Bitmap getIcon() {
		return icon;
	}

	/**
	 * @return Ad unique identifier of this ads entry
	 */
	public int getIdAd() {
		return idAd;
	}

	/**
	 * @return URL matching this ad.
	 */
	public String getURL() {
		return URL;
	}
	
	/**
	 * @return boolean true is already read false otherwise.
	 */
	public boolean isRead() {
		return readStatus;
	}
	
	/**
	 * 
	 * @param readStatus true is already read false otherwise
	 */
	public void setIsRead(boolean readStatus) {
		this.readStatus = readStatus;
	}
}
