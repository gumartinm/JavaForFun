package de.example.debezium.transformer;

import java.time.OffsetDateTime;

public class Base {
	private final String type;
	private final OffsetDateTime published;
	private final String schema;

	private Base(String type, OffsetDateTime published, String schema) {
		this.type = type;
		this.published = published;
		this.schema = schema;
	}

	public String getType() {
		return type;
	}

	public String getPublished() {
		return published.toString();
	}

	public String getSchema() {
		return schema;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String type;
		private OffsetDateTime published;
		private String schema;

		public Builder withType(String type) {
			this.type = type;
			return this;
		}

		public Builder withPublished(OffsetDateTime published) {
			this.published = published;
			return this;
		}

		public Builder withSchema(String schema) {
			this.schema = schema;
			return this;
		}

		public Base build() {
			return new Base(this.type, this.published, this.schema);
		}
	}
}
