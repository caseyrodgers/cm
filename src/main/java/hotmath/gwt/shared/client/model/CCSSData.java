package hotmath.gwt.shared.client.model;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSData implements Response {

	private static final long serialVersionUID = -5793823416934583310L;

	private String name;
	private List<CCSSData.Level> levels = new ArrayList<Level>();

	public CCSSData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CCSSData.Level> getLevels() {
		return levels;
	}

	public static class Level {
		protected String name;
		protected List<CCSSData.Level.Domain> domains = new ArrayList<Domain>();

		public Level() {}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<CCSSData.Level.Domain> getDomains() {
			return domains;
		}

		public static class Domain {
			protected String name;
			protected List<CCSSData.Level.Domain.Standard> standards = new ArrayList<Standard>();

			public void setName(String name) {
				this.name = name;
			}

			public String getName() {
				return name;
			}

			public List<CCSSData.Level.Domain.Standard> getStandards() {
				return standards;
			}
			
			public static class Standard {
				protected String originalName;
				protected String name;
				protected String summary;
				protected String description;

				public Standard(String name, String originalName, String summary, String description) {
					this.name = name;
					this.originalName = originalName;
					this.summary = summary;
					this.description = description;
				}

				public String getOriginalName() {
					return originalName;
				}

				public void setOriginalName(String originalName) {
					this.originalName = originalName;
				}

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				public String getSummary() {
					return summary;
				}

				public void setSummary(String summary) {
					this.summary = summary;
				}

				public String getDescription() {
					return description;
				}

				public void setDescription(String description) {
					this.description = description;
				}
			}
		}

	}

}
