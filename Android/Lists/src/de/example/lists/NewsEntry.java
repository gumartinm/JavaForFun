package de.example.lists;

import java.util.Date;

/**
 * Encapsulates information about a news entry
 */
public final class NewsEntry {
	
	private final String title;
	private final String author;
	private final Date postDate;
	private final int icon;

	public NewsEntry(final String title, final String author, 
			final Date postDate, final int icon) {
		this.title = title;
		this.author = author;
		this.postDate = postDate;
		this.icon = icon;
	}

	/**
	 * @return Title of news entry
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return Author of news entry
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return Post date of news entry
	 */
	public Date getPostDate() {
		return postDate;
	}

	/**
	 * @return Icon of this news entry
	 */
	public int getIcon() {
		return icon;
	}

}
