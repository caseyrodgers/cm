package hotmath.gwt.shared.client.model;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc.client.model.program_listing.CmTreeNode;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.shared.client.model.CCSSData.Level.Domain.Standard.Lesson;

public class CCSSData implements CmTreeNode, Response {

	private static final long serialVersionUID = -5793823416934583310L;

	private String name;
	private List<CCSSData.Level> levels = new ArrayList<Level>();

	public static final int ROOT     = 0;
	public static final int GRADE    = 1;
	public static final int DOMAIN   = 2;
 	public static final int STANDARD = 3;
 	public static final int LESSON   = 4;

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

	@Override
	public int getLevel() {
		return ROOT;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public CmTreeNode getParent() {
		return null;
	}

	public static class Level implements CmTreeNode {
		protected String name;
		protected CmTreeNode parent;
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

		@Override
		public int getLevel() {
			return GRADE;
		}

		@Override
		public String getLabel() {
			return name;
		}

		@Override
		public CmTreeNode getParent() {
			return parent;
		}

		public void setParent(CmTreeNode parent) {
			this.parent = parent;;
		}

		public static class Domain implements CmTreeNode {
			protected String name;
			protected CmTreeNode parent;
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

			@Override
			public int getLevel() {
				return DOMAIN;
			}

			@Override
			public String getLabel() {
				return name;
			}

			@Override
			public CmTreeNode getParent() {
				return parent;
			}

			public void setParent(CmTreeNode parent) {
				this.parent = parent;;
			}

			public static class Standard implements CmTreeNode {
				protected String originalName;
				protected String name;
				protected String summary;
				protected String description;
				protected CmTreeNode parent;

				protected List<CCSSData.Level.Domain.Standard.Lesson> lessons = new ArrayList<Lesson>();

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

				@Override
				public int getLevel() {
					return STANDARD;
				}

				@Override
				public String getLabel() {
					return summary;
				}

				public List<Lesson> getLessons() {
					return lessons;
				}

				@Override
				public CmTreeNode getParent() {
					return parent;
				}

				public void setParent(CmTreeNode parent) {
					this.parent = parent;;
				}

				public static class Lesson implements CmTreeNode {

				    protected String name;
				    protected String file;
					protected CmTreeNode parent;

					public Lesson(String name, String file) {
						this.name = name;
						this.file = file;
					}

					public String getName() {
						return name;
					}

					public void setName(String name) {
						this.name = name;
					}

					public String getFile() {
						return file;
					}

					public void setFile(String file) {
						this.file = file;
					}

					@Override
					public int getLevel() {
						return LESSON;
					}

					@Override
					public String getLabel() {
						return name;
					}

					@Override
					public CmTreeNode getParent() {
						return parent;
					}
					
					public void setParent(CmTreeNode parent) {
						this.parent = parent;;
					}
				}
			}
		}
	}
}
